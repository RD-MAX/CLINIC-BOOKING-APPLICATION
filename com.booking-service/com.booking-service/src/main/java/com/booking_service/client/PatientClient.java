package com.booking_service.client;

import com.booking_service.dto.Patient;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "patient-service")
public interface PatientClient {

    @GetMapping("api/v1/patients/patient/{id}")
    Patient getPatientById(@PathVariable("id") Long id);
}