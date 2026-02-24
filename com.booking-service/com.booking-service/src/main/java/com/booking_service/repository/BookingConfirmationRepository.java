package com.booking_service.repository;

import com.booking_service.entity.BookingConfirmation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.time.LocalTime;

public interface BookingConfirmationRepository extends JpaRepository<BookingConfirmation,Long> {


    boolean existsByDoctorIdAndDateAndTime(long doctorId, LocalDate date, LocalTime time);
}
