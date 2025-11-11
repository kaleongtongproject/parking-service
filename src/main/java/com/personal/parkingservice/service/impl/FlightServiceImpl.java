package com.personal.parkingservice.service.impl;

import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.personal.parkingservice.dto.FlightRequestDTO;
import com.personal.parkingservice.dto.FlightResponseDTO;
import com.personal.parkingservice.dto.FlightstatusHistoryResponseDTO;
import com.personal.parkingservice.entity.Flight;
import com.personal.parkingservice.entity.FlightStatusHistory;
import com.personal.parkingservice.entity.InspectionArea;
import com.personal.parkingservice.enums.FlightStatus;
import com.personal.parkingservice.repository.FlightRepository;
import com.personal.parkingservice.repository.FlightStatusHistoryRepository;
import com.personal.parkingservice.repository.InspectionAreaRepository;
import com.personal.parkingservice.service.FlightService;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class FlightServiceImpl implements FlightService {

    private final FlightRepository flightRepository;
    private final InspectionAreaRepository inspectionAreaRepository;
    private final FlightStatusHistoryRepository flightStatusHistoryRepository;

    @Autowired
    public FlightServiceImpl(
            FlightRepository flightRepository,
            InspectionAreaRepository inspectionAreaRepository,
            FlightStatusHistoryRepository flightStatusHistoryRepository) {
        this.flightRepository = flightRepository;
        this.inspectionAreaRepository = inspectionAreaRepository;
        this.flightStatusHistoryRepository = flightStatusHistoryRepository;
    }

    @Override
    @Transactional
    public FlightResponseDTO create(FlightRequestDTO dto) {
        InspectionArea area = inspectionAreaRepository.findById(dto.getInspectionAreaId())
                .orElseThrow(() -> new EntityNotFoundException("Inspection Area not found"));

        Flight flight = new Flight();
        flight.setStatus(FlightStatus.SCHEDULED);
        flight.setInspectionArea(area);
        flight.setFlightDate(dto.getFlightDate());
        flight.setDroneOperator(dto.getDroneOperator());
        flight.setNotes(dto.getNotes());

        Flight savedFlight = flightRepository.save(flight);

        logStatusChange(savedFlight, FlightStatus.SCHEDULED);

        return toDto(savedFlight);
    }

    @Override
    public List<FlightResponseDTO> getAll() {
        return flightRepository.findAll().stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public FlightResponseDTO getById(UUID id) {
        Flight flight = flightRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Flight not found"));
        return toDto(flight);
    }

    @Override
    @Transactional
    public FlightResponseDTO update(UUID id, FlightRequestDTO dto) {
        Flight flight = flightRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Flight not found"));

        InspectionArea area = inspectionAreaRepository.findById(dto.getInspectionAreaId())
                .orElseThrow(() -> new EntityNotFoundException("Inspection Area not found"));

        FlightStatus oldStatus = flight.getStatus();
        FlightStatus newStatus = dto.getStatus() != null ? dto.getStatus() : oldStatus;

        if (!newStatus.equals(oldStatus)) {
            logStatusChange(flight, newStatus);
        }

        flight.setStatus(newStatus);
        flight.setInspectionArea(area);
        flight.setFlightDate(dto.getFlightDate());
        flight.setDroneOperator(dto.getDroneOperator());
        flight.setNotes(dto.getNotes());

        return toDto(flightRepository.save(flight));
    }

    @Override
    public List<FlightstatusHistoryResponseDTO> getStatusHistory(UUID flightId) {
        List<FlightStatusHistory> historyList = flightStatusHistoryRepository
                .findByFlightIdOrderByChangedAtDesc(flightId);
        return historyList.stream()
                .map(history -> {
                    FlightstatusHistoryResponseDTO dto = new FlightstatusHistoryResponseDTO();
                    dto.setId(history.getId());
                    dto.setFlightId(history.getFlight().getId());
                    dto.setStatus(history.getStatus());
                    dto.setChangedAt(history.getChangedAt());
                    return dto;
                })
                .collect(Collectors.toList());
    }

    @Override
    public void delete(UUID id) {
        flightRepository.deleteById(id);
    }

    private void logStatusChange(Flight flight, FlightStatus status) {
        FlightStatusHistory history = new FlightStatusHistory();
        history.setFlight(flight);
        history.setStatus(status);
        history.setChangedAt(Timestamp.from(Instant.now()));
        flightStatusHistoryRepository.save(history);
    }

    private FlightResponseDTO toDto(Flight flight) {
        FlightResponseDTO dto = new FlightResponseDTO();
        dto.setId(flight.getId());
        dto.setInspectionAreaId(flight.getInspectionArea().getId());
        dto.setFlightDate(flight.getFlightDate());
        dto.setDroneOperator(flight.getDroneOperator());
        dto.setNotes(flight.getNotes());
        dto.setCreatedAt(flight.getCreatedAt());
        dto.setStatus(flight.getStatus());
        return dto;
    }
}
