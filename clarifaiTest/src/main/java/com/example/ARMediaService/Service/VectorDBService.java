package com.example.ARMediaService.Service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;

@Service
public class VectorDBService {

    @Value("${pinecone.api_key}")
    private String pineconeApiKey;

    @Value("${pinecone.host}")
    private String pineconeHost;

    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper mapper = new ObjectMapper();

    public List<String> queryNearestNeighbors(List<Float> vector, int topK) throws Exception {
        String url = pineconeHost + "/query";

        // Build JSON request body
        String payload = mapper.createObjectNode()
                .put("topK", topK)
                .put("includeMetadata", true)
                .put("includeValues", false)
                .set("vector", mapper.valueToTree(vector))
                .toString();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Api-Key", pineconeApiKey);

        HttpEntity<String> requestEntity = new HttpEntity<>(payload, headers);

        // Execute POST request to Pinecone
        ResponseEntity<JsonNode> responseEntity = restTemplate.exchange(
                url,
                HttpMethod.POST,
                requestEntity,
                JsonNode.class);

        JsonNode matchesNode = responseEntity.getBody().get("matches");
        List<String> ids = new ArrayList<>();

        if (matchesNode != null && matchesNode.isArray()) {
            for (JsonNode match : matchesNode) {
                ids.add(match.get("id").asText());
            }
        }

        return ids;
    }
}
