package com.doctor_service.repository;

import com.doctor_service.entity.Doctor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface DoctorsRepository extends JpaRepository<Doctor, Long> {

    @Query("""
        SELECT d FROM Doctor d
        JOIN d.area a
        WHERE LOWER(d.specialization) = LOWER(:specialization)
        AND LOWER(a.name) = LOWER(:areaName)
    """)
    Page<Doctor> findBySpecializationAndArea_Name(
            String specialization,
            String areaName,
            Pageable pageable
    );

    Page<Doctor> findBySpecialization(
            String specialization,
            Pageable pageable
    );

    Page<Doctor> findByArea_Name(
            String areaName,
            Pageable pageable
    );

}