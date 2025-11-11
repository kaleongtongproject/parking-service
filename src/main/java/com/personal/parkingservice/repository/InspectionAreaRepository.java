package com.personal.parkingservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.personal.parkingservice.entity.InspectionArea;

import java.util.List;
import java.util.UUID;

public interface InspectionAreaRepository extends JpaRepository<InspectionArea, UUID> {
    List<InspectionArea> findByPropertyId(UUID propertyId);
}
