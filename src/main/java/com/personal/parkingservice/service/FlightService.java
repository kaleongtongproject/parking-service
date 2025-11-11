package com.personal.parkingservice.service;

import java.util.List;
import java.util.UUID;

import com.personal.parkingservice.dto.FlightRequestDTO;
import com.personal.parkingservice.dto.FlightResponseDTO;
import com.personal.parkingservice.dto.FlightstatusHistoryResponseDTO;

public interface FlightService {
    FlightResponseDTO create(FlightRequestDTO dto);

    List<FlightResponseDTO> getAll();

    FlightResponseDTO getById(UUID id);

    FlightResponseDTO update(UUID id, FlightRequestDTO dto);

    void delete(UUID id);

    List<FlightstatusHistoryResponseDTO> getStatusHistory(UUID flightId);
}
