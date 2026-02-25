package com.booking_service.client;

import com.booking_service.dto.BookingConfirmationDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "booking-service", url = "http://localhost:7076/api/v1/bookings")
public interface BookingClient {

    @PostMapping("/confirm")
    BookingConfirmationDto confirmBooking(@RequestBody BookingConfirmationDto dto);
}