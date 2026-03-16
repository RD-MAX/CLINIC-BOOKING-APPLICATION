package com.notification_service.client;

import com.notification_service.dto.BookingConfirmationDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "booking-service", url = "http://localhost:7076/api/v1/bookings")
public interface BookingClient {

    @GetMapping("/{bookingId}")
    BookingConfirmationDto getBookingById(@PathVariable Long bookingId);

}