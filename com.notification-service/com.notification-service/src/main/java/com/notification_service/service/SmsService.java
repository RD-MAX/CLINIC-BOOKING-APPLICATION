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

        if (req.getPhone() == null || req.getPhone().trim().isEmpty()) {
            System.out.println("⚠ SMS skipped: phone number missing");
            return;
        }

        String phone = req.getPhone().trim();

        // 🔥 Ensure international format
        if (!phone.startsWith("+")) {
            phone = "+91" + phone;
        }

        String body =
                "Appointment Confirmed\n" +
                        "Doctor: " + req.getDoctorName() +
                        "\nDate: " + req.getDate() +
                        "\nTime: " + req.getTime();

        System.out.println("📱 Sending SMS to: " + phone);

        Message.creator(
                new PhoneNumber(phone),       // Patient phone
                new PhoneNumber(twilioPhone), // Twilio number
                body
        ).create();

        System.out.println("✅ SMS sent successfully");
    }

    public void sendWhatsApp(NotificationRequestDto req) {

        initTwilio();

        if (req.getPhone() == null || req.getPhone().trim().isEmpty()) {
            System.out.println("⚠ WhatsApp skipped: phone number missing");
            return;
        }

        String phone = req.getPhone().trim();

        if (!phone.startsWith("+")) {
            phone = "+91" + phone;
        }

        String body =
                "Hello " + req.getPatientName() +
                        "\nYour appointment with Dr. " + req.getDoctorName() +
                        " is confirmed." +
                        "\nDate: " + req.getDate() +
                        "\nTime: " + req.getTime();

        System.out.println("💬 Sending WhatsApp to: " + phone);

        Message.creator(
                new PhoneNumber("whatsapp:" + phone),
                new PhoneNumber(whatsappNumber),
                body
        ).create();

        System.out.println("✅ WhatsApp sent successfully");
    }
}