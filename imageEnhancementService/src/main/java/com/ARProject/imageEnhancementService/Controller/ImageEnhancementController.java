package com.ARProject.imageEnhancementService.Controller;

import com.ARProject.imageEnhancementService.Service.ImageEnhancementService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

    @RestController
    @RequestMapping("/api/image")
    public class ImageEnhancementController {

        @Autowired
        private ImageEnhancementService imageEnhancementService;

        @PostMapping("/enhance")
        public ResponseEntity<String> enhance(@RequestParam("file") MultipartFile file) {
            try {
                String enhancedUrl = imageEnhancementService.uploadAndEnhanceImage(file);
                return ResponseEntity.ok(enhancedUrl);
            } catch (Exception e) {
                return ResponseEntity.status(500).body("Cloudinary upload failed: " + e.getMessage());
            }
        }
    }

