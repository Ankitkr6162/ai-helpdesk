package com.ai.helpdesk.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    private final JavaMailSender mailSender;

    @Value("${helpdesk.admin.email}")
    private String adminEmail;

    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    // @Async ensures the user doesn't wait for the email to send before getting a reply
    @Async
    public void sendNotification(String subject, String body) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom("ai-bot@helpdesk.com");
            message.setTo(adminEmail);
            message.setSubject(subject);
            message.setText(body);

            mailSender.send(message);
            System.out.println(">>> EMAIL SENT: " + subject);
        } catch (Exception e) {
            System.err.println("FAILED TO SEND EMAIL: " + e.getMessage());
        }
    }
}
