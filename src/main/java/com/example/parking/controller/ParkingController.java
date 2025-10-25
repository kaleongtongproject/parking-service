package com.example.parking.controller;

import com.example.parking.dto.LeaveRequest;
import com.example.parking.dto.ParkRequest;
import com.example.parking.dto.StatusResponse;
import com.example.parking.service.ParkingService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/parking")
public class ParkingController {

    private final ParkingService parkingService;

    public ParkingController(ParkingService parkingService) {
        this.parkingService = parkingService;
    }

    @PostMapping("/park")
    public ResponseEntity<?> park(@RequestBody ParkRequest req) throws InterruptedException {
        boolean ok = parkingService.park(req.getCarId(), req.getWaitSeconds());
        if (ok)
            return ResponseEntity.ok().body("parked");
        return ResponseEntity.status(409).body("no-spot");
    }

    @PostMapping("/leave")
    public ResponseEntity<?> leave(@RequestBody LeaveRequest req) {
        boolean ok = parkingService.leave(req.getCarId());
        if (ok)
            return ResponseEntity.ok().body("left");
        return ResponseEntity.status(404).body("not-found");
    }

    @GetMapping("/status")
    public StatusResponse status() {
        return new StatusResponse(parkingService.capacity(), parkingService.availableSpots(),
                parkingService.currentParkedCars());
    }
}