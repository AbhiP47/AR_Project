package com.ARProject.imageQualityAssessmentService.Model;

public class ImageQuality {
    private double blurScore;

    public ImageQuality(double blurScore)
    {
        this.blurScore = blurScore;
    }

    public double getBlurScore()
    {
        return blurScore;
    }

    public void setBlurScore(double blurScore)
    {
        this.blurScore = blurScore;
    }
}
