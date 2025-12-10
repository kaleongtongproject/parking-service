package com.personal.parkingservice.service.impl;

import org.springframework.stereotype.Service;

import com.personal.parkingservice.dto.PricingRequestDTO;
import com.personal.parkingservice.dto.PricingResultDTO;
import com.personal.parkingservice.service.PricingService;

import java.time.Duration;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class PricingServiceImpl implements PricingService {

    private final List<Map<String, Object>> rules;

    public PricingServiceImpl() {
        // Mock rules in memory
        rules = List.of(
                Map.of("type", "base_rate", "centsPerMinute", 5),
                Map.of("type", "membership_discount", "membership", "PREMIUM", "discountPercent", 20),
                Map.of("type", "time_surcharge", "startHour", 18, "endHour", 22, "surchargeCentsPerMinute", 2));
    }

    @Override
    public PricingResultDTO calculate(PricingRequestDTO request) {
        long minutes = Duration.between(request.checkin(), request.checkout()).toMinutes();
        if (minutes < 0)
            minutes = 0;

        long base = 0;
        long surcharge = 0;
        long discount = 0;
        List<String> appliedRules = new ArrayList<>();

        for (Map<String, Object> rule : rules) {
            String type = (String) rule.get("type");
            switch (type) {
                case "base_rate":
                    int rate = (int) rule.get("centsPerMinute");
                    base += minutes * rate;
                    appliedRules.add("BASE_RATE");
                    break;
                case "membership_discount":
                    String membership = (String) rule.get("membership");
                    int percent = (int) rule.get("discountPercent");
                    if (membership.equalsIgnoreCase(request.membership())) {
                        discount += base * percent / 100;
                        appliedRules.add("MEMBERSHIP_DISCOUNT");
                    }
                    break;
                case "time_surcharge":
                    int start = (int) rule.get("startHour");
                    int end = (int) rule.get("endHour");
                    int extra = (int) rule.get("surchargeCentsPerMinute");

                    // Simple approximation: if checkout is in evening, apply surcharge
                    int checkoutHour = request.checkout().atZone(ZoneOffset.UTC).getHour();
                    if (checkoutHour >= start && checkoutHour < end) {
                        surcharge += minutes * extra;
                        appliedRules.add("EVENING_SURCHARGE");
                    }
                    break;
            }
        }

        long total = Math.max(0, base + surcharge - discount);
        return new PricingResultDTO(base, surcharge, discount, total, appliedRules);
    }
}
