package com.personal.facilityscope.service;

import com.personal.facilityscope.dto.FlightRequestDTO;
import com.personal.facilityscope.dto.FlightResponseDTO;
import com.personal.facilityscope.dto.FlightstatusHistoryResponseDTO;


import java.util.List;
import java.util.UUID;

public interface FlightService {
    FlightResponseDTO create(FlightRequestDTO dto);
    List<FlightResponseDTO> getAll();
    FlightResponseDTO getById(UUID id);
    FlightResponseDTO update(UUID id, FlightRequestDTO dto);
    void delete(UUID id);
    List<FlightstatusHistoryResponseDTO> getStatusHistory(UUID flightId);
}
