package com.booking_service.repository;

import com.booking_service.entity.BookingConfirmation;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public interface BookingConfirmationRepository extends JpaRepository<BookingConfirmation,Long> {



    boolean existsByDoctorIdAndDateAndTime(long doctorId, LocalDate date, LocalTime time);

    Page<BookingConfirmation> findByDoctorId(Long doctorId, Pageable pageable);

    Page<BookingConfirmation> findByDoctorIdAndStatus(Long doctorId, String status, Pageable pageable);

    Page<BookingConfirmation> findByPatientId(Long patientId, Pageable pageable);

    Page<BookingConfirmation> findByPatientIdAndStatus(Long patientId, String status, Pageable pageable);
}