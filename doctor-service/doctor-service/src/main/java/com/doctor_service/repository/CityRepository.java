package com.doctor_service.repository;

import com.doctor_service.entity.City;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CityRepository extends JpaRepository<City, Long> {

    List<City> findByStateId(Long stateId);
}