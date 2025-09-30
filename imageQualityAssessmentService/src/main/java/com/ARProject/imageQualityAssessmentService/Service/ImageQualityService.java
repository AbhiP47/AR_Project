package com.ARProject.imageQualityAssessmentService.Service;

public interface ImageQualityService {
    /**
     * Calculates the blur score of an image represented as a byte array.
     * @param imageBytes The image data in bytes.
     * @return double representing the blur score (variance of Laplacian).
     * @throws IllegalArgumentException if image is invalid or unreadable.
     */

    double calculateBlurScore(byte[] imageBytes);
}
