package org.example.notificationservice.strategies;

import org.example.notificationservice.models.Notification;
import org.springframework.stereotype.Component;

@Component
public interface NotificationStrategy {
    boolean deliver(Notification notification);

    String getStrategyType();
}
