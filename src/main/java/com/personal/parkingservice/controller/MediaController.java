package com.personal.parkingservice.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.personal.parkingservice.entity.Media;
import com.personal.parkingservice.service.MediaService;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/media")
public class MediaController {

    private final MediaService mediaService;

    public MediaController(MediaService mediaService) {
        this.mediaService = mediaService;
    }

    @PostMapping
    public ResponseEntity<Media> create(@RequestBody Media media) {
        return ResponseEntity.ok(mediaService.create(media));
    }

    @GetMapping
    public ResponseEntity<List<Media>> getAll() {
        return ResponseEntity.ok(mediaService.getAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Media> getById(@PathVariable UUID id) {
        return ResponseEntity.ok(mediaService.getById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Media> update(@PathVariable UUID id, @RequestBody Media updatedMedia) {
        return ResponseEntity.ok(mediaService.update(id, updatedMedia));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        mediaService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
