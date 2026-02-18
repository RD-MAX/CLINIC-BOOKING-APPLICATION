package com.booking_service.repository;

import com.booking_service.entity.BookingConfirmation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BookingRepository extends JpaRepository <BookingConfirmation,Long> {

    List<BookingConfirmation> findByStatus(String status);

    List<BookingConfirmation> findByDoctorId(Long doctorId);

    List<BookingConfirmation> findByPatientId(Long patientId);
}
