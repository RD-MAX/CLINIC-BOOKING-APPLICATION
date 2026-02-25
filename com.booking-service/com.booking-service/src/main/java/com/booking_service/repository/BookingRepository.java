package com.booking_service.repository;

import com.booking_service.entity.Booking;
//import com.booking_service.entity.BookingConfirmation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {

    //------------- updated to check in database if any booking exits with folowiing details
    boolean existsByDoctorIdAndDateAndTimeAndStatus(
            long doctorId, LocalDate date, LocalTime time, String status
    );
//to delete from database-----------
    void deleteByDoctorIdAndPatientIdAndStatus(
            long doctorId, long patientId, String status
    );
}
