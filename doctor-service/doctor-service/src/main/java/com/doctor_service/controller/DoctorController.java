package com.doctor_service.controller;


import com.doctor_service.entity.Doctor;
import com.doctor_service.entity.DoctorAppointmentSchedule;
import com.doctor_service.entity.TimeSlots;
import com.doctor_service.repository.DoctorsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/doctors")
public class DoctorController {

    @Autowired
    private DoctorsRepository doctorsRepository;

  @PostMapping("/save-doctor")
    public ResponseEntity<Doctor> saveDoctor(@RequestBody Doctor doctor) {

        if(doctor.getAppointmentSchedules() !=null){
            doctor.getAppointmentSchedules().forEach(schedule ->
            {
                schedule.setDoctor(doctor);

            if(schedule.getTimeSlots() != null){
                schedule.getTimeSlots().forEach(slot-> slot.setDoctorAppointmentSchedule(schedule));
            }
            });

        }
        Doctor saved= doctorsRepository.save(doctor);
  return new ResponseEntity<>(saved,HttpStatus.CREATED);
  }}




