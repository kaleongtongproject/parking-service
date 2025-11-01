package com.personal.facilityscope.repository;

import com.personal.facilityscope.entity.InspectionArea;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface InspectionAreaRepository extends JpaRepository<InspectionArea, UUID> {
    List<InspectionArea> findByPropertyId(UUID propertyId);
}
