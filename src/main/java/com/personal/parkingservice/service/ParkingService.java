package com.personal.parkingservice.service;

import java.time.Instant;
import java.util.Collections;
import java.util.UUID;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Service;

import com.personal.parkingservice.dto.StartSessionRequestDTO;
import com.personal.parkingservice.dto.StartSessionResponseDTO;
import com.personal.parkingservice.entity.ParkingSession;
import com.personal.parkingservice.repository.ParkingSessionRepository;

@Service
public class ParkingService {

    public static final String AVAILABLE_SPOTS_KEY = "parking:available_spots";
    private final ParkingSessionRepository parkingSessionRepository;
    private final StringRedisTemplate redisTemplate;

    // Lua script:
    // - Return -1 → missing key
    // - Return 0 → full
    // - Return 1 → decrement success
    private static final String PARK_IN_LUA = "local spots = tonumber(redis.call('GET', KEYS[1])) " +
            "if not spots then return -1 end " +
            "if spots > 0 then " +
            "   redis.call('DECR', KEYS[1]) " +
            "   return 1 " +
            "else return 0 end";

    private final DefaultRedisScript<Long> parkInScript;

    public ParkingService(StringRedisTemplate redisTemplate, ParkingSessionRepository parkingSessionRepository) {
        this.redisTemplate = redisTemplate;
        this.parkInScript = new DefaultRedisScript<>();
        this.parkInScript.setScriptText(PARK_IN_LUA);
        this.parkInScript.setResultType(Long.class);
        this.parkingSessionRepository = parkingSessionRepository;
    }

    /**
     * Attempt to park a car (atomic via Lua).
     * 
     * @return true if parked successfully
     * @throws IllegalStateException
     *             if key missing or Redis error
     */
    public boolean parkIn() {
        Long result = redisTemplate.execute(
                parkInScript,
                Collections.singletonList(AVAILABLE_SPOTS_KEY));

        if (result == null) {
            throw new IllegalStateException("Redis returned null for Lua script execution");
        }

        switch (result.intValue()) {
            case -1:
                throw new IllegalStateException("Parking key not initialized: " + AVAILABLE_SPOTS_KEY);
            case 0:
                return false; // full
            case 1:
                return true; // success
            default:
                throw new IllegalStateException("Unexpected value from Lua script: " + result);
        }
    }

    /** Park out (increment available spots). */
    public void parkOut() {
        redisTemplate.opsForValue().increment(AVAILABLE_SPOTS_KEY);
    }

    /** Get current available spots. */
    public int getAvailableSpots() {
        String v = redisTemplate.opsForValue().get(AVAILABLE_SPOTS_KEY);

        if (v == null) {
            return -1;
        }

        try {
            return Integer.parseInt(v);
        } catch (NumberFormatException ex) {
            throw new IllegalStateException(
                    "Invalid Redis value for " + AVAILABLE_SPOTS_KEY + ": " + v);
        }
    }

    /** Force reset spots — used for load test & debugging. */
    public void resetSpots(int spots) {
        redisTemplate.opsForValue().set(AVAILABLE_SPOTS_KEY, String.valueOf(spots));
    }

    public StartSessionResponseDTO startSession(StartSessionRequestDTO request) {

        ParkingSession session = ParkingSession.builder()
                .id(UUID.randomUUID())
                .userId(request.userId())
                .spotId(request.spotId()) // can be NULL
                .checkinTs(Instant.now())
                .status("ONGOING")
                .pricingVersion("v1")
                .build();

        parkingSessionRepository.save(session);

        return new StartSessionResponseDTO(
                session.getId(),
                session.getUserId(),
                session.getSpotId(),
                session.getCheckinTs(),
                session.getStatus());
    }

}
