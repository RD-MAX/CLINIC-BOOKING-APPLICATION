package com.booking_service.client;

import com.booking_service.dto.BookingConfirmationDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "booking-service")
public interface BookingClient {

    @PostMapping("/confirm/{id}")
    BookingConfirmationDto confirmBooking(@RequestBody BookingConfirmationDto dto);


}