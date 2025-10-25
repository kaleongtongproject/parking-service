package com.example.parking.Concurrency;

import com.example.parking.service.ParkingService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
public class ParkingLotConcurrencyTest {
    private static final Logger log = LoggerFactory.getLogger(ParkingLotConcurrencyTest.class);

    @Autowired
    private ParkingService parkingService;

    @BeforeEach
    void setup() {
        // Reset service state to ensure clean test runs
        parkingService.resetMetricsForTest();
    }

    @Test
    void simulate20ConcurrentParkAndLeave() throws InterruptedException {
        final int totalClients = 20;
        final int maxSpots = parkingService.capacity(); // 5 by default in service
        ExecutorService exec = Executors.newFixedThreadPool(totalClients);
        CountDownLatch startLatch = new CountDownLatch(1);
        CountDownLatch doneLatch = new CountDownLatch(totalClients);

        AtomicInteger succeeded = new AtomicInteger(0);

        for (int i = 0; i < totalClients; i++) {
            final String carId = "car-" + i;
            exec.submit(() -> {
                try {
                    startLatch.await(); // wait for common start
                    log.debug("{} starting request", carId);
                    boolean parked = parkingService.park(carId, 10); // wait up to 10s
                    if (parked) {
                        succeeded.incrementAndGet();
                        // simulate parked duration so other threads must wait
                        long parkedMillis = ThreadLocalRandom.current().nextLong(200, 800);
                        log.debug("{} sleeping while parked for {} ms", carId, parkedMillis);
                        Thread.sleep(parkedMillis);
                        parkingService.leave(carId);
                    } else {
                        log.info("{} could not park (no spot within timeout)", carId);
                    }
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    log.warn("{} interrupted", carId);
                } finally {
                    doneLatch.countDown();
                }
            });
        }

        InstantUtilities.sleepMillis(100); // tiny warmup (optional)
        long start = System.currentTimeMillis();
        startLatch.countDown(); // start all threads
        boolean finished = doneLatch.await(30, TimeUnit.SECONDS);
        long duration = System.currentTimeMillis() - start;
        exec.shutdownNow();

        assertThat(finished).isTrue();

        log.info("Test finished in {} ms; succeeded parks: {}", duration, succeeded.get());
        log.info("Max observed concurrent parked (service metric) = {}", parkingService.getMaxObserved());
        assertThat(parkingService.getMaxObserved()).isLessThanOrEqualTo(maxSpots);
        // After all left, available permits should equal capacity
        assertThat(parkingService.availableSpots()).isEqualTo(parkingService.capacity());
    }

    // tiny helper to avoid importing Instant everywhere in the test
    static class InstantUtilities {
        static void sleepMillis(long ms) {
            try {
                Thread.sleep(ms);
            } catch (InterruptedException ignored) {
                Thread.currentThread().interrupt();
            }
        }
    }
}
