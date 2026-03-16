package com.booking_service.service;

import com.booking_service.client.BookingClient;
import com.booking_service.client.DoctorClient;
import com.booking_service.client.PatientClient;
import com.booking_service.dto.BookingConfirmationDto;
import com.booking_service.entity.Booking;
import com.booking_service.entity.BookingConfirmation;
import com.booking_service.repository.BookingConfirmationRepository;
import com.booking_service.repository.BookingRepository;
import com.doctor_service.entity.Doctor;
import com.doctor_service.entity.DoctorAppointmentSchedule;
import com.doctor_service.entity.TimeSlots;
import com.patient_service.entity.Patient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
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
// after payment --booking confirmed-kafka-
@Autowired
private BookingProducer bookingProducer;

    public BookingConfirmationDto createBooking(BookingConfirmationDto dto) {

        boolean pendingExists =
                bookingRepository.existsByDoctorIdAndDateAndTimeAndStatus(
                        dto.getDoctorId(),
                        dto.getDate(),
                        dto.getTime(),
                        "PENDING_PAYMENT"
                );

        boolean confirmedExists =
                bookingConfirmationRepository.existsByDoctorIdAndDateAndTime(
                        dto.getDoctorId(),
                        dto.getDate(),
                        dto.getTime()
                );

        if (pendingExists || confirmedExists) {
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

        // 4️⃣ Save basic booking in BOOKINGS table
        Booking rawBooking = new Booking();               // ✅ renamed variable
        rawBooking.setDoctorId(dto.getDoctorId());
        rawBooking.setPatientId(dto.getPatientId());
        rawBooking.setDate(dto.getDate());          // ✅ store date for pending booking
        rawBooking.setTime(dto.getTime());          // ✅ store time for pending booking
        rawBooking.setStatus("PENDING_PAYMENT");    // ✅ pending until payment
        rawBooking.setAmount(dto.getBookingAmount());



        bookingRepository.save(rawBooking);

        // ❌ Do NOT save into BOOKING_CONFIRMATIONS here (payment not done yet)
        // confirmation will be created after payment success

        dto.setStatus("PENDING_PAYMENT");           // ✅ send pending status to frontend
        return dto;
    }

    public BookingConfirmationDto getBookingById(Long id) {

        // 1️⃣ First try to fetch from CONFIRMED bookings
        BookingConfirmation confirmed = bookingConfirmationRepository.findById(id).orElse(null);

        if (confirmed != null) {
            return mapToDto(confirmed);
        }

        // 2️⃣ If not found, fetch from PENDING bookings
        Booking rawBooking = bookingRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Booking not found: " + id));

        // 3️⃣ Enrich pending booking using Doctor & Patient services
        Doctor doctor = doctorClient.getDoctorById(rawBooking.getDoctorId());
        Patient patient = patientClient.getPatientById(rawBooking.getPatientId());

        BookingConfirmationDto dto = new BookingConfirmationDto();
        dto.setId(rawBooking.getId());
        dto.setDoctorId(rawBooking.getDoctorId());
        dto.setPatientId(rawBooking.getPatientId());
        dto.setDate(rawBooking.getDate());
        dto.setTime(rawBooking.getTime());
        dto.setStatus(rawBooking.getStatus());

        // ✅ Enriched fields
        dto.setDoctorName(doctor.getName());
        dto.setClinicName(doctor.getName());
        dto.setPatientName(patient.getName());
        dto.setAddress(doctor.getAddress());



        // 🔥 NEW: extra fields in response
        dto.setClinicName(doctor.getClinicName());
        dto.setBookingAmount(rawBooking.getAmount());

        return dto;
    }

    public List<BookingConfirmationDto> getBookingsByPatientId(Long patientId) {
        List<BookingConfirmation> bookings =
                bookingConfirmationRepository.findByPatientId(patientId);

        List<BookingConfirmationDto> result = new ArrayList<>();
        for (BookingConfirmation b : bookings) {
            result.add(mapToDto(b));
        }
        return result;
    }

    public List<BookingConfirmationDto> getBookingsByDoctorId(Long doctorId) {
        List<BookingConfirmation> bookings =
                bookingConfirmationRepository.findByDoctorId(doctorId);

        List<BookingConfirmationDto> result = new ArrayList<>();
        for (BookingConfirmation b : bookings) {
            result.add(mapToDto(b));
        }
        return result;
    }

    // after payment--status changed to --booked-----------


    // 🔥 NEW: Confirm booking using bookingId (used by payment-service)
    @Transactional
    public BookingConfirmationDto confirmBookingById(Long bookingId) {

        // 1️⃣ Ignore duplicate webhook
        BookingConfirmation alreadyConfirmed =
                bookingConfirmationRepository.findById(bookingId).orElse(null);

        if (alreadyConfirmed != null) {
            System.out.println("⚠ Already confirmed. Ignoring duplicate webhook.");
            return mapToDto(alreadyConfirmed);
        }

        // 2️⃣ Fetch pending booking
        Booking rawBooking = bookingRepository.findById(bookingId).orElse(null);

        if (rawBooking == null) {
            System.out.println("⚠ Booking not found. Possibly already processed.");
            return null;
        }

        if (!"PENDING_PAYMENT".equals(rawBooking.getStatus())) {
            System.out.println("⚠ Booking not in PENDING state.");
            return null;
        }

        // ✅ 3️⃣ NEW: Check if slot already confirmed by someone else
        boolean confirmedExists =
                bookingConfirmationRepository.existsByDoctorIdAndDateAndTime(
                        rawBooking.getDoctorId(),
                        rawBooking.getDate(),
                        rawBooking.getTime()
                );

        if (confirmedExists) {

            System.out.println("⚠ Slot already taken. Marking booking FAILED.");

            rawBooking.setStatus("FAILED");
            bookingRepository.save(rawBooking);

            return null;  // 🔥 IMPORTANT: stop here
        }

        // 4️⃣ Normal confirmation flow
        Doctor doctor = doctorClient.getDoctorById(rawBooking.getDoctorId());
        Patient patient = patientClient.getPatientById(rawBooking.getPatientId());

        BookingConfirmation confirmation = new BookingConfirmation();
        confirmation.setId(rawBooking.getId()); // same ID
        confirmation.setDoctorId(rawBooking.getDoctorId());
        confirmation.setPatientId(rawBooking.getPatientId());
        confirmation.setDate(rawBooking.getDate());
        confirmation.setTime(rawBooking.getTime());
        confirmation.setStatus("CONFIRMED");

        confirmation.setDoctorName(doctor.getName());
        confirmation.setPatientName(patient.getName());
        confirmation.setAddress(doctor.getAddress());
        confirmation.setClinicName(doctor.getClinicName());
        confirmation.setBookingAmount(rawBooking.getAmount());

        BookingConfirmation saved =
                bookingConfirmationRepository.save(confirmation);

        rawBooking.setStatus("CONFIRMED");
        bookingRepository.save(rawBooking);

        System.out.println("✅ Booking confirmed for ID: " + bookingId);

        bookingProducer.publishBookingEvent(bookingId);
        return mapToDto(saved);
    }

//    @Transactional
//    public BookingConfirmationDto confirmBooking(BookingConfirmationDto dto) {
//
//        Doctor doctor = doctorClient.getDoctorById(dto.getDoctorId());
//        Patient patient = patientClient.getPatientById(dto.getPatientId());
//
//        // 1️⃣ Save confirmed booking
//        BookingConfirmation confirmation = mapToEntity(dto);
//        confirmation.setStatus("confirmed");
//        confirmation.setAddress(doctor.getAddress());
//        confirmation.setDoctorName(doctor.getName());
//        confirmation.setPatientName(patient.getName());
//
//        confirmation.setClinicName(doctor.getClinicName());
//        confirmation.setBookingAmount(dto.getBookingAmount());
//
//        BookingConfirmation saved = bookingConfirmationRepository.save(confirmation);
//
//        // 2️⃣ Delete pending booking
//        bookingRepository.deleteByDoctorIdAndPatientIdAndStatus(
//                dto.getDoctorId(), dto.getPatientId(), "PENDING_PAYMENT"
//        );
//
//        return mapToDto(saved);
//    }

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


        dto.setClinicName(booking.getClinicName());
        dto.setBookingAmount(booking.getBookingAmount());


        return dto;
    }
}