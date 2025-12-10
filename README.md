# Parking Service API Documentation

This document lists the available API endpoints for the Parking Service and explains their purpose, request structures, and example usages.

---

## 🚗 **Parking Session APIs**

### ### 1. **Start a Parking Session**

**POST** `/api/parking/start`

Creates a new parking session when a vehicle arrives.

#### Request Body

```json
{
  "userId": "UUID",
  "licensePlate": "ABC123",
  "spotId": 42
}
```

#### Response

```json
{
  "sessionId": "<sessionId>",
  "userId": "<UUID>",
  "spotId": 42,
  "checkinTs": "2025-12-10T00:19:03.286707967Z",
  "status": "ONGOING"
}
```

---

### 2. **Checkout (Calculate pricing + create payment intent)**

**POST** `/checkout`

Performs pricing calculation based on stored check-in timestamp and requested checkout timestamp.  
Also creates a mocked payment intent and updates the session.

#### Request Body

```json
{
  "sessionId": "uuid",
  "checkout": "2025-12-01T12:00:00Z",
  "lotId": 1,
  "membership": "PREMIUM" || "NONE
}
```

#### Headers

`X-User-Id: <uuid>` (optional)

#### Response

```json
{
  "clientSecret": "pi_mock_1",
  "pricing": {
    "baseCents": 1200,
    "surchargeCents": 0,
    "discountCents": 0,
    "totalCents": 1200,
    "appliedRules": ["BASE_RATE"]
  }
}
```

---

### 3. **Complete Payment (Mocked Internal Workflow)**

This happens automatically inside `/checkout` via `paymentService.handleWebhook(...)`.

You do **not** call this manually in the mock environment.

---

## 🔍 **Diagnostic APIs**

### 6. **Get Parking Session**

**GET** `/api/parking/status/{licensePlate}`

### 7. **List All Sessions**

**GET** `/api/parking/active`

---

## 🧱 **Entities Involved**

### ParkingSession

Tracks each parking visit.

### PricingRule

Defines dynamic pricing, discounts, and surcharges.

---

## 🧪 **Testing Notes**

- Use UUIDs in standard 36-character format.

---

## ✨ Summary

This API supports:

- Starting a Parking Session
- Pricing calculation
- Pricing rules
- Session lookups
- Checkout a parking session
