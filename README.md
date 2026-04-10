# 🏥 Clinic Booking Application (Microservices Architecture)

## 🚀 Overview

This is a **full-stack microservices-based Clinic Booking Application** built using **Spring Boot**. The system allows patients to search doctors, book appointments, make payments, and receive notifications.

The project demonstrates real-world backend architecture including:

* Microservices communication
* Event-driven design
* Secure authentication
* Payment integration

---

## 🧩 Microservices Included

* 🔐 Auth Service (JWT + Spring Security)
* 👨‍⚕️ Doctor Service
* 🧑‍🤝‍🧑 Patient Service
* 📅 Booking Service
* 💳 Payment Service (Stripe Webhook Integration)
* 🔔 Notification Service (Email, SMS, WhatsApp via Twilio)
* 📦 Order/Workflow handling
* 🧭 Eureka Server (Service Discovery)

---

## 🏗️ Architecture Diagram

```text
                ┌────────────────────┐
                │    API Client      │
                │   (Postman/UI)     │
                └─────────┬──────────┘
                          │
                          ▼
                ┌────────────────────┐
                │   Auth Service     │
                │  (JWT Security)    │
                └─────────┬──────────┘
                          │
                          ▼
                ┌────────────────────┐
                │  API Requests Flow │
                └─────────┬──────────┘
                          │
        ┌─────────────────┼─────────────────┐
        ▼                 ▼                 ▼
┌──────────────┐  ┌──────────────┐  ┌──────────────┐
│ Doctor       │  │ Patient      │  │ Booking      │
│ Service      │  │ Service      │  │ Service      │
└──────┬───────┘  └──────┬───────┘  └──────┬───────┘
       │                 │                 │
       └──────────┬──────┴──────────┬──────┘
                  ▼                 ▼
          ┌──────────────┐  ┌──────────────┐
          │ Payment      │  │ Notification │
          │ Service      │  │ Service      │
          └──────┬───────┘  └──────────────┘
                 │
                 ▼
          ┌──────────────┐
          │   Kafka      │
          │ (Event Bus)  │
          └──────────────┘

        (All services registered with Eureka Server)
```

### 🔥 Flow Explanation

1. Client sends request → Auth Service validates JWT
2. Booking Service interacts with Doctor & Patient services via Feign
3. Payment Service handles Stripe payment
4. On success → Kafka event triggered and Booking Confirmed
5. Notification Service consumes event → sends SMS/Email/Whatsapp

---

## ⚙️ Tech Stack

### Backend

* Java 17
* Spring Boot
* Spring Data JPA
* Spring Security + JWT
* Spring Cloud (Open Feign Client, Eureka)
* Apache Kafka (Event-driven communication)

### Database

* MySQL (Workbench used for queries & management)

### Third-Party Integrations

* Stripe (Payment + Webhooks)
* Twilio (SMS / WhatsApp Notifications)
* Email Notifications

### Tools & Development

* IntelliJ IDEA
* Postman (API Testing)
* Maven
* Spring Initializer

---

## 🔗 Key Features

### ✅ Microservices Architecture

* Loosely coupled services
* Service-to-service communication using **OpenFeign**

### ✅ Authentication & Security

* JWT-based authentication
* Role-based authorization
* Spring Security implemented

### ✅ Booking System

* Doctor schedule validation
* Slot availability check
* Prevents double booking

### ✅ Payment Integration

* Stripe payment gateway
* Prevents double payment
* Webhook-based booking confirmation

### ✅ Event-Driven Architecture

* Kafka used for async communication
* Booking confirmation triggers notification events

### ✅ Notification System

* Email notifications
* SMS & WhatsApp via Twilio

### ✅ Pagination, Sorting & Filtering

* Implemented in APIs
* Supports search

### ✅ Validation

* DTO-level validation using annotations
* Global exception handling

---

## 🗄️ Database Design

* Relational database (MySQL)
* Separate schema per service
* Tables include:

  * Patients
  * Doctors
  * Bookings
  * Booking Confirmations

---

## 🐳 Docker Support (Planned / In Progress)

* Services can be containerized
* MySQL and Kafka can run via Docker
* Goal: One-command startup using `docker-compose`

### Prerequisites To Run

* Java 17
* Maven
* MySQL
* Kafka

### Steps

1. Clone the repository
2. Start MySQL & Kafka
3. Run Eureka Server
4. Start all microservices
5. Test APIs using Postman

---

## 📌 Future Improvements

* Docker Compose for full system setup
* API Gateway integration
* UI (React/Angular)
* Centralized logging (ELK)
* Distributed tracing (Zipkin)

---

## 💡 What I Learned

* Designing scalable microservices
* Handling inter-service communication
* Implementing secure authentication
* Working with real-world payment systems
* Event-driven architecture using Kafka

---

## 👩‍💻 Author

**Riya**
