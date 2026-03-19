package com.doctor_service.repository;

import com.doctor_service.entity.Area;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;

public interface AreaRepository extends JpaRepository<Area, Long>  {

    List<Area> findByCityId(Long cityId);
}