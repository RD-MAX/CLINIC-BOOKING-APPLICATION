package com.booking_service.client;

import com.booking_service.dto.Doctor;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "doctor-service", url = "${doctor.service.url}")
public interface DoctorClient {

    @GetMapping("/api/v1/doctors/doctor/{id}")
    Doctor getDoctorById(@PathVariable("id") long id); // ✅ FIXED
}