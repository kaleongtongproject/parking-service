package com.personal.parkingservice.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.Instant;
import java.util.UUID;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaymentTransaction {

    @Id
    @GeneratedValue
    private UUID id;

    @Column(nullable = false, unique = true)
    private String paymentIntentId;

    @Column(nullable = false)
    private UUID sessionId;

    private long amountCents;

    private String status;
    private Instant createdAt;
    private Instant updatedAt;
}
