package com.doctor_service.controller;

import com.doctor_service.dto.SearchResultDto;
import com.doctor_service.entity.Doctor;
import com.doctor_service.entity.DoctorAppointmentSchedule;
import com.doctor_service.entity.TimeSlots;
import com.doctor_service.repository.DoctorAppointmentScheduleRepository;
import com.doctor_service.repository.DoctorsRepository;
import com.doctor_service.repository.TimeSlotsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/v1/doctor")
public class SearchController {

    @Autowired
    private DoctorsRepository doctorsRepository;

    @Autowired
    private TimeSlotsRepository timeSlotsRepository;

    @Autowired
    private DoctorAppointmentScheduleRepository doctorAppointmentScheduleRepository;

    @GetMapping("/search-doctor")
    public ResponseEntity<List<SearchResultDto>> searchDoctors(
            @RequestParam String specialization,
            @RequestParam String areaName
    ) {
        LocalDate currentDate = LocalDate.now();
        List<SearchResultDto> result = new ArrayList<>();
        List<Doctor> doctors = doctorsRepository.findBySpecializationAndArea(specialization, areaName);

        for (Doctor doctor : doctors) {
            SearchResultDto dto = new SearchResultDto();

            List<LocalDate> validDates = new ArrayList<>();
            List<LocalTime> allTimeSlots = new ArrayList<>();
            List<DoctorAppointmentSchedule> schedules = doctor.getAppointmentSchedules();  // ✅ fixed

            for (DoctorAppointmentSchedule schedule : schedules) {
                LocalDate scheduleDate = schedule.getDate();
                LocalTime now = LocalTime.now();

                List<TimeSlots> timeSlots = timeSlotsRepository.getAllTimeSlots(schedule.getId());

                for (TimeSlots ts : timeSlots) {
                    LocalTime slotTime = ts.getTime();

                    if (scheduleDate.isEqual(currentDate)) {
                        if (slotTime.isAfter(now)) {
                            allTimeSlots.add(slotTime);
                        }
                    }
                    else if ( scheduleDate.isBefore(currentDate)){
                        System.out.println("invalid date");
                    }
                    // If schedule is in the future
                    else if (scheduleDate.isAfter(currentDate)) {   // ✅ fixed condition
                        allTimeSlots.add(slotTime);
                    }
                }
            }

            dto.setId(doctor.getId());
            dto.setUrl(doctor.getUrl());

            dto.setName(doctor.getName());
            dto.setArea(doctor.getArea().getName());
            dto.setCity(doctor.getCity().getName());
            dto.setQualification(doctor.getQualification());
            dto.setSpecialization(doctor.getSpecialization());
            dto.setDates(validDates);
            dto.setTimeSlots(allTimeSlots);

            result.add(dto);
        }

        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @GetMapping("/get-doctor-by-id")
    public Doctor getDoctorById(@RequestParam long id) {   // ✅ fixed variable name
        return doctorsRepository.findById(id).get();
    }
}
