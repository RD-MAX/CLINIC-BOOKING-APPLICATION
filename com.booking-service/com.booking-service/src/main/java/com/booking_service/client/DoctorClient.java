package com.booking_service.client;

import com.doctor_service.entity.Doctor;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "doctor-service", url = "http://localhost:7079/api/v1/doctors")
public interface DoctorClient {
    @GetMapping("/get-doctor-by-id")
    Doctor getDoctorById(@RequestParam("id") long id);




}
