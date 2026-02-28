package com.booking_service.controller;

import com.booking_service.dto.BookingConfirmationDto;
import com.booking_service.service.BookingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/bookings")
public class BookingController {

    @Autowired
    private BookingService bookingService;

    @PostMapping("/create")
    public BookingConfirmationDto createBooking(@RequestBody BookingConfirmationDto dto) {
        return bookingService.createBooking(dto);
    }

     //after--payment--confirm by details in json-- /*@RequestBody BookingConfirmationDto dto*/
   @PostMapping("/confirm/{bookingId}")
    public BookingConfirmationDto confirmBookingById(@PathVariable long bookingId) {
        return bookingService.confirmBookingById(bookingId);
    }

    @GetMapping("/{bookingId}")
    public BookingConfirmationDto getBookingById(@PathVariable Long bookingId) {
        return bookingService.getBookingById(bookingId);
    }

    @GetMapping("/getbypatientid")
    public List<BookingConfirmationDto> getBookingsByPatientId(@RequestParam Long patientId) {
        return bookingService.getBookingsByPatientId(patientId);
    }
    @GetMapping("/getbydoctorid")
    public List<BookingConfirmationDto> getBookingsByDoctorId(@RequestParam Long doctorId) {
        return bookingService.getBookingsByDoctorId(doctorId);
    }


}
