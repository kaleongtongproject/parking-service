🚗 Parking Service — High-Performance, Resilient, and Scalable Parking Lot System

A production-ready parking lot backend service built with Java, Spring Boot, and Redis.
Designed to support 500+ requests per second, real-time spot management, high availability, and enterprise-level profitability features such as billing, analytics, and dynamic pricing.

This README documents the system architecture, requirements, features, and roadmap for evolving this project into a full enterprise solution.

📘 Table of Contents

Overview

Core Features

System Requirements

Architecture

Technical Design

Scalability & High Availability

Security & Compliance

Monitoring & Observability

Testing Strategy

Corporate Feature Roadmap

Local Development

API Endpoints

📌 Overview

This service manages real-time parking capacity for one or more parking lots and ensures:

Atomic check-in/check-out operations

Resilience to high concurrency

Consistency even under extreme load

Low latency (<5 ms Redis operations)

The service uses Redis Lua scripts to achieve safe distributed counters at 500+ RPS.

🚀 Core Features
✔️ Real-Time Spot Management

Atomic check-in (decrement spot count)

Atomic check-out (increment spot count)

Redis-backed availability tracking

✔️ High Performance

Designed for >500 RPS

Uses Redis Lua scripting to prevent race conditions

✔️ REST API

/parking/in

/parking/out

/parking/status

✔️ Production-level Requirements (expandable)

Billing & pricing

Reservations

Dynamic pricing

Payment integration

Analytics & dashboards

Event-driven architecture

🧩 System Requirements
Functional Requirements

Users can check in (park) if a spot is available.

Users can check out (leave), freeing a spot.

System must calculate remaining availability in real time.

Admins can configure:

Maximum capacity

Pricing rules

Parking zones

Non-Functional Requirements
Category Requirements
Performance Handle 500+ RPS for check-ins/outs
Availability 99.9% uptime, multi-node deployment
Latency Redis operations < 5 ms
Consistency No overselling spots (atomic Lua scripts)
Security OAuth2/JWT, RBAC, rate limiting
Scalability Horizontal scaling of application & Redis
🏗️ Architecture
┌────────────────────┐ ┌─────────────────────┐
│ Client / Mobile │ ---> │ API Gateway │
└────────────────────┘ └─────────────────────┘
│ │
▼ ▼
┌──────────────────────────────────────────┐
│ Parking Service (Spring) │
└──────────────────────────────────────────┘
│ │
▼ ▼
┌─────────────────┐ ┌─────────────────────┐
│ Redis (Primary) │ │ Redis (Replica) │
└─────────────────┘ └─────────────────────┘
│
▼
┌───────────────────────┐
│ Long-term Storage │ (Optional: events)
│ Postgres / S3 / Kafka │
└───────────────────────┘

⚙️ Technical Design
Redis Key
parking:available_spots -> integer

Lua Script for Atomic Check-In

Prevents overselling spots.

local spots = tonumber(redis.call('GET', KEYS[1]))
if not spots then return -1 end
if spots > 0 then
redis.call('DECR', KEYS[1])
return 1
else
return 0
end

Check-Out

Uses Redis INCR.

📈 Scalability & High Availability

1. Redis Cluster or Sentinel

To avoid single-point-of-failure.

2. Stateless Application Nodes

Autoscaling via Kubernetes/ECS.

3. API Rate Limiting

Protects against abuse.

4. Caching Layers

Redis handles real-time spot data with O(1) operations.

5. Event-driven Logging

Every parking event emitted to Kafka → used for BI dashboards.

🔐 Security & Compliance

OAuth2 + JWT authentication

TLS everywhere

Rate limiting (per IP or per user)

Role-Based Access Control (RBAC)

PCI DSS compliance for billing integrations

GDPR/CCPA if operating user data in regulated regions

📊 Monitoring & Observability
Metrics (Prometheus)

request count, success/failure

Redis latency

available spots

peak-hour load

error rate

Logs (JSON structured)

every check-in/out event

admin configuration changes

Alerts

Redis unavailable

occupancy anomaly

high error rate

latency > threshold

🧪 Testing Strategy
Unit Tests

Service logic

Lua script wrapper tests

Integration Tests

Testcontainers for Redis

Concurrency tests simulating >100 requests/sec

Load Testing

Tools:

k6

JMeter

Locust

Scenarios:

burst of 500 RPS for 60 seconds

sustained load for 10 minutes

failover simulation

🗺️ Corporate Feature Roadmap
Phase 1 — Stability (MVP+)

High availability Redis

Prometheus/Grafana monitoring

Rate limiting & JWT auth

Phase 2 — Revenue Features

Hourly billing engine

Dynamic pricing

Stripe/PayPal payment integration

Email/SMS receipts

Phase 3 — Customer Products

Reservations system

Membership subscriptions

Mobile app integration

Phase 4 — Enterprise Platform

Multi-lot management

Event-driven architecture with Kafka

Analytics dashboards (PowerBI, Looker)

AI pricing optimizer

🧑‍💻 Local Development
Requirements

JDK 17+

Docker (for Redis)

Maven

Start Redis
docker run -p 6379:6379 --name redis redis:7

Set initial capacity
redis-cli set parking:available_spots 30

Run the app
mvn spring-boot:run

🌐 API Endpoints
Check In
POST /parking/in

Response

{ "success": true }

Check Out
POST /parking/out

Response

{ "success": true }

Get Status
GET /parking/status

Response

{ "availableSpots": 20 }
