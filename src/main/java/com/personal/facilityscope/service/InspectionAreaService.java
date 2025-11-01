package com.personal.facilityscope.service;

import com.personal.facilityscope.dto.InspectionAreaRequestDTO;
import com.personal.facilityscope.entity.InspectionArea;

import java.util.List;
import java.util.UUID;

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