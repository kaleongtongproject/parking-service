package com.personal.parkingservice.repository;

import com.personal.parkingservice.entity.PaymentTransaction;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface PaymentTransactionRepository extends JpaRepository<PaymentTransaction, UUID> {

    Optional<PaymentTransaction> findByPaymentIntentId(String paymentIntentId);

    Optional<PaymentTransaction> findBySessionId(UUID sessionId);
}
