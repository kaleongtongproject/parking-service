package com.personal.parkingservice.entity;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;

import com.personal.parkingservice.enums.FlightStatus;

import java.sql.Timestamp;
import java.util.UUID;

@Entity
public class FlightStatusHistory {

    @Id
    @GeneratedValue
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "flight_id", nullable = false)
    private Flight flight;

    @Column(name = "status", nullable = false)
    @Enumerated(EnumType.STRING)
    private FlightStatus status;

    @CreationTimestamp
    private Timestamp changedAt;

    // Getters and Setters
    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public Flight getFlight() {
        return flight;
    }

    public void setFlight(Flight flight) {
        this.flight = flight;
    }

    public FlightStatus getStatus() {
        return status;
    }

    public void setStatus(FlightStatus status) {
        this.status = status;
    }

    public Timestamp getChangedAt() {
        return changedAt;
    }

    public void setChangedAt(Timestamp changedAt) {
        this.changedAt = changedAt;
    }
}
