package com.doctor_service.controller;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;
import com.doctor_service.dto.SearchResultDto;
import com.doctor_service.entity.Doctor;
import com.doctor_service.entity.DoctorAppointmentSchedule;
import com.doctor_service.entity.TimeSlots;
import com.doctor_service.repository.DoctorAppointmentScheduleRepository;
import com.doctor_service.repository.DoctorsRepository;
import com.doctor_service.repository.TimeSlotsRepository;
import com.doctor_service.service.DoctorService;
import jakarta.validation.constraints.Size;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RestController
@Validated
@RequestMapping("/api/v1/doctors")
public class SearchController {

    @Autowired
    private DoctorsRepository doctorsRepository;

    @Autowired
    private TimeSlotsRepository timeSlotsRepository;

    @Autowired
    private DoctorAppointmentScheduleRepository doctorAppointmentScheduleRepository;

    @Autowired
    private DoctorService doctorService;

    @GetMapping("/search-doctor")
    public ResponseEntity<?> searchDoctors(

            @RequestParam(required = false)
            @Size(min = 2, message = "Specialization must be at least 2 characters")
            String specialization,
            @RequestParam(required = false)
            @Size(min = 2, message = "Area name must be at least 2 characters")
            String areaName,
            @RequestParam(name="pageNo",required=false,defaultValue = "0")
            @Min(value = 0, message = "Page number cannot be negative")
            int pageNo,
            @RequestParam(name="pageSize", required=false,defaultValue = "5")
            @Min(value = 1, message = "Page size must be at least 1")
            int pageSize,
            @RequestParam(name="sortDir",required = false,defaultValue = "asc") String sortDir,
            @RequestParam(name="sortBy",required = false,defaultValue = "name") String sortBy
    ) {

        return ResponseEntity.ok(
                doctorService.searchDoctors(
                        specialization,
                        areaName,
                        pageNo,
                        pageSize,
                        sortDir,
                        sortBy
                )
        );
    }

    @GetMapping("/get-doctor-by-id")
    public Doctor getDoctorById(@RequestParam long id) {   // ✅ fixed variable name
        return doctorsRepository.findById(id).orElse(null);
    }
}