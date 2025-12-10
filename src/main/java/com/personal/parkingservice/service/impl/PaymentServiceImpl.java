package com.personal.parkingservice.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.personal.parkingservice.entity.PaymentTransaction;
import com.personal.parkingservice.repository.PaymentTransactionRepository;
import com.personal.parkingservice.service.PaymentService;

import lombok.RequiredArgsConstructor;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

@Service
public class PaymentServiceImpl implements PaymentService {

    @Autowired
    private PaymentTransactionRepository paymentTxRepo;

    // For demos: Just increment integers for fake ids
    private static final AtomicInteger COUNTER = new AtomicInteger();

    @Override
    public String createPaymentIntent(long amountCents, String currency, UUID userId, String idempotencyKey) {

        // Check idempotency
        Optional<PaymentTransaction> existing = paymentTxRepo
                .findBySessionId(UUID.fromString(idempotencyKey.split(":")[2]));

        if (existing.isPresent()) {
            return existing.get().getPaymentIntentId(); // return same ID
        }

        // Create fake "payment intent"
        String fakePaymentIntentId = "pi_mock_" + COUNTER.incrementAndGet();

        PaymentTransaction tx = PaymentTransaction.builder()
                .paymentIntentId(fakePaymentIntentId)
                .sessionId(UUID.fromString(idempotencyKey.split(":")[2]))
                .amountCents(amountCents)
                .status("CREATED")
                .createdAt(Instant.now())
                .updatedAt(Instant.now())
                .build();

        paymentTxRepo.save(tx);

        // Immediately simulate payment success
        processPayment(fakePaymentIntentId);

        return fakePaymentIntentId;
    }

    @Override
    public void processPayment(String paymentIntentId) {

        PaymentTransaction tx = paymentTxRepo.findByPaymentIntentId(paymentIntentId)
                .orElseThrow(() -> new RuntimeException("Unknown paymentIntentId"));

        // Avoid double-processing
        if ("SUCCEEDED".equals(tx.getStatus()))
            return;

        tx.setStatus("SUCCEEDED");
        tx.setUpdatedAt(Instant.now());
        paymentTxRepo.save(tx);
    }

    @Override
    public boolean isPaymentCompleted(String paymentIntentId) {
        return paymentTxRepo.findByPaymentIntentId(paymentIntentId)
                .map(t -> "SUCCEEDED".equals(t.getStatus()))
                .orElse(false);
    }
}
