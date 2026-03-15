package com.notification_service.service;

import com.notification_service.dto.NotificationRequestDto;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    public void sendEmail(NotificationRequestDto req){

        SimpleMailMessage message = new SimpleMailMessage();

        message.setTo(req.getEmail());
        message.setSubject("Appointment Confirmation");

        message.setText(
                "Hello " + req.getPatientName() +
                        "\nYour appointment with Dr. " + req.getDoctorName() +
                        "\nClinic: " + req.getClinicName() +
                        "\nDate: " + req.getDate() +
                        "\nTime: " + req.getTime() +
                        "\n\nStatus: CONFIRMED"
        );

        mailSender.send(message);
    }
}