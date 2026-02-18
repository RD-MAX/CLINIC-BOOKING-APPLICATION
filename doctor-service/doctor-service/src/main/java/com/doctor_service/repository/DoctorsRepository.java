package com.doctor_service.repository;

import com.doctor_service.entity.Doctor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface DoctorsRepository extends JpaRepository<Doctor, Long>  {

    @Query("select d from Doctor d"+ " where lower(d.specialization) = lower(:specialization)"+" and lower(d.area.name)= lower(:areaName)")
     List<Doctor> findBySpecializationAndArea(@Param("specialization") String specialization ,@Param("areaName") String areaName);
}