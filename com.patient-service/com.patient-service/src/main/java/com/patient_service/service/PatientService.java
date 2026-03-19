package com.patient_service.service;

import com.patient_service.dto.PatientDto;
import com.patient_service.entity.Patient;
import com.patient_service.repository.PatientRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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


    public Map<String, Object> searchPatients(
            String name,
            String gender,
            int pageNo,
            int pageSize,
            String sortDir,
            String sortBy
    ) {

        // ✅ sorting
        Sort sort = sortDir.equalsIgnoreCase("asc") ?
                Sort.by(sortBy).ascending() :
                Sort.by(sortBy).descending();

        // ✅ pagination
        Pageable pageable = PageRequest.of(pageNo, pageSize, sort);

        Page<Patient> page;

        // ✅ filtering logic (same pattern as doctor)
        if (name != null && gender != null) {
            page = patientRepository.findByGenderAndNameContainingIgnoreCase(gender, name, pageable);
        } else if (name != null) {
            page = patientRepository.findByNameContainingIgnoreCase(name, pageable);
        } else if (gender != null) {
            page = patientRepository.findByGender(gender, pageable);
        } else {
            page = patientRepository.findAll(pageable);
        }

        // ✅ convert entity -> DTO
        List<Patient> patients = page.getContent();
        List<PatientDto> result = new ArrayList<>();

        for (Patient patient : patients) {
            result.add(mapToDto(patient));
        }

        // ✅ response format (same as doctor service)
        Map<String, Object> response = new HashMap<>();
        response.put("content", result);
        response.put("page", page.getNumber());
        response.put("size", page.getSize());
        response.put("totalElements", page.getTotalElements());
        response.put("totalPages", page.getTotalPages());
        response.put("last", page.isLast());

        return response;
    }
}
