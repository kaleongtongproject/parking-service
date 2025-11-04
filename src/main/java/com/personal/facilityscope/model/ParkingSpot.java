package com.personal.facilityscope.model;

import java.util.concurrent.atomic.AtomicBoolean;

public class ParkingSpot {
    private final int id;
    private final AtomicBoolean occupied = new AtomicBoolean(false);
    private volatile String vehicleNumber;

    public ParkingSpot(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public boolean isOccupied() {
        return occupied.get();
    }

    public boolean tryOccupy(String vehicleNumber) {
        if (occupied.compareAndSet(false, true)) {
            this.vehicleNumber = vehicleNumber;
            return true;
        }
        return false;
    }

    public boolean release() {
        if (occupied.compareAndSet(true, false)) {
            this.vehicleNumber = null;
            return true;
        }
        return false;
    }

    public String getVehicleNumber() {
        return vehicleNumber;
    }
}