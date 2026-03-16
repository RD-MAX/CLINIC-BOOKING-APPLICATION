package com.doctor_service.repository;

import com.doctor_service.entity.Doctor;
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
    List<Doctor> findBySpecializationAndArea_Name(
            @Param("specialization") String specialization,
            @Param("areaName") String areaName
    );
}