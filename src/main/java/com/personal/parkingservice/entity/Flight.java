package com.personal.parkingservice.entity;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.GenericGenerator;

import com.personal.parkingservice.enums.FlightStatus;

import java.sql.Timestamp;
import java.util.UUID;

@Entity
public class Flight {

    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "inspection_area_id", nullable = false)
    private InspectionArea inspectionArea;

    @Column(nullable = false)
    private Timestamp flightDate;

    private String droneOperator;

    private String notes;

    @CreationTimestamp
    private Timestamp createdAt;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private FlightStatus status;

    public FlightStatus getStatus() {
        return status;
    }

    public void setStatus(FlightStatus status) {
        this.status = status;
    }

    // Getters and Setters
    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public InspectionArea getInspectionArea() {
        return inspectionArea;
    }

    public void setInspectionArea(InspectionArea inspectionArea) {
        this.inspectionArea = inspectionArea;
    }

    public Timestamp getFlightDate() {
        return flightDate;
    }

    public void setFlightDate(Timestamp flightDate) {
        this.flightDate = flightDate;
    }

    public String getDroneOperator() {
        return droneOperator;
    }

    public void setDroneOperator(String droneOperator) {
        this.droneOperator = droneOperator;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }
}
