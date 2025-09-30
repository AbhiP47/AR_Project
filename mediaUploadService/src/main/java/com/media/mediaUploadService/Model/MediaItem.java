package com.media.mediaUploadService.Model;


import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.UUID;

@Document(collection = "media")
public class MediaItem {
    @Id
    private String id = UUID.randomUUID().toString();             // same as Pinecone ID
    @Indexed
    private String name;           // human-readable name
    private String imageUrl;
    private String videoUrl;
    private String model3dUrl;

    public void setId(String id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public void setVideoUrl(String videoUrl) {
        this.videoUrl = videoUrl;
    }

    public void setModel3dUrl(String model3dUrl) {
        this.model3dUrl = model3dUrl;
    }
}
