package com.personal.parkingservice.service;

import java.util.UUID;

public interface PaymentService {

    /**
     * Create a payment intent and simulate a payment.
     * Should be idempotent.
     */
    String createPaymentIntent(long amountCents, String currency, UUID userId, String idempotencyKey);

    /**
     * Simulate payment success immediately (no webhook).
     */
    void processPayment(String paymentIntentId);

    /**
     * Lookup payment state.
     */
    boolean isPaymentCompleted(String paymentIntentId);
}
