package com.example.ARMediaService.Service;

import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

@Service
public class ClarifaiEmbeddingService {

    @Value("${clarifai.pat}")
    private String pat;

    @Value("${clarifai.user_id}")
    private String userId;

    @Value("${clarifai.app_id}")
    private String appId;

    public List<Float> getImageEmbedding(byte[] imageBytes) {
        RestTemplate restTemplate = new RestTemplate();

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Key " + pat);
        headers.setContentType(MediaType.APPLICATION_JSON);

        String base64Image = Base64.getEncoder().encodeToString(imageBytes);

        String payload = "{"
                + "\"user_app_id\": {"
                + "\"user_id\": \"" + userId + "\","
                + "\"app_id\": \"" + appId + "\""
                + "},"
                + "\"inputs\": [{"
                + "\"data\": {"
                + "\"image\": {"
                + "\"base64\": \"" + base64Image + "\""
                + "}"
                + "}"
                + "}]"
                + "}";

        HttpEntity<String> request = new HttpEntity<>(payload, headers);

        String url = "https://api.clarifai.com/v2/models/general-image-embedding/outputs";

        ResponseEntity<JsonNode> resp = restTemplate.postForEntity(url, request, JsonNode.class);

        JsonNode vector = resp.getBody().at("/outputs/0/data/embeddings/0/vector");
        List<Float> embedding = new ArrayList<>();
        if (vector.isArray()) {
            for (JsonNode node : vector) {
                embedding.add((float) node.asDouble());
            }
        }
        return embedding;
    }
}
