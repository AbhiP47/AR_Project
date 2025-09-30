package com.example.ARMediaService.Repository;

import com.example.ARMediaService.Model.MediaItem;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
    public interface MediaRepository extends MongoRepository<MediaItem, String> {
        List<MediaItem> findByNameIgnoreCase(String name);
    }

