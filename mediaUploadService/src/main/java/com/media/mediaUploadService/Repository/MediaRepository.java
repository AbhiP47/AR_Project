package com.media.mediaUploadService.Repository;

import com.media.mediaUploadService.Model.MediaItem;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MediaRepository extends MongoRepository<MediaItem,String> {
}
