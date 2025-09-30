package com.example.ARMediaService.Controller;

import com.example.ARMediaService.Model.MediaItem;
import com.example.ARMediaService.Repository.MediaRepository;
import com.example.ARMediaService.Service.CombinedService;
import com.example.ARMediaService.Service.MediaService;
import com.example.ARMediaService.Service.VectorDBService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

@RestController
@RequestMapping("/api/media")
public class MediaController {

    private final MediaService mediaService;
    private final VectorDBService vectorDBService;
    private final CombinedService combinedService;
    private final MediaRepository mediaRepository;

    public MediaController(MediaService mediaService ,
                           VectorDBService vectorDBService,
                           CombinedService combinedService ,
                           MediaRepository mediaRepository
    ) {
        this.mediaService = mediaService;
        this.vectorDBService = vectorDBService;
        this.combinedService = combinedService;
        this.mediaRepository = mediaRepository;
    }

    // Endpoint for uploading image file and returning its embedding vector as JSON response
    @PostMapping(value = "/embedding", consumes = "multipart/form-data")
    public ResponseEntity<List<Float>> getImageEmbedding(@RequestParam("file") MultipartFile file) throws IOException {
        List<Float> embedding = mediaService.convertImageToEmbedding(file);
        return ResponseEntity.ok(embedding);
    }

    // Endpoint to search nearest neighbors via vector DB
    @PostMapping(value="/searchNeighbor" , consumes = "multipart/form-data")
    public ResponseEntity<List<String>> searchSimilarImage(@RequestParam("file") MultipartFile file) throws Exception {
        List<Float> embedding = mediaService.convertImageToEmbedding(file);
        List<String> nearestNeighbors = vectorDBService.queryNearestNeighbors(embedding, 1);
        return ResponseEntity.ok(nearestNeighbors);
    }

    /**
     * POST /api/media/search
     * Accepts a multipart file (image) uploaded by the user.
     * Steps:
     * - Generates embedding vector for the uploaded image using Clarifai.
     * - Queries Pinecone vector DB for nearest neighbor IDs.
     * - Looks up media metadata URLs from MongoDB for those neighbor IDs.
     * Returns: List of MediaItem objects containing URLs of relevant media.
     */
    @PostMapping(value = "/search", consumes = "multipart/form-data")
    public ResponseEntity<List<MediaItem>> searchSimilarMedia(@RequestParam("file") MultipartFile file) {
        try {
            // Find nearest media items and associated URLs
            List<MediaItem> mediaItems = combinedService.findNearestMedia(file, 5);

            // Return list of nearest media metadata
            return ResponseEntity.ok(mediaItems);
        } catch (Exception e) {
            e.printStackTrace();
            // On error, return empty list with 500 status
            return ResponseEntity.status(500).body(Collections.emptyList());
        }
    }

    // Find the URLs by name
    public List<MediaItem> getMediaByName(String name) {
        return mediaRepository.findByNameIgnoreCase(name);
    }
}
