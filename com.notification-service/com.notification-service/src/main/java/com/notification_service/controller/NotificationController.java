package com.notification_service.controller;

import com.notification_service.dto.NotificationRequestDto;
import com.notification_service.service.EmailService;
import com.notification_service.service.SmsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/notifications")
public class NotificationController {

    @Autowired
    private EmailService emailService;

    @Autowired
    private SmsService smsService;

    @PostMapping("/send")
    public String sendNotification(@RequestBody NotificationRequestDto request){

        emailService.sendEmail(request);
        smsService.sendSms(request);
        smsService.sendWhatsApp(request);

        return "Notification Sent Successfully";
    }
}