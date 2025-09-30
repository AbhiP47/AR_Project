package com.media.mediaUploadService.Model;

import java.util.List;
import java.util.Map;

// POJO class to hold vector data for upsert
public class VectorData {
    private String id;
    private List<Float> values;
    private Map<String, Object> metadata;

    public VectorData(String id, List<Float> values, Map<String, Object> metadata) {
        this.id = id;
        this.values = values;
        this.metadata = metadata;
    }

    public String getId() { return id; }
    public List<Float> getValues() { return values; }
    public Map<String, Object> getMetadata() { return metadata; }

    public void setId(String id) { this.id = id; }
    public void setValues(List<Float> values) { this.values = values; }
    public void setMetadata(Map<String, Object> metadata) { this.metadata = metadata; }
}