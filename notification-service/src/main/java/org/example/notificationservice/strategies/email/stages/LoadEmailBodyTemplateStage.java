package org.example.notificationservice.strategies.email.stages;

import lombok.extern.slf4j.Slf4j;
import org.example.notificationservice.models.Notification;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class LoadEmailBodyTemplateStage extends EmailNotificationChain {

    @Override
    public boolean executeStage(Notification notification, Context context) {
        log.info("Load email body template...");
        return true;
    }
}
