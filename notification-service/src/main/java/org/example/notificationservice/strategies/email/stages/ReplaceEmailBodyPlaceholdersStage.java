package org.example.notificationservice.strategies.email.stages;

import lombok.extern.slf4j.Slf4j;
import org.example.notificationservice.models.Notification;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class ReplaceEmailBodyPlaceholdersStage extends EmailNotificationChain {
    @Override
    public boolean executeStage(Notification notification, Context context) {
        log.info("Replace email body placeholders...");
        return true;
    }
}
