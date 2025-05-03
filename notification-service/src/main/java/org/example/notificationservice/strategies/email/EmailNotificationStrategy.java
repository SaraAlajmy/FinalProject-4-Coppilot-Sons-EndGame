package org.example.notificationservice.strategies.email;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.notificationservice.models.Notification;
import org.example.notificationservice.models.NotificationStrategyType;
import org.example.notificationservice.strategies.NotificationStrategy;
import org.example.notificationservice.strategies.email.stages.EmailNotificationChain;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class EmailNotificationStrategy implements NotificationStrategy {
    private final EmailNotificationChain emailNotificationStage;

    @Override
    public boolean deliver(Notification notification) {
        log.info("Delivering notification to email...");
        emailNotificationStage.execute(notification, new EmailNotificationChain.Context());
        return true;
    }

    @Override
    public String getStrategyType() {
        return NotificationStrategyType.EMAIL;
    }
}
