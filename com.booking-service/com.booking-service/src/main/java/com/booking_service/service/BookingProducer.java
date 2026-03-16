package com.booking_service.service;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class BookingProducer {

    private static final String TOPIC = "booking-confirmed";

    private final KafkaTemplate<String, String> kafkaTemplate;

    public BookingProducer(KafkaTemplate<String, String> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void publishBookingEvent(Long bookingId) {

        kafkaTemplate.send(TOPIC, bookingId.toString());

        System.out.println("📨 Kafka event sent for bookingId: " + bookingId);
    }
}