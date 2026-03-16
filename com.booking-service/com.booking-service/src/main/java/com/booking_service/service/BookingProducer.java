package com.booking_service.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class BookingProducer {

    private static final String TOPIC = "booking-confirmed";

    @Autowired
    private KafkaTemplate<String, Long> kafkaTemplate;

    public void publishBookingEvent(Long bookingId) {

        kafkaTemplate.send(TOPIC, bookingId);

        System.out.println("📨 Kafka event sent for bookingId: " + bookingId);
    }
}