package org.example.notificationservice.services;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    public void sendEmail(Email email) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(email.getToEmail());
        message.setSubject(email.getSubject());
        message.setText(email.getBody());

        mailSender.send(message);
        log.info("Email sent to {}", email.getToEmail());
    }

    @Data
    @Builder
    @AllArgsConstructor
    public static class Email {
        private String toEmail;
        private String subject;
        private String body;
    }
}

