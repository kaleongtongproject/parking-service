package com.personal.parkingservice.dto;

import java.sql.Timestamp;
import java.util.UUID;

import com.personal.parkingservice.enums.FlightStatus;

public class FlightResponseDTO {

    private UUID id;
    private UUID inspectionAreaId;
    private Timestamp flightDate;
    private String droneOperator;
    private String notes;
    private Timestamp createdAt;
    private FlightStatus status;

    // Getters and Setters

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public UUID getInspectionAreaId() {
        return inspectionAreaId;
    }

    public void setInspectionAreaId(UUID inspectionAreaId) {
        this.inspectionAreaId = inspectionAreaId;
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

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }

    public FlightStatus getStatus() {
        return status;
    }

    public void setStatus(FlightStatus status) {
        this.status = status;
    }
}
