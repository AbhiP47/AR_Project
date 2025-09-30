package com.ARProject.imageEnhancementService.Service;


import com.ARProject.imageEnhancementService.Helper.MultipartInputStreamFileResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.Map;
import java.util.TreeMap;

@Service
public class ImageEnhancementService {

    @Value("${cloudinary.cloud_name}")
    private String CLOUD_NAME;
    @Value("${cloudinary.api_key}")
    private  String API_KEY ;
    @Value("${cloudinary.api_secret}")
    private  String API_SECRET ;

    @Autowired
    private RestTemplate restTemplate;

    private static final String CLOUDINARY_UPLOAD_URL = "https://api.cloudinary.com/v1_1/%s/image/upload";

    public String uploadAndEnhanceImage(MultipartFile file) throws Exception {
        String url = String.format(CLOUDINARY_UPLOAD_URL, CLOUD_NAME);

        // Prepare parameters
        Map<String, String> params = new TreeMap<>();
        params.put("timestamp", String.valueOf(System.currentTimeMillis() / 1000));
        params.put("api_key", API_KEY);
        params.put("transformation", "q_auto,e_sharpen");

        // LOG: Print prepared params
        System.out.println("Cloudinary Params: " + params);

        // Generate signature for authentication
        String signature = generateSignature(params);

        // LOG: Print signature string before hashing and result
        StringBuilder toSign = new StringBuilder();
        params.entrySet().forEach(entry -> toSign.append(entry.getKey()).append("=").append(entry.getValue()).append("&"));
        if (toSign.length() > 0) toSign.setLength(toSign.length() - 1);
        toSign.append(API_SECRET);
        System.out.println("Signature string (pre-hash): " + toSign);
        System.out.println("Signature hash: " + signature);

        params.put("signature", signature);

        // Prepare multipart/form-data request
        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        for (Map.Entry<String, String> entry : params.entrySet()) {
            body.add(entry.getKey(), entry.getValue());
        }
        body.add("file", new MultipartInputStreamFileResource(file.getInputStream(), file.getOriginalFilename()));

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);

        HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);

        try {
            ResponseEntity<Map> response = restTemplate.postForEntity(url, requestEntity, Map.class);

            // LOG: Print response status code and body
            System.out.println("Cloudinary Response Status: " + response.getStatusCode());
            System.out.println("Cloudinary Response Body: " + response.getBody());

            Map responseBody = response.getBody();
            if (responseBody != null && responseBody.get("secure_url") != null)
                return responseBody.get("secure_url").toString();

            throw new RuntimeException("Upload failed (no secure_url in response)");
        } catch (Exception e) {
            // LOG: Print error and details from response if possible
            System.err.println("Cloudinary upload failed: " + e.getMessage());
            if (e instanceof org.springframework.web.client.HttpClientErrorException) {
                System.err.println("Raw Cloudinary error: " + ((org.springframework.web.client.HttpClientErrorException)e).getResponseBodyAsString());
            }
            throw e;
        }
    }
    private String generateSignature(Map<String, String> params) throws Exception {
        // Build signature string in lex order ignoring api_key and signature itself
        StringBuilder toSign = new StringBuilder();
        params.entrySet().stream()
                .filter(entry -> !entry.getKey().equals("api_key") && !entry.getKey().equals("signature"))
                .forEach(entry -> toSign.append(entry.getKey()).append("=").append(entry.getValue()).append("&"));

        if (toSign.length() > 0) toSign.setLength(toSign.length() - 1);
        toSign.append(API_SECRET);

        MessageDigest md = MessageDigest.getInstance("SHA-1");
        byte[] digest = md.digest(toSign.toString().getBytes(StandardCharsets.UTF_8));

        StringBuilder sb = new StringBuilder();
        for (byte b : digest) {
            sb.append(String.format("%02x", b));
        }
        return sb.toString();
    }
}
