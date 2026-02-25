package com.payment_service.dto;

import lombok.Data;
import java.time.LocalDate;
import java.time.LocalTime;

@Data
public class BookingConfirmationDto {
    private Long doctorId;
    private Long patientId;
    private LocalDate date;
    private LocalTime time;
}