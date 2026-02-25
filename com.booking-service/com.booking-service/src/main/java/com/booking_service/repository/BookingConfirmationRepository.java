package com.booking_service.repository;

import com.booking_service.entity.BookingConfirmation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public interface BookingConfirmationRepository extends JpaRepository<BookingConfirmation,Long> {

    List<BookingConfirmation> findByStatus(String status);
    List<BookingConfirmation> findByDoctorId(Long doctorId);

    List<BookingConfirmation> findByPatientId(Long patientId);
    boolean existsByDoctorIdAndDateAndTime(long doctorId, LocalDate date, LocalTime time);
}
