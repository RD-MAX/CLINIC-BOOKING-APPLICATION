package com.booking_service.service;
import com.booking_service.client.DoctorClient;
import com.booking_service.client.PatientClient;
import com.booking_service.dto.BookingConfirmationDto;
import com.booking_service.entity.BookingConfirmation;
import com.booking_service.repository.BookingConfirmationRepository;
import com.booking_service.repository.BookingRepository;
import com.doctor_service.entity.Doctor;
import com.doctor_service.entity.DoctorAppointmentSchedule;
import com.doctor_service.entity.TimeSlots;
import com.patient_service.entity.Patient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class BookingService {

    @Autowired
    private BookingRepository bookingRepository;

    //===================================confrimation after payment

    @Autowired
    private BookingConfirmationRepository bookingConfirmationRepository;

    @Autowired
    private DoctorClient doctorClient;

    @Autowired
    private PatientClient patientClient;


    public BookingConfirmationDto createBooking(BookingConfirmationDto dto) {



        // 🔒 Validate slot availability (prevent duplicate booking)
        boolean exists = bookingConfirmationRepository.existsByDoctorIdAndDateAndTime(
                dto.getDoctorId(),
                dto.getDate(),
                dto.getTime()
        );

        if (exists) {
            throw new RuntimeException("Slot already booked for this doctor at selected date & time");
        }



        // 1️⃣ Validate doctor exists
        Doctor doctor = doctorClient.getDoctorById(dto.getDoctorId());
        if (doctor == null) {
            throw new RuntimeException("Doctor not found");
        }

// 2️⃣ Validate patient exists
        Patient patient = patientClient.getPatientById(dto.getPatientId());
        if (patient == null) {
            throw new RuntimeException("Patient not found");
        }

// 3️⃣ Validate slot exists in doctor's schedule
        boolean slotFound = false;

        for (DoctorAppointmentSchedule schedule : doctor.getAppointmentSchedules()) {
            if (schedule.getDate().isEqual(dto.getDate())) {
                for (TimeSlots slot : schedule.getTimeSlots()) {
                    if (slot.getTime().equals(dto.getTime())) {
                        slotFound = true;
                        break;
                    }
                }
            }
        }

        if (!slotFound) {
            throw new RuntimeException("Selected slot is not available for this doctor");
        }


        BookingConfirmation booking = mapToEntity(dto);
        booking.setStatus("BOOKED");   // initial status before payment

        BookingConfirmation saved = bookingRepository.save(booking);
        return mapToDto(saved);
    }




    public BookingConfirmationDto getBookingById(Long id) {
        BookingConfirmation booking = bookingRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Booking not found: " + id));
        return mapToDto(booking);
    }

    public List<BookingConfirmationDto> getBookingsByPatientId(Long patientId) {
        List<BookingConfirmation> bookings = bookingRepository.findByPatientId(patientId);
        List<BookingConfirmationDto> result = new ArrayList<>();

        for (BookingConfirmation b : bookings) {
            result.add(mapToDto(b));
        }
        return result;
    }

    public List<BookingConfirmationDto> getBookingsByDoctorId(Long doctorId) {
        List<BookingConfirmation> bookings = bookingRepository.findByDoctorId(doctorId);
        List<BookingConfirmationDto> result = new ArrayList<>();

        for (BookingConfirmation b : bookings) {
            result.add(mapToDto(b));
        }
        return result;
    }

    // 🔁 Mappers
    private BookingConfirmation mapToEntity(BookingConfirmationDto dto) {
        BookingConfirmation booking = new BookingConfirmation();
        booking.setPatientId(dto.getPatientId());
        booking.setDoctorId(dto.getDoctorId());
        booking.setDate(dto.getDate());
        booking.setTime(dto.getTime());
        booking.setStatus(dto.getStatus());
        return booking;
    }

    private BookingConfirmationDto mapToDto(BookingConfirmation booking) {
        BookingConfirmationDto dto = new BookingConfirmationDto();
        dto.setId(booking.getId());
        dto.setPatientId(booking.getPatientId());
        dto.setDoctorId(booking.getDoctorId());
        dto.setDate(booking.getDate());
        dto.setTime(booking.getTime());
        dto.setStatus(booking.getStatus());
        return dto;
    }
}

