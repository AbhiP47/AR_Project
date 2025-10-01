package com.ARProject.imageQualityAssessmentService.Controller;

import com.ARProject.imageQualityAssessmentService.Model.ImageQuality;
import com.ARProject.imageQualityAssessmentService.Service.ImageQualityServiceImpl;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("api/imagescore")
public class ImageQualityController {

    private final ImageQualityServiceImpl imageQualityService;

    public ImageQualityController(ImageQualityServiceImpl imageQualityService)
    {
        this.imageQualityService = imageQualityService;
    }

    @PostMapping("/blurScore")
    public ResponseEntity<ImageQuality> getBlurScore(@RequestParam("file") MultipartFile file)
    {
        try{
            double score = imageQualityService.calculateBlurScore(file.getBytes());
            return ResponseEntity.ok(new ImageQuality(score));
        }
        catch(IllegalArgumentException e)
        {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }
    }
