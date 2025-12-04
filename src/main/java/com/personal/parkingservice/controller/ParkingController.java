package com.personal.parkingservice.controller;

import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.personal.parkingservice.dto.PricingRequestDTO;
import com.personal.parkingservice.dto.PricingResultDTO;
import com.personal.parkingservice.dto.StartSessionRequestDTO;
import com.personal.parkingservice.dto.StartSessionResponseDTO;
import com.personal.parkingservice.entity.ParkingSession;
import com.personal.parkingservice.repository.ParkingSessionRepository;
import com.personal.parkingservice.service.ParkingService;
import com.personal.parkingservice.service.PaymentService;
import com.personal.parkingservice.service.PricingService;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/api/parking")
public class ParkingController {

        private final PricingService pricingService;
        private final PaymentService paymentService;
        private final ParkingService parkingService;
        private final ParkingSessionRepository sessionRepository;

        public ParkingController(PricingService pricingService, PaymentService paymentService,
                        ParkingSessionRepository sessionRepository, ParkingService parkingService) {
                this.pricingService = pricingService;
                this.paymentService = paymentService;
                this.sessionRepository = sessionRepository;
                this.parkingService = parkingService;
        }

        @PostMapping("/checkout")
        public ResponseEntity<?> checkout(
                        @Valid @RequestBody CheckoutRequest req,
                        @RequestHeader(value = "X-User-Id", required = false) UUID userId) {

                UUID sessionId = req.sessionId();
                if (sessionId == null) {
                        return ResponseEntity.badRequest().body("sessionId is required");
                }

                // 1. Load session from DB
                ParkingSession session = sessionRepository.findById(sessionId)
                                .orElseThrow(() -> new IllegalArgumentException("Session not found: " + sessionId));

                // 2. Use stored check-in timestamp
                if (session.getCheckinTs() == null) {
                        throw new IllegalStateException("Session has no check-in timestamp");
                }

                // 3. Use checkout timestamp from the request
                Instant checkoutTime = req.checkout();
                if (checkoutTime == null) {
                        return ResponseEntity.badRequest().body("checkout timestamp is required");
                }

                // 4. Build pricing request using real DB checkin
                PricingResultDTO result = pricingService.calculate(
                                new PricingRequestDTO(
                                                sessionId,
                                                session.getCheckinTs(),
                                                checkoutTime,
                                                req.lotId(),
                                                req.membership()));

                // 5. Create payment intent
                String idempotency = "checkout:" + (userId != null ? userId : "anon") + ":" + sessionId;
                String clientSecret = paymentService.createPaymentIntent(
                                result.totalCents(), "usd", userId, idempotency);

                // 6. Persist updated session
                session.setCheckoutTs(checkoutTime);
                session.setTotalAmountCents(result.totalCents());
                session.setPaymentIntentId(clientSecret);
                session.setStatus("AWAITING_PAYMENT");
                sessionRepository.save(session);

                // 7. Return result
                return ResponseEntity.ok(new CheckoutResponse(clientSecret, result));
        }

        @PostMapping("/start")
        public ResponseEntity<StartSessionResponseDTO> startSession(
                        @Valid @RequestBody StartSessionRequestDTO request) {

                StartSessionResponseDTO response = parkingService.startSession(request);
                return ResponseEntity.ok(response);
        }

        @GetMapping("/status/{licensePlate}")
        public ResponseEntity<?> getStatus(@PathVariable String licensePlate) {
                Optional<ParkingSession> session = sessionRepository.findByLicensePlateAndStatus(licensePlate,
                                "AWAITING_PAYMENT");
                return session.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
        }

        /**
         * List all currently parked cars (optional)
         */
        @GetMapping("/active")
        public ResponseEntity<List<ParkingSession>> getActiveSessions() {
                List<ParkingSession> active = sessionRepository.findAllByStatus("AWAITING_PAYMENT");
                return ResponseEntity.ok(active);
        }

        // record-based request/response
        public static record CheckoutRequest(UUID sessionId, Instant checkin, Instant checkout, Long lotId,
                        String membership) {
        }

        public static record CheckoutResponse(String clientSecret, PricingResultDTO pricing) {
        }
}