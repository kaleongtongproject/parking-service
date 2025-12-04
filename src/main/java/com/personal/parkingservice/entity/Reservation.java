package com.personal.parkingservice.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "reservation")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Reservation {
    @Id
    private UUID id;
    private UUID userId;
    private Long spotId;
    private Long lotId;
    private Instant arrivalTs;
    private Integer expectedDurationMinutes;
    private String status;
    private Long priceCents;
    private String paymentIntentId;
    private Instant createdAt;
}