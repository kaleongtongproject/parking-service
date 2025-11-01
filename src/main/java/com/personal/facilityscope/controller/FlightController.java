package com.personal.facilityscope.controller;
import com.personal.facilityscope.dto.FlightstatusHistoryResponseDTO;
import com.personal.facilityscope.dto.FlightRequestDTO;
import com.personal.facilityscope.dto.FlightResponseDTO;
import com.personal.facilityscope.service.FlightService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/flights")
public class FlightController {

    private final FlightService flightService;

    public FlightController(FlightService flightService) {
        this.flightService = flightService;
    }

    @GetMapping("/{id}/status-history")
    public List<FlightstatusHistoryResponseDTO> getStatusHistory(@PathVariable UUID id) {
        return flightService.getStatusHistory(id);
    }

    @PostMapping
    public ResponseEntity<FlightResponseDTO> create(@RequestBody FlightRequestDTO dto) {
        FlightResponseDTO response = flightService.create(dto);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<List<FlightResponseDTO>> getAll() {
        return ResponseEntity.ok(flightService.getAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<FlightResponseDTO> getById(@PathVariable UUID id) {
        return ResponseEntity.ok(flightService.getById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<FlightResponseDTO> update(@PathVariable UUID id, @RequestBody FlightRequestDTO dto) {
        return ResponseEntity.ok(flightService.update(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        flightService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
