package com.personal.facilityscope.dto;

import com.personal.facilityscope.enums.FlightStatus;
import java.sql.Timestamp;
import java.util.UUID;

public class FlightstatusHistoryResponseDTO {
    private UUID id;
    private UUID flightId;
    private FlightStatus status;
    private Timestamp changedAt;

    // Getters and setters
    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public UUID getFlightId() {
        return flightId;
    }

    public void setFlightId(UUID flightId) {
        this.flightId = flightId;
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
