package com.personal.parkingservice.dto;

import java.sql.Timestamp;
import java.util.UUID;

import com.personal.parkingservice.enums.FlightStatus;

import jakarta.validation.constraints.NotNull;

public class FlightRequestDTO {

    private UUID inspectionAreaId;
    private Timestamp flightDate;
    private String droneOperator;
    private String notes;

    @NotNull(message = "Status is required")
    private FlightStatus status;

    // Getter and Setter
    public FlightStatus getStatus() {
        return status;
    }

    public void setStatus(FlightStatus status) {
        this.status = status;
    }
    // Getters and Setters

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
}
