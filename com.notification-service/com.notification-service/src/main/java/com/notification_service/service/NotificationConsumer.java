package com.notification_service.service;

import com.notification_service.client.BookingClient;
import com.notification_service.dto.BookingConfirmationDto;
import com.notification_service.dto.NotificationRequestDto;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class NotificationConsumer {

    private final EmailService emailService;
    private final SmsService smsService;
    private final BookingClient bookingClient;

    @Autowired
    public NotificationConsumer(EmailService emailService,
                                SmsService smsService,
                                BookingClient bookingClient) {
        this.emailService = emailService;
        this.smsService = smsService;
        this.bookingClient = bookingClient;
    }

    @KafkaListener(topics = "booking-confirmed", groupId = "notification-group")
    public void consume(String message) {

        System.out.println("📩 Received Kafka message: " + message);

        Long bookingId;

        try {
            bookingId = Long.parseLong(message.trim());
        } catch (NumberFormatException e) {
            System.out.println("❌ Invalid booking ID received from Kafka: " + message);
            return;
        }

        try {
            // Fetch booking details from booking-service
            BookingConfirmationDto booking = bookingClient.getBookingById(bookingId);

            if (booking == null) {
                System.out.println("❌ Booking not found for ID: " + bookingId);
                return;
            }

            NotificationRequestDto notification = new NotificationRequestDto();
            notification.setDoctorName(booking.getDoctorName());
            notification.setPatientName(booking.getPatientName());
            notification.setDate(booking.getDate());
            notification.setTime(booking.getTime());
            notification.setEmail(booking.getEmail());
            notification.setPhone(booking.getPhone());

            // Send notifications
            emailService.sendEmail(notification);
            smsService.sendSms(notification);
            smsService.sendWhatsApp(notification);

            System.out.println("✅ Notifications sent for booking ID: " + bookingId);

        } catch (Exception ex) {
            System.out.println("❌ Error processing booking ID: " + bookingId);
            ex.printStackTrace();
        }
    }
}