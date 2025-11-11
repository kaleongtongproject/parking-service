package com.personal.parkingservice.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.personal.parkingservice.dto.InspectionAreaRequestDTO;
import com.personal.parkingservice.dto.InspectionAreaResponseDTO;
import com.personal.parkingservice.entity.InspectionArea;
import com.personal.parkingservice.mapper.InspectionAreaMapper;
import com.personal.parkingservice.service.InspectionAreaService;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/inspection-areas")
public class InspectionAreaController {

    private final InspectionAreaService inspectionAreaService;

    public InspectionAreaController(InspectionAreaService inspectionAreaService) {
        this.inspectionAreaService = inspectionAreaService;
    }

    @PostMapping
    public ResponseEntity<InspectionAreaResponseDTO> create(@RequestBody InspectionAreaRequestDTO dto) {
        InspectionArea area = inspectionAreaService.createFromDTO(dto);
        return ResponseEntity.ok(InspectionAreaMapper.toDto(area));
    }

    @GetMapping
    public ResponseEntity<List<InspectionAreaResponseDTO>> getAll() {
        List<InspectionArea> areas = inspectionAreaService.getAll();
        List<InspectionAreaResponseDTO> dtoList = areas.stream()
                .map(InspectionAreaMapper::toDto)
                .collect(Collectors.toList());
        return ResponseEntity.ok(dtoList);
    }

    @GetMapping("/{id}")
    public ResponseEntity<InspectionAreaResponseDTO> getById(@PathVariable UUID id) {
        InspectionArea area = inspectionAreaService.getById(id);
        return ResponseEntity.ok(InspectionAreaMapper.toDto(area));
    }

    @PutMapping("/{id}")
    public ResponseEntity<InspectionAreaResponseDTO> update(
            @PathVariable UUID id,
            @RequestBody InspectionAreaRequestDTO dto) {
        InspectionArea updatedArea = inspectionAreaService.updateFromDTO(id, dto);
        return ResponseEntity.ok(InspectionAreaMapper.toDto(updatedArea));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        inspectionAreaService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
