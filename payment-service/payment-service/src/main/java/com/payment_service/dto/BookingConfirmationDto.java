package com.payment_service.dto;

import lombok.Data;

import java.time.LocalDate;
import java.time.LocalTime;

@Data
public class BookingConfirmationDto {

    // 🔥 Identity
    private Long id;          // bookingId

    // booking details
    private Long doctorId;
    private Long patientId;
    private LocalDate date;
    private LocalTime time;

    // enriched fields from booking-service
    private String doctorName;
    private String clinicName;
    private String status;

    // 🔥 amount to be paid
    private Float bookingAmount;   // use clear naming
}