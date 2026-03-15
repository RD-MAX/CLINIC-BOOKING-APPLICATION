package com.notification_service.service;

import com.notification_service.dto.NotificationRequestDto;
import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class SmsService {

    @Value("${twilio.account.sid}")
    private String accountSid;

    @Value("${twilio.auth.token}")
    private String authToken;

    @Value("${twilio.phone.number}")
    private String twilioPhone;

    @Value("${twilio.whatsapp.number}")
    private String whatsappNumber;

    private void initTwilio() {
        Twilio.init(accountSid, authToken);
    }

    public void sendSms(NotificationRequestDto req) {

        initTwilio();

        String body =
                "Appointment Confirmed\n" +
                        "Doctor: " + req.getDoctorName() +
                        "\nDate: " + req.getDate() +
                        "\nTime: " + req.getTime();

        Message.creator(
                new PhoneNumber(req.getPhone()),      // Patient phone
                new PhoneNumber(twilioPhone),         // Twilio number
                body
        ).create();
    }

    public void sendWhatsApp(NotificationRequestDto req) {

        initTwilio();

        String body =
                "Hello " + req.getPatientName() +
                        "\nYour appointment with Dr. " + req.getDoctorName() +
                        " is confirmed." +
                        "\nDate: " + req.getDate() +
                        "\nTime: " + req.getTime();

        Message.creator(
                new PhoneNumber("whatsapp:" + req.getPhone()),  // Patient WhatsApp
                new PhoneNumber(whatsappNumber),                // Twilio WhatsApp number
                body
        ).create();
    }
}