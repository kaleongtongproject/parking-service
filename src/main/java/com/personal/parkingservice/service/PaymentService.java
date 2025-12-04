package com.personal.parkingservice.service;

import java.util.UUID;

public interface PaymentService {
    /**
     * Create a payment intent and return a client secret (or payment identifier).
     */
    String createPaymentIntent(long amountCents, String currency, UUID userId, String idempotencyKey);

    void handleWebhook(String payload);

    void refund(String paymentIntentId, long amountCents);
}
