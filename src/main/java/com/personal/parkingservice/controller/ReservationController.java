package com.personal.parkingservice.controller;

import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.personal.parkingservice.entity.Reservation;
import com.personal.parkingservice.service.ReservationService;

import java.util.UUID;

@RestController
@RequestMapping("/api/reservations")
public class ReservationController {

    private final ReservationService reservationService;

    public ReservationController(ReservationService reservationService) {
        this.reservationService = reservationService;
    }

    @PostMapping
    public ResponseEntity<?> create(@Valid @RequestBody CreateReservationRequest req,
            @RequestHeader(value = "X-User-Id", required = false) UUID userId) {
        UUID uid = userId != null ? userId : UUID.randomUUID();
        Reservation r = reservationService.createReservation(uid, req.lotId(), req.arrivalIso(), req.durationMinutes(),
                req.prepay());
        return ResponseEntity.ok(r);
    }

    public static record CreateReservationRequest(Long lotId, String arrivalIso, int durationMinutes, boolean prepay) {
    }
}
