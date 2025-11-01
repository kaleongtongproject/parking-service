package com.personal.facilityscope.repository;

import com.personal.facilityscope.entity.FlightStatusHistory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface FlightStatusHistoryRepository extends JpaRepository<FlightStatusHistory, UUID> {
    List<FlightStatusHistory> findByFlightIdOrderByChangedAtDesc(UUID flightId);
}