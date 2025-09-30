package com.media.mediaUploadService.Service;

import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.*;
import com.media.mediaUploadService.Model.VectorData;

@Service
public class ImageIngestionService {

    private final ClarifaiEmbeddingService clarifaiEmbeddingService;
    private final PineconeUpsertService pineconeUpsertService;

    @Autowired
    public ImageIngestionService(ClarifaiEmbeddingService clarifaiEmbeddingService, PineconeUpsertService pineconeUpsertService ) {
        this.clarifaiEmbeddingService = clarifaiEmbeddingService;
        this.pineconeUpsertService = pineconeUpsertService;
    }

    public void ingestImagesFromFolder(String folderPath) throws Exception {
        File folder = new File(folderPath);
        if (!folder.exists() || !folder.isDirectory()) {
            throw new IllegalArgumentException("Invalid folder path: " + folderPath);
        }

        List<VectorData> vectorsToUpsert = new ArrayList<>();

        for (File file : Objects.requireNonNull(folder.listFiles())) {
            if (file.isFile() && isSupportedImage(file.getName())) {
                // Read image bytes
                byte[] imageBytes = Files.readAllBytes(file.toPath());

                // Get embedding via Clarifai
                List<Float> embedding = clarifaiEmbeddingService.getImageEmbedding(imageBytes);

                // Create VectorData with file name as ID and URL metadata placeholder
                VectorData vectorData = new VectorData(
                        file.getName(),
                        embedding,
                        Map.of("url", "http://your-cloud-storage/" + file.getName()) // update with your real URLs
                );

                vectorsToUpsert.add(vectorData);
            }
        }

        // Upsert all vectors to Pinecone
        pineconeUpsertService.upsertVectors(vectorsToUpsert);
    }

    private boolean isSupportedImage(String fileName) {
        String lowerFile = fileName.toLowerCase();
        return lowerFile.endsWith(".jpg") || lowerFile.endsWith(".jpeg")
                || lowerFile.endsWith(".png") || lowerFile.endsWith(".webp");
    }
}
