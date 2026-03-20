package com.payment_service.client;


import com.payment_service.dto.BookingConfirmationDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

@FeignClient(name = "booking-service"  ,url = "${booking.service.url}" )  // ✅ config-based (NO hardcoding))
public interface BookingClient {

    @PostMapping("/api/v1/bookings/confirm/{id}")
    void confirmBookingById(@PathVariable("id") long bookingId);

    @GetMapping("/api/v1/bookings/booking/{id}")
    BookingConfirmationDto getBookingById(@PathVariable("id") long bookingId);

}