package com.personal.parkingservice.controller;

import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.personal.parkingservice.service.ParkingService;

@RestController
@RequestMapping("/parking")
public class ParkingController {

    private final ParkingService parkingService;

    public ParkingController(ParkingService parkingService) {
        this.parkingService = parkingService;
    }

    @PostMapping("/in")
    public ResponseEntity<?> parkIn() {
        try {
            boolean success = parkingService.parkIn();
            int available = parkingService.getAvailableSpots();

            if (success) {
                return ResponseEntity.ok(Map.of(
                        "success", true,
                        "message", "Park-in success",
                        "availableSpots", available));
            } else {
                return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of(
                        "success", false,
                        "message", "Parking lot full",
                        "availableSpots", available));
            }

        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                    "success", false,
                    "error", ex.getMessage()));
        }
    }

    @PostMapping("/out")
    public ResponseEntity<?> parkOut() {
        parkingService.parkOut();
        int available = parkingService.getAvailableSpots();

        return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "Park-out processed",
                "availableSpots", available));
    }

    @GetMapping("/status")
    public ResponseEntity<?> status() {
        int available = parkingService.getAvailableSpots();

        if (available == -1) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                    "success", false,
                    "error", "Parking key not initialized"));
        }

        return ResponseEntity.ok(Map.of(
                "success", true,
                "availableSpots", available));
    }

    /** Reset spots for debugging & load testing */
    @PostMapping("/reset")
    public ResponseEntity<?> reset(@RequestParam(defaultValue = "30") int spots) {
        parkingService.resetSpots(spots);

        return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "Parking spots reset",
                "availableSpots", spots));
    }
}
