package com.example.parking.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;
import java.util.Collections;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

@Service
public class ParkingService {
    private static final Logger log = LoggerFactory.getLogger(ParkingService.class);

    private final int capacity;
    private final Semaphore spots;
    private final ConcurrentMap<String, Instant> parkedCars = new ConcurrentHashMap<>();

    // Instrumentation
    private final AtomicInteger currentCount = new AtomicInteger(0);
    private final AtomicInteger maxObserved = new AtomicInteger(0);

    public ParkingService() {
        this.capacity = 5; // default
        this.spots = new Semaphore(capacity, true);
    }

    /**
     * Attempts to park a car.
     *
     * @param carId
     *            unique car id
     * @param waitSeconds
     *            how many seconds to wait for a permit
     * @return true if parked (already parked or acquired permit), false otherwise
     * @throws InterruptedException
     *             if thread interrupted while waiting
     */
    public boolean park(String carId, long waitSeconds) throws InterruptedException {
        if (carId == null || carId.isBlank()) {
            log.warn("park() called with null/blank carId");
            return false;
        }
        if (parkedCars.containsKey(carId)) {
            log.info("car={} already parked (no-op)", carId);
            return true;
        }

        Instant before = Instant.now();
        log.debug("car={} attempting to acquire a permit (waitSeconds={})", carId, waitSeconds);
        boolean got = spots.tryAcquire(waitSeconds, TimeUnit.SECONDS);
        Instant after = Instant.now();
        long waitedMs = Duration.between(before, after).toMillis();

        if (!got) {
            log.info("car={} could not acquire a spot after {} ms (no available permits)", carId, waitedMs);
            return false;
        }

        // we got a permit — attempt to register the car
        Instant prev = parkedCars.putIfAbsent(carId, Instant.now());
        if (prev != null) {
            // race: someone else recorded the car; release the permit we acquired
            spots.release();
            log.info("car={} was already parked by another thread; released permit (waited {} ms)", carId, waitedMs);
            return true;
        }

        // successfully parked: update instrumentation
        int now = currentCount.incrementAndGet();
        maxObserved.getAndUpdate(prevMax -> Math.max(prevMax, now));

        log.info("car={} parked (waited={} ms). currentParked={}, maxObserved={}",
                carId, waitedMs, now, maxObserved.get());
        // If you want quick-and-dirty console output instead of logging, uncomment:
        // System.out.println("car=" + carId + " parked (waited=" + waitedMs + "ms).
        // current=" + now);

        return true;
    }

    /**
     * Leave the parking lot and release a permit.
     */
    public boolean leave(String carId) {
        if (carId == null || carId.isBlank()) {
            log.warn("leave() called with null/blank carId");
            return false;
        }
        Instant removed = parkedCars.remove(carId);
        if (removed == null) {
            log.info("leave() called for car={} but it was not found", carId);
            return false;
        }

        spots.release();
        int now = currentCount.decrementAndGet();
        log.info("car={} left. currentParked={}, availablePermits={}", carId, now, spots.availablePermits());
        // System.out.println("car=" + carId + " left. current=" + now);

        return true;
    }

    public int availableSpots() {
        return spots.availablePermits();
    }

    public int capacity() {
        return capacity;
    }

    public Set<String> currentParkedCars() {
        return Collections.unmodifiableSet(parkedCars.keySet());
    }

    // instrumentation getters
    public int getCurrentCount() {
        return currentCount.get();
    }

    public int getMaxObserved() {
        return maxObserved.get();
    }

    // test helper - reset metrics (package-private)
    public void resetMetricsForTest() {
        currentCount.set(0);
        maxObserved.set(0);
        parkedCars.clear();
        while (spots.availablePermits() < capacity) {
            // try to re-create permits if tests left permits acquired (defensive)
            spots.release();
        }
    }
}
