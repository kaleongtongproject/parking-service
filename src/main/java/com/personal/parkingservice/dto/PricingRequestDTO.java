package com.personal.parkingservice.dto;

import java.time.Instant;
import java.util.UUID;

public record PricingRequestDTO(UUID sessionId, Instant checkin, Instant checkout, Long lotId, String membership) {
}
