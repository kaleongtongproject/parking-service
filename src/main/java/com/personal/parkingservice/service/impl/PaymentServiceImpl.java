package com.personal.parkingservice.service.impl;

import org.springframework.stereotype.Service;

import com.personal.parkingservice.service.PaymentService;

import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

@Service
public class PaymentServiceImpl implements PaymentService {

    private static final AtomicInteger COUNTER = new AtomicInteger();

    @Override
    public String createPaymentIntent(long amountCents, String currency, UUID userId, String idempotencyKey) {
        // In production, call Stripe's SDK and return client secret.
        return "pi_stub_" + COUNTER.incrementAndGet();
    }

    @Override
    public void handleWebhook(String payload) {
        // parse and handle real webhook events from Stripe
    }

    @Override
    public void refund(String paymentIntentId, long amountCents) {
        // call Stripe to refund
    }
}
