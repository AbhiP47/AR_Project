package com.media.mediaUploadService.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.media.mediaUploadService.Model.VectorData;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

@Service
public class PineconeUpsertService {

    @Value("${pinecone.api_key}")
    private String apiKey;

    @Value("${pinecone.host}")
    private String pineconeHost;

    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();

    // Upsert multiple vectors with metadata to Pinecone
    public void upsertVectors(List<VectorData> vectors) throws Exception {
        String url = pineconeHost + "/vectors/upsert";

        ObjectNode root = objectMapper.createObjectNode();
        ArrayNode vectorsNode = objectMapper.createArrayNode();

        for (VectorData v : vectors) {
            ObjectNode vectorNode = objectMapper.createObjectNode();
            vectorNode.put("id", v.getId());
            vectorNode.set("values", objectMapper.valueToTree(v.getValues()));

            if (v.getMetadata() != null) {
                vectorNode.set("metadata", objectMapper.valueToTree(v.getMetadata()));
            }
            vectorsNode.add(vectorNode);
        }

        root.set("vectors", vectorsNode);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Api-Key", apiKey);

        HttpEntity<String> request = new HttpEntity<>(root.toString(), headers);

        ResponseEntity<String> response = restTemplate.postForEntity(url, request, String.class);

        if (!response.getStatusCode().is2xxSuccessful()) {
            throw new RuntimeException("Pinecone upsert failed: " + response.getBody());
        }
    }


}
