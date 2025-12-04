package com.personal.parkingservice.dto;

import java.util.List;

public record PricingResultDTO(long baseCents, long surchargeCents, long discountCents, long totalCents,
        List<String> appliedRules) {
}
