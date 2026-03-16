package com.patient_service.service;

import com.patient_service.dto.PatientDto;
import com.patient_service.entity.Patient;
import com.patient_service.repository.PatientRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class PatientService {

    @Autowired
    private PatientRepository patientRepository;

    public PatientDto savePatient(PatientDto dto) {

        // DTO -> Entity
        Patient patient = mapToEntity(dto);

        // Save to DB
        Patient saved = patientRepository.save(patient);

        // Entity -> DTO
        return mapToDto(saved);
    }

    public PatientDto getPatientById(Long id) {

        Patient patient = patientRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Patient not found with id: " + id));

        return mapToDto(patient);
    }

    public List<PatientDto> getAllPatients() {

        List<Patient> patients = patientRepository.findAll();
        List<PatientDto> result = new ArrayList<>();

        for (Patient patient : patients) {
            PatientDto dto = mapToDto(patient);   // reuse mapper
            result.add(dto);
        }

        return result;
    }

    // 🔁 Mapper methods

    private PatientDto mapToDto(Patient patient) {
        PatientDto dto = new PatientDto();
        dto.setId(patient.getId());
        dto.setName(patient.getName());
        dto.setEmail(patient.getEmail());
        dto.setPhone(patient.getPhone());
        dto.setAge(patient.getAge());
        dto.setGender(patient.getGender());
        return dto;
    }

    private Patient mapToEntity(PatientDto dto) {
        Patient patient = new Patient();
        patient.setName(dto.getName());
        patient.setEmail(dto.getEmail());
        patient.setPhone(dto.getPhone());
        patient.setAge(dto.getAge());
        patient.setGender(dto.getGender());
        return patient;
    }
}
