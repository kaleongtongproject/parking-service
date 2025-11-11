package com.personal.parkingservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.personal.parkingservice.entity.Media;

import java.util.List;
import java.util.UUID;

public interface MediaRepository extends JpaRepository<Media, UUID> {
    List<Media> findByFlightId(UUID flightId);
}
