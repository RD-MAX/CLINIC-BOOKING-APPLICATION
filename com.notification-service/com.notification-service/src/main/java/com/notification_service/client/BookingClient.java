package com.notification_service.client;

import com.notification_service.dto.BookingConfirmationDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "booking-service")
public interface BookingClient {

    @GetMapping("/booking/{bookingId}")
    BookingConfirmationDto getBookingById(@PathVariable Long bookingId);

}