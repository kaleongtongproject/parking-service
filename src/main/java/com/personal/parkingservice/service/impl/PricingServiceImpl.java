package com.personal.parkingservice.service.impl;

import org.springframework.stereotype.Service;

import com.personal.parkingservice.dto.PricingRequestDTO;
import com.personal.parkingservice.dto.PricingResultDTO;
import com.personal.parkingservice.service.PricingService;

import java.time.Duration;
import java.util.List;

@Service
public class PricingServiceImpl implements PricingService {

    // NOTE: This is a simple implementation for demo purposes.
    // Production should split time ranges and apply rules per segment.
    private final long rateCentsPerMinute = 5; // $0.05 per minute -> $3.00/hr

    @Override
    public PricingResultDTO calculate(PricingRequestDTO request) {
        long minutes = Duration.between(request.checkin(), request.checkout()).toMinutes();
        if (minutes < 0)
            minutes = 0;
        long base = minutes * rateCentsPerMinute;
        long surcharge = 0;
        long discount = 0;
        long total = Math.max(0, base + surcharge - discount);
        return new PricingResultDTO(base, surcharge, discount, total, List.of("BASE_RATE"));
    }
}
