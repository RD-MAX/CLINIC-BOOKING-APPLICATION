package com.notification_service.client;

import com.notification_service.dto.BookingConfirmationDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "booking-service", url = "${booking.service.url}")
public interface BookingClient {

    @GetMapping("/booking/{id}")
    BookingConfirmationDto getBookingById(@PathVariable("id") Long bookingId);

}