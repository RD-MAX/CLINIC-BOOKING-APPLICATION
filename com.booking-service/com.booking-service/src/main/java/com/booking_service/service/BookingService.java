package com.booking_service.service;
import com.booking_service.dto.BookingConfirmationDto;
import com.booking_service.entity.BookingConfirmation;
import com.booking_service.repository.BookingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class BookingService {

    @Autowired
    private BookingRepository bookingRepository;

    public BookingConfirmationDto createBooking(BookingConfirmationDto dto) {

        BookingConfirmation booking = mapToEntity(dto);
         booking.setStatus("BOOKED");

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
        List<BookingConfirmation> bookings = bookingRepository.findByPatientId(doctorId);
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

