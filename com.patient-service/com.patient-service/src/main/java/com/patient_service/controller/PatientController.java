package com.patient_service.controller;

import com.patient_service.dto.PatientDto;
import com.patient_service.entity.Patient;
import com.patient_service.repository.PatientRepository;
import com.patient_service.service.PatientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/patients")
public class PatientController {

@Autowired
    private PatientRepository patientRepository;
@Autowired
    private PatientService patientService;


@PostMapping("/save-patient")
    public PatientDto savePatient(@RequestBody PatientDto dto){
    return  patientService.savePatient(dto);   // returns 200 OK by default;
}

@GetMapping("/getpatientbyid")
    public PatientDto getPatientById(@RequestParam  Long id){
    return patientService.getPatientById(id);
}

@GetMapping("/getallpatients")
    public List< PatientDto> getAllPatients() {
return patientService.getAllPatients();
}

}
