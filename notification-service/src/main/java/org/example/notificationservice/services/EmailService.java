package org.example.notificationservice.services;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class EmailService {
    public void sendEmail(Email email) {
        // TODO: Implement email sending logic
    }

    @Data
    @Builder
    @AllArgsConstructor
    public static class Email {
        private String fromEmail;
        private String toEmail;
        private String subject;
        private String body;
    }
}
