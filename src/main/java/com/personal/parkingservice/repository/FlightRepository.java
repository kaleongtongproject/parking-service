package com.personal.parkingservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.personal.parkingservice.entity.Flight;

import java.util.List;
import java.util.UUID;

public interface FlightRepository extends JpaRepository<Flight, UUID> {
    List<Flight> findByInspectionAreaId(UUID inspectionAreaId);
}
