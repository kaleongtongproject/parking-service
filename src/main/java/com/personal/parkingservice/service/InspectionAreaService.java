package com.personal.parkingservice.service;

import java.util.List;
import java.util.UUID;

import com.personal.parkingservice.dto.InspectionAreaRequestDTO;
import com.personal.parkingservice.entity.InspectionArea;

public interface InspectionAreaService {
    InspectionArea create(InspectionArea inspectionArea);

    List<InspectionArea> getAll();

    InspectionArea getById(UUID id);

    InspectionArea update(UUID id, InspectionArea updatedInspectionArea);

    void delete(UUID id);

    // New method for DTO-based creation
    InspectionArea createFromDTO(InspectionAreaRequestDTO dto);

    InspectionArea updateFromDTO(UUID id, InspectionAreaRequestDTO dto);
}