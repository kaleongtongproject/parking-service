package com.personal.parkingservice.dto;

import java.time.Instant;
import java.util.UUID;

public record StartSessionResponseDTO(
        UUID sessionId,
        UUID userId,
        Long spotId,
        Instant checkinTs,
        String status) {
}
