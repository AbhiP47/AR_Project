package com.ARProject.imageQualityAssessmentService;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
public class ImageQualityAssessmentServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(ImageQualityAssessmentServiceApplication.class, args);
	}

}
