package com.media.mediaUploadService.Controller;

import com.media.mediaUploadService.Service.ImageIngestionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/ingest")
public class ImageIngestController {
    @Autowired
    private ImageIngestionService imageIngestionService;

    @PostMapping("/upload-folder")
    public ResponseEntity<String> uploadImagesFromFolder(@RequestParam String folderPath) {
        try {
            imageIngestionService.ingestImagesFromFolder(folderPath);
            return ResponseEntity.ok("Images ingested and uploaded to Pinecone successfully.");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body("Failed to ingest images: " + e.getMessage());
        }
    }
}
