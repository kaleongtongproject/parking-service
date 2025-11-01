package com.personal.facilityscope.service.impl;

import com.personal.facilityscope.entity.Media;
import com.personal.facilityscope.repository.MediaRepository;
import com.personal.facilityscope.service.MediaService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class MediaServiceImpl implements MediaService {

    private final MediaRepository mediaRepository;

    public MediaServiceImpl(MediaRepository mediaRepository) {
        this.mediaRepository = mediaRepository;
    }

    @Override
    public Media create(Media media) {
        return mediaRepository.save(media);
    }

    @Override
    public List<Media> getAll() {
        return mediaRepository.findAll();
    }

    @Override
    public Media getById(UUID id) {
        return mediaRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Media not found"));
    }

    @Override
    public Media update(UUID id, Media updatedMedia) {
        Media existing = getById(id);
        return mediaRepository.save(updatedMedia);
    }

    @Override
    public void delete(UUID id) {
        mediaRepository.deleteById(id);
    }
}
