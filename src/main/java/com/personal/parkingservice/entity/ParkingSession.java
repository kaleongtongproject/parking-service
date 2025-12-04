package com.personal.parkingservice.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "parking_session")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ParkingSession {
    @Id
    private UUID id;
    private UUID userId;
    private Long spotId;
    private Instant checkinTs;
    private Instant checkoutTs;
    private String status;
    private String pricingVersion;
    private Long totalAmountCents;
    private String paymentIntentId;
    private String licensePlate;
}