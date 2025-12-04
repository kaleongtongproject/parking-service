package com.personal.parkingservice.service.impl;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import com.personal.parkingservice.entity.Reservation;
import com.personal.parkingservice.repository.ReservationRepository;
import com.personal.parkingservice.service.PaymentService;
import com.personal.parkingservice.service.ReservationService;

import java.time.Instant;
import java.time.format.DateTimeParseException;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Service
public class ReservationServiceImpl implements ReservationService {

    private final ReservationRepository reservationRepository;
    private final StringRedisTemplate redis;
    private final PaymentService paymentService;

    public ReservationServiceImpl(ReservationRepository reservationRepository, StringRedisTemplate redis,
            PaymentService paymentService) {
        this.reservationRepository = reservationRepository;
        this.redis = redis;
        this.paymentService = paymentService;
    }

    @Override
    public Reservation createReservation(UUID userId, Long lotId, String arrivalIso, int durationMinutes,
            boolean prepay) {
        Instant arrival;
        try {
            arrival = Instant.parse(arrivalIso);
        } catch (DateTimeParseException e) {
            arrival = Instant.now();
        }

        // simple availability check using redis key (demo)
        String key = "reservation:availability:" + lotId + ":" + arrival.toString().substring(0, 10);
        String remaining = redis.opsForValue().get(key);
        if (remaining == null) {
            // initialize to 30 reserved spots for demo
            redis.opsForValue().set(key, "30", 1, TimeUnit.DAYS);
            remaining = "30";
        }
        long r = Long.parseLong(remaining);
        if (r <= 0)
            throw new RuntimeException("No availability");

        // decrement
        Long newVal = redis.opsForValue().decrement(key);
        if (newVal < 0) {
            redis.opsForValue().increment(key);
            throw new RuntimeException("No availability after decrement");
        }

        Reservation res = Reservation.builder()
                .id(UUID.randomUUID())
                .userId(userId)
                .lotId(lotId)
                .arrivalTs(arrival)
                .expectedDurationMinutes(durationMinutes)
                .status("PENDING")
                .priceCents(0L)
                .createdAt(Instant.now())
                .build();

        reservationRepository.save(res);

        // cache reservation as json with TTL (demo: 24 hours)
        String resKey = "reservation:" + res.getId();
        redis.opsForValue().set(resKey, res.getId().toString(), 24, TimeUnit.HOURS);

        if (prepay) {
            String idempotency = "reservation:prepay:" + userId + ":" + res.getId();
            String pi = paymentService.createPaymentIntent(1000, "usd", userId, idempotency);
            res.setPaymentIntentId(pi);
            res.setStatus("CONFIRMED");
            res.setPriceCents(1000L);
            reservationRepository.save(res);
        }

        return res;
    }
}
