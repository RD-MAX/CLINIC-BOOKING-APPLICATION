package com.doctor_service.controller;


import com.doctor_service.entity.Doctor;
import com.doctor_service.entity.DoctorAppointmentSchedule;
import com.doctor_service.entity.TimeSlots;
import com.doctor_service.repository.DoctorsRepository;
import com.doctor_service.service.S3Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

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
  }


//----------------------for uploading pictures of profiles---------using s3------



    @Autowired
        private final S3Service s3Service;

        public DoctorController(S3Service s3Service) {
            this.s3Service = s3Service;
        }

        @PostMapping("/upload")
        public ResponseEntity<String> uploadPhoto(@RequestParam("file") MultipartFile file ,@RequestParam long doctorId)
                throws IOException {

            String url = s3Service.uploadFile(file);

            // to save photos in s3 bucket
            Doctor doctor=doctorsRepository.findById(doctorId).orElseThrow(() -> new RuntimeException("Doctor not found"));;
            doctor.setUrl(url);
            doctorsRepository.save(doctor);

            return ResponseEntity.ok(url);

        }
}




