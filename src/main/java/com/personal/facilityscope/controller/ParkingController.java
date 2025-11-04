package com.personal.facilityscope.controller;

import com.personal.facilityscope.service.ParkingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/facility/parking")
public class ParkingController {

    private final ParkingService parkingService;

    @Autowired
    public ParkingController(ParkingService parkingService) {
        this.parkingService = parkingService;
    }

    @PostMapping("/park/{vehicle}")
    public ResponseEntity<?> park(@PathVariable("vehicle") String vehicle) {
        int spot = parkingService.park(vehicle);
        if (spot > 0) {
            return ResponseEntity.ok(new ApiResponse(true, "Parked at " + spot));
        }
        return ResponseEntity.status(409).body(new ApiResponse(false, "No spots available"));
    }

    @PostMapping("/exit/{vehicle}")
    public ResponseEntity<?> exit(@PathVariable("vehicle") String vehicle) {
        int spot = parkingService.exit(vehicle);
        if (spot > 0) {
            return ResponseEntity.ok(new ApiResponse(true, "Exited from " + spot));
        }
        return ResponseEntity.status(404).body(new ApiResponse(false, "Vehicle not found"));
    }

    @GetMapping("/available")
    public ResponseEntity<Integer> available() {
        return ResponseEntity.ok(parkingService.getAvailable());
    }

    public static class ApiResponse {
        public final boolean success;
        public final String message;

        public ApiResponse(boolean success, String message) {
            this.success = success;
            this.message = message;
        }
    }
}