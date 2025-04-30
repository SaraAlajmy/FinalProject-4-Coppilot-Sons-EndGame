package org.example.notificationservice.strategies.email.stages;

import lombok.extern.slf4j.Slf4j;
import org.example.notificationservice.models.Notification;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class SendEmailStage extends EmailNotificationChain {
    @Override
    public boolean executeStage(Notification notification, Context context) {
        log.info("Send email...");
        return true;
    }
}
