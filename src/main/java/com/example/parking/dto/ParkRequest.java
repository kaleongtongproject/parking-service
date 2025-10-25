package com.example.parking.dto;

public class ParkRequest {
    private String carId;
    private long waitSeconds = 5;

    public ParkRequest() {
    }

    public String getCarId() {
        return carId;
    }

    public void setCarId(String carId) {
        this.carId = carId;
    }

    public long getWaitSeconds() {
        return waitSeconds;
    }

    public void setWaitSeconds(long waitSeconds) {
        this.waitSeconds = waitSeconds;
    }
}