package org.example.notificationservice.strategies.email.stages;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.notificationservice.models.Notification;
import org.example.notificationservice.services.EmailService;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class SendEmailStage extends EmailNotificationChain {
    private final EmailService emailService;

    @Override
    public boolean executeStage(Notification notification, Context context) {
        log.info("Send email to: {}", notification.getRecipientEmail());

        emailService.sendEmail(
            EmailService.Email
                .builder()
                .toEmail(notification.getRecipientEmail())
                .subject(context.getSubject())
                .body(context.getBody())
                .build()
        );

        return true;
    }
}
