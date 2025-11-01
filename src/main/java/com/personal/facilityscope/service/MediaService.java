package com.personal.facilityscope.service;

import com.personal.facilityscope.entity.Media;

import java.util.List;
import java.util.UUID;

public interface MediaService {
    Media create(Media media);
    List<Media> getAll();
    Media getById(UUID id);
    Media update(UUID id, Media updatedMedia);
    void delete(UUID id);
}
