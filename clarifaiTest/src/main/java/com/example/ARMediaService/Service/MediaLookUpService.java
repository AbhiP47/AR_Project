package com.example.ARMediaService.Service;

import com.example.ARMediaService.Model.MediaItem;
import com.example.ARMediaService.Repository.MediaRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class MediaLookUpService {
    private final MediaRepository mediaRepository;

    public MediaLookUpService(MediaRepository mediaRepository) {
        this.mediaRepository = mediaRepository;
    }

    // Find media URLs by Pinecone ID (which is same as MongoDB _id)
    public Optional<MediaItem> getMediaUrlsById(String pineconeId) {
        return mediaRepository.findById(pineconeId);
    }

    public List<MediaItem> getMediaUrlsByIdList(List<String> pineconeIds) {
        return mediaRepository.findAllById(pineconeIds);
    }
}
