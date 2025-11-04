package com.personal.facilityscope.service;

import com.personal.facilityscope.model.ParkingSpot;
import org.springframework.stereotype.Service;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicInteger;

@Service
public class ParkingService {
    private final ConcurrentLinkedQueue<ParkingSpot> freeSpots = new ConcurrentLinkedQueue<>();
    private final ConcurrentHashMap<String, ParkingSpot> occupied = new ConcurrentHashMap<>();
    private final AtomicInteger available = new AtomicInteger(0);
    private static final int INITIAL_SPOTS = 30;

    public ParkingService() {
        for (int i = 1; i <= INITIAL_SPOTS; i++) {
            freeSpots.add(new ParkingSpot(i));
        }
        available.set(INITIAL_SPOTS);
    }

    /**
     * Try to park a vehicle. This is designed to be fast and non-blocking.
     * Returns the spot id if parked, or -1 if failed.
     */
    public int park(String vehicleNumber) {
        // If vehicle already parked, return existing spot id
        ParkingSpot existing = occupied.get(vehicleNumber);
        if (existing != null) {
            return existing.getId();
        }

        ParkingSpot spot;
        while ((spot = freeSpots.poll()) != null) {
            // try to occupy this spot (atomic)
            if (spot.tryOccupy(vehicleNumber)) {
                occupied.put(vehicleNumber, spot);
                available.decrementAndGet();
                return spot.getId();
            } else {
                // someone beat us to it; continue
                continue;
            }
        }

        // no free spot available
        return -1;
    }

    /**
     * Remove a parked vehicle. Returns the freed spot id or -1 if not found.
     */
    public int exit(String vehicleNumber) {
        ParkingSpot spot = occupied.remove(vehicleNumber);
        if (spot != null) {
            if (spot.release()) {
                freeSpots.add(spot);
                available.incrementAndGet();
                return spot.getId();
            }
        }
        return -1;
    }

    public int getAvailable() {
        return available.get();
    }
}