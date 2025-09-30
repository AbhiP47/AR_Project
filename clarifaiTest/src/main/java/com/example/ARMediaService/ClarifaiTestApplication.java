package com.example.ARMediaService;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@EnableFeignClients
@SpringBootApplication
public class ClarifaiTestApplication {

	public static void main(String[] args) {
		SpringApplication.run(ClarifaiTestApplication.class, args);
	}

}
