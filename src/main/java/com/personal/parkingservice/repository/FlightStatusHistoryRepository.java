package com.personal.parkingservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.personal.parkingservice.entity.FlightStatusHistory;

import java.util.List;
import java.util.UUID;

public interface FlightStatusHistoryRepository extends JpaRepository<FlightStatusHistory, UUID> {
    List<FlightStatusHistory> findByFlightIdOrderByChangedAtDesc(UUID flightId);
}