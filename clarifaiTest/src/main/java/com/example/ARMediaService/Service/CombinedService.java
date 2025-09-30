package com.example.ARMediaService.Service;

import com.example.ARMediaService.Model.MediaItem;
import com.example.ARMediaService.Repository.MediaRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.Collections;
import java.util.List;

@Service
public class CombinedService {

    private final ClarifaiEmbeddingService clarifaiEmbeddingService;
    private final VectorDBService vectorDBService;
    private final MediaLookUpService mediaLookUpService;
    private final MediaRepository mediaRepository;

    public CombinedService(ClarifaiEmbeddingService clarifaiEmbeddingService,
                           VectorDBService vectorDBService,
                           MediaLookUpService mediaLookUpService,
                           MediaRepository mediaRepository) {
        this.clarifaiEmbeddingService = clarifaiEmbeddingService;
        this.vectorDBService = vectorDBService;
        this.mediaLookUpService = mediaLookUpService;
        this.mediaRepository = mediaRepository;
    }

    // Method now returns a List<MediaItem> (multiple nearest neighbors)
    public List<MediaItem> findNearestMedia(MultipartFile file, int topK) throws Exception {
        // Convert image to embedding vector
        byte[] imageBytes = file.getBytes();
        List<Float> embedding = clarifaiEmbeddingService.getImageEmbedding(imageBytes);

        // Get nearest neighbor IDs from Pinecone
        List<String> nearestNeighborIds = vectorDBService.queryNearestNeighbors(embedding, topK);

        // If no neighbors found, return empty list
        if (nearestNeighborIds == null || nearestNeighborIds.isEmpty()) {
            return Collections.emptyList();
        }

        return mediaLookUpService.getMediaUrlsByIdList(nearestNeighborIds);
    }

    // Fetch media metadata documents from MongoDB by these IDs
    public List<MediaItem> getMediaByName(String name) {
        return mediaRepository.findByNameIgnoreCase(name);
    }
}
