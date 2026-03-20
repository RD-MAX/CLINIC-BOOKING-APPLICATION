package com.payment_service.client;


import com.payment_service.dto.BookingConfirmationDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

@FeignClient(name = "booking-service")
public interface BookingClient {

    @PostMapping("/api/v1/bookings/confirm/{bookingId}")
    void confirmBookingById(@PathVariable long bookingId);

    @GetMapping("/api/v1/bookings/booking/{id}")
    BookingConfirmationDto getBookingById(@PathVariable("id") long bookingId);

}