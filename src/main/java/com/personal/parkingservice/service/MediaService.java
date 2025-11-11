package com.personal.parkingservice.service;

import java.util.List;
import java.util.UUID;

import com.personal.parkingservice.entity.Media;

public interface MediaService {
    Media create(Media media);

    List<Media> getAll();

    Media getById(UUID id);

    Media update(UUID id, Media updatedMedia);

    void delete(UUID id);
}
