package com.personal.parkingservice.service;

import java.util.UUID;

import com.personal.parkingservice.entity.Reservation;

public interface ReservationService {
    Reservation createReservation(UUID userId, Long lotId, String arrivalIso, int durationMinutes, boolean prepay);
}
