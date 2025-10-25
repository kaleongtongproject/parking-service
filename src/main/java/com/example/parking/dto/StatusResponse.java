package com.example.parking.dto;

import java.util.Set;

public class StatusResponse {
    private int capacity;
    private int available;
    private Set<String> parkedCars;

    public StatusResponse() {
    }

    public StatusResponse(int capacity, int available, Set<String> parkedCars) {
        this.capacity = capacity;
        this.available = available;
        this.parkedCars = parkedCars;
    }

    public int getCapacity() {
        return capacity;
    }

    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }

    public int getAvailable() {
        return available;
    }

    public void setAvailable(int available) {
        this.available = available;
    }

    public Set<String> getParkedCars() {
        return parkedCars;
    }

    public void setParkedCars(Set<String> parkedCars) {
        this.parkedCars = parkedCars;
    }
}