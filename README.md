🏥 Doctor Appointment Booking System (Microservices)

A Doctor Appointment Booking Platform built using Spring Boot Microservices.
Patients can search doctors, view available slots, book appointments, and make payments.
Doctors can create profiles and manage their appointment schedules.

This project demonstrates real-world microservice architecture, Feign client communication, and Stripe payment integration.


🚀 Tech Stack


Language: Java

Framework: Spring Boot

Microservices:

Doctor Service

Patient Service

Booking Service

Payment Service

Database: MySQL

Service Discovery: Eureka Server

Inter-Service Communication: OpenFeign

Payment Gateway: Stripe

Build Tool: Maven

API Testing: Postman



Version Control: Git & GitHub


🧩 Microservices Overview
🩺 Doctor Service

Handles doctor profiles and appointment schedules.



Features:


Create doctor profile

Add available appointment slots

Search doctors by specialization and location

Exposes doctor data to Booking Service via Feign



APIs:


POST /api/v1/doctors/save-doctor

GET /api/v1/doctor/search-doctor?specialization=Cardiologist&areaName=BTM

GET /api/v1/doctors/getdoctorbyid?id=1



🧑 Patient Service


Manages patient details.


Features:


Create patient profile

Fetch patient by ID

View all patients

Exposes patient data to Booking Service via Feign



APIs:


POST /api/v1/patients/save-patient

GET /api/v1/patients/getpatientbyid?id=1

GET /api/v1/patients/getallpatients



📅 Booking Service


Handles appointment bookings.


Features:


Create booking for selected doctor and time slot

Get booking by booking ID

Get bookings by patient ID

Get bookings by doctor ID

Uses Feign clients to call Doctor and Patient services


APIs:


POST /api/v1/bookings/create

GET /api/v1/bookings/getbybookingid?id=1

GET /api/v1/bookings/getbypatientid?patientId=2

GET /api/v1/bookings/getbydoctorid?doctorId=3



💳 Payment Service (Stripe)


Handles online payments for bookings.


Features:


Create Stripe checkout session

Redirect user to Stripe payment page

Handle payment success and cancellation


APIs:


POST /checkout

GET /success?session_id={CHECKOUT_SESSION_ID}

GET /cancel


📡 Eureka Server


Service registry for microservices.


Features:


Registers Doctor, Patient, Booking, and Payment services

Enables service discovery for Feign clients



🔁 Booking Flow

Patient → Search Doctor → View Slots → Select Date & Time → Create Booking → Make Payment → Booking Confirmed


🧪 How to Run


Start Eureka Server

Start Doctor Service

Start Patient Service

Start Booking Service

Start Payment Service

Test APIs using Postman



📌 Key Concepts Demonstrated


Microservices architecture


REST API design

DTO ↔ Entity mapping

Feign Client communication

Service Discovery using Eureka

Stripe payment gateway integration

Layered architecture (Controller, Service, Repository)

Git branching & version control
