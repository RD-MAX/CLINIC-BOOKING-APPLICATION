package com.booking_service.repository;

import com.booking_service.entity.Booking;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.time.LocalTime;

public interface BookingRepository extends JpaRepository<Booking, Long> {

    boolean existsByDoctorIdAndDateAndTimeAndStatus(
            long doctorId, LocalDate date, LocalTime time, String status
    );

    void deleteByDoctorIdAndPatientIdAndStatus(
            long doctorId, long patientId, String status
    );
}