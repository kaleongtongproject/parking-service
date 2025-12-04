package com.personal.parkingservice.service;

import com.personal.parkingservice.dto.PricingRequestDTO;
import com.personal.parkingservice.dto.PricingResultDTO;

public interface PricingService {
    PricingResultDTO calculate(PricingRequestDTO request);
}
