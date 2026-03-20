package com.doctor_service.controller;

import com.doctor_service.entity.Doctor;
import com.doctor_service.entity.DoctorAppointmentSchedule;
import com.doctor_service.entity.TimeSlots;
import com.doctor_service.repository.DoctorsRepository;
import com.doctor_service.service.S3Service;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@Validated
@RequestMapping("/api/v1/doctors")
public class DoctorController {

    private final DoctorsRepository doctorsRepository;
    private final S3Service s3Service;

    public DoctorController(DoctorsRepository doctorsRepository, S3Service s3Service) {
        this.doctorsRepository = doctorsRepository;
        this.s3Service = s3Service;
    }

  @PostMapping( "/save-doctor")
    public ResponseEntity<Doctor> saveDoctor(@Valid @RequestBody Doctor doctor) {

        if(doctor.getAppointmentSchedules() !=null){
            for (DoctorAppointmentSchedule schedule : doctor.getAppointmentSchedules()) {

                // ✅ FIX 1: link schedule → doctor
                schedule.setDoctor(doctor);

                if (schedule.getTimeSlots() != null) {

                    for (TimeSlots slot : schedule.getTimeSlots()) {

                        // ✅ FIX 2: link slot → schedule
                        slot.setDoctorAppointmentSchedule(schedule);
                    }
                }
            }

        }
        Doctor saved= doctorsRepository.save(doctor);
  return new ResponseEntity<>(saved,HttpStatus.CREATED);
  }

//----------------------for uploading pictures of profiles---------using s3------

    @PostMapping("/upload")
        public ResponseEntity<String> uploadPhoto(@RequestParam("file") MultipartFile file ,@RequestParam long doctorId)
                throws IOException {

            String url = s3Service.uploadFile(file);

            // to save photos in s3 bucket
            Doctor doctor=doctorsRepository.findById(doctorId).orElseThrow(() -> new RuntimeException("Doctor not found"));
            doctor.setUrl(url);
            doctorsRepository.save(doctor);

            return ResponseEntity.ok(url);

        }
}




