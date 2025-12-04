package com.personal.parkingservice.dto;

import java.util.UUID;

public record StartSessionRequestDTO(
        UUID userId,
        Long spotId) {
}