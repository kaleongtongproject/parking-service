package com.personal.facilityscope.repository;

import com.personal.facilityscope.entity.Flight;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface FlightRepository extends JpaRepository<Flight, UUID> {
    List<Flight> findByInspectionAreaId(UUID inspectionAreaId);
}
