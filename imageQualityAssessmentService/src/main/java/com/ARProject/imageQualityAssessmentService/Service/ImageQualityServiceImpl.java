package com.ARProject.imageQualityAssessmentService.Service;

import nu.pattern.OpenCV;
import org.opencv.core.*;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import org.springframework.stereotype.Service;


import java.util.ArrayList;
import java.util.List;

@Service
public class ImageQualityServiceImpl implements ImageQualityService {

    static {
        OpenCV.loadLocally();
    }

    @Override
    public double calculateBlurScore(byte[] imageBytes) {
        Mat img = Imgcodecs.imdecode(new MatOfByte(imageBytes), Imgcodecs.IMREAD_GRAYSCALE);
        if (img.empty()) {
            throw new IllegalArgumentException("Invalid image");
        }

        // Optional: resize for speed and consistency
        Imgproc.resize(img, img, new Size(500, 500));

        double volScore = calculateVarianceOfLaplacian(img);
        double edgeDensityScore = calculateEdgeDensity(img);
        double frequencyScore = calculateFrequencyScore(img);

        // Combine scores weighted by empirical importance
        double combinedScore = 0.5 * volScore + 0.3 * edgeDensityScore + 0.2 * frequencyScore;

        return combinedScore;
    }

    // Variance of Laplacian (defocus blur indicator)
    private double calculateVarianceOfLaplacian(Mat image) {
        Mat laplacian = new Mat();
        Imgproc.Laplacian(image, laplacian, CvType.CV_64F);

        MatOfDouble mean = new MatOfDouble();
        MatOfDouble stddev = new MatOfDouble();
        org.opencv.core.Core.meanStdDev(laplacian, mean, stddev);

        double variance = Math.pow(stddev.get(0, 0)[0], 2);

        laplacian.release();
        mean.release();
        stddev.release();

        return variance;
    }

    // Edge Density (counts strong edges - low in motion blur)
    private double calculateEdgeDensity(Mat image) {
        Mat edges = new Mat();
        Imgproc.Canny(image, edges, 100, 200);

        int edgePixels = org.opencv.core.Core.countNonZero(edges);
        double density = (double) edgePixels / (image.rows() * image.cols());

        edges.release();

        return density * 10000; // scaled for comparable magnitude
    }

    // Frequency Score (FFT low freq energy ratio - blurred images lack high freq)
    private double calculateFrequencyScore(Mat image) {
        Mat padded = new Mat();
        int m = Core.getOptimalDFTSize(image.rows());
        int n = Core.getOptimalDFTSize(image.cols());

        Core.copyMakeBorder(image, padded, 0, m - image.rows(), 0, n - image.cols(), Core.BORDER_CONSTANT, Scalar.all(0));

        List<Mat> planes = new ArrayList<>();
        padded.convertTo(padded, CvType.CV_32F);
        planes.add(padded);
        planes.add(Mat.zeros(padded.size(), CvType.CV_32F));

        Mat complexImage = new Mat();
        Core.merge(planes, complexImage);

        Core.dft(complexImage, complexImage);

        // compute magnitude
        Core.split(complexImage, planes);
        Mat mag = new Mat();
        Core.magnitude(planes.get(0), planes.get(1), mag);

        // switch quadrants
        shiftDFT(mag);

        Core.add(mag, Scalar.all(1), mag);
        Core.log(mag, mag);

        // calculate high frequency energy ratio
        Rect lowFreqRect = new Rect(0, 0, mag.cols() / 4, mag.rows() / 4);
        Mat lowFreq = new Mat(mag, lowFreqRect);
        double lowEnergy = Core.sumElems(lowFreq).val[0];
        double totalEnergy = Core.sumElems(mag).val[0];

        return (totalEnergy - lowEnergy) / totalEnergy * 10000; // scaled
    }

    // Helper to swap quadrants of Fourier image for better view - needed for freq score
    private void shiftDFT(Mat mag) {
        int cx = mag.cols() / 2;
        int cy = mag.rows() / 2;

        Mat q0 = new Mat(mag, new Rect(0, 0, cx, cy));       // Top-Left
        Mat q1 = new Mat(mag, new Rect(cx, 0, cx, cy));      // Top-Right
        Mat q2 = new Mat(mag, new Rect(0, cy, cx, cy));      // Bottom-Left
        Mat q3 = new Mat(mag, new Rect(cx, cy, cx, cy));     // Bottom-Right

        Mat tmp = new Mat();

        q0.copyTo(tmp);
        q3.copyTo(q0);
        tmp.copyTo(q3);

        q1.copyTo(tmp);
        q2.copyTo(q1);
        tmp.copyTo(q2);
    }
}
