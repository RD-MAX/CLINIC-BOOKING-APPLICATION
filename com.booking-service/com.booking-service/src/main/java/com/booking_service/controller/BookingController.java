package com.booking_service.controller;

import com.booking_service.dto.BookingConfirmationDto;
import com.booking_service.service.BookingService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@Validated
@RequestMapping("/api/v1/bookings")
public class BookingController {

    @Autowired
    private BookingService bookingService;

    @PostMapping("/create")
    public BookingConfirmationDto createBooking(@Valid @RequestBody BookingConfirmationDto dto) {
        return bookingService.createBooking(dto);
    }

     //after--payment--confirm by details in json-- /*@RequestBody BookingConfirmationDto dto*/
   @PostMapping("/confirm/{id}")
    public BookingConfirmationDto confirmBookingById(@PathVariable("id") long bookingId) {
        return bookingService.confirmBookingById(bookingId);
    }

    @GetMapping("/booking/{id}")
    public BookingConfirmationDto getBookingById(@PathVariable("id") Long bookingId) {
        return bookingService.getBookingById(bookingId);
    }

    @GetMapping("/patient/{id}")
    public Map<String, Object> getBookingsByPatientId(

            @PathVariable("id") Long patientId,

            @RequestParam(required = false) String status,

            @RequestParam(defaultValue = "0") int pageNo,
            @RequestParam(defaultValue = "5") int pageSize,

            @RequestParam(defaultValue = "asc") String sortDir,
            @RequestParam(defaultValue = "date") String sortBy
    ) {
        return bookingService.getBookingsByPatientId(
                patientId,
                status,
                pageNo,
                pageSize,
                sortDir,
                sortBy
        );
    }

    @GetMapping("/doctor/{doctorId}")
    public Map<String, Object> getBookingsByDoctorId(

            @PathVariable Long doctorId,

            @RequestParam(required = false) String status,

            @RequestParam(defaultValue = "0") int pageNo,
            @RequestParam(defaultValue = "5") int pageSize,

            @RequestParam(defaultValue = "asc") String sortDir,
            @RequestParam(defaultValue = "date") String sortBy
    ) {
        return bookingService.getBookingsByDoctorId(
                doctorId,
                status,
                pageNo,
                pageSize,
                sortDir,
                sortBy
        );
    }
}


