# 🏗️ Facility Scope Backend

A Spring Boot backend application with PostgreSQL and Flyway support, containerized using Docker.

---

## 🧠 Project Purpose

This system manages drone flight inspections for facilities. It allows operators to schedule, track, and audit inspection flights over property areas — while also recording detailed status history and calculating cost per flight.

> ✅ Business Insight: This backend also supports **flight cost estimation** to help reduce operational expenses and improve inspection efficiency.

---

## 🧮 Flight Cost Calculation

The cost of each drone flight is estimated using business logic based on your data model:

| Factor                   | Description                            | Logic                                         |
| ------------------------ | -------------------------------------- | --------------------------------------------- |
| **Flight Duration**      | Time from `IN_PROGRESS` to `COMPLETED` | `duration_minutes * cost_per_minute`          |
| **Rescheduling**         | Extra times flight was rescheduled     | `($reschedules - 1) * penalty_per_reschedule` |
| **Inspection Area Size** | Calculated from polygon area (PostGIS) | `area_m2 * cost_per_sq_meter`                 |
| **Property Location**    | Urban, Rural, or Restricted            | Multiplier to total cost                      |
| **Base Rate**            | Flat rate per flight                   | `$50.00` (modifiable constant)                |

### ✅ Example Calculation

A flight with the following characteristics:

- 45 minutes in the air
- 2 total `SCHEDULED` statuses (1 reschedule)
- 5,000 m² inspection area
- Urban property

**Cost =**  
`$50 + (45 * $0.75) + (1 * $15) + (5000 * $0.01)` → then multiply by `1.2` (urban location multiplier)  
🧾 **Total: ~$185.40**

An endpoint like `GET /flights/{id}/cost` can expose this calculation.

---

## 🐳 Running the App with Docker

This project uses Docker Compose to spin up two services:

- **PostgreSQL** database (`postgres`)
- **Spring Boot** application (`facilityscope_app`)

---

## ⚙️ Prerequisites

- Docker
- Docker Compose
- Java 17+
- Maven

---

## 🚀 Step-by-Step Setup

### 1. 🧼 Clean and Build the JAR

From the project root:

```bash
./mvnw clean package -DskipTests
docker compose down -v --remove-orphans
docker compose up --build -d
```

## 🔄 Run Flyway Migration to Populate Database Tables

docker run --rm --network=facility-scope_default \
 -v ${PWD}/src/main/resources/db/migration:/flyway/sql \
 flyway/flyway:9.22.3 \
 -url=jdbc:postgresql://postgres:5432/facilityscope \
 -user=fs_user -password=fs_password migrate

📌 Future Enhancements
🔍 Predictive Insights: Use past flights and inspection results to predict area deterioration.

🧠 AI/ML Integration: Start simple (e.g., flag areas with >X inspections in Y days) and later use ML models.

🖼️ Image Analysis: Integrate drone-captured imagery for anomaly detection.
