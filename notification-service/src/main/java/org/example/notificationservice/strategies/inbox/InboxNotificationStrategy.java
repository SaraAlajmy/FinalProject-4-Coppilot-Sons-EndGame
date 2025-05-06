package org.example.notificationservice.strategies.inbox;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.notificationservice.models.Notification;
import org.example.notificationservice.models.NotificationStrategyType;
import org.example.notificationservice.strategies.NotificationStrategy;
import org.example.notificationservice.repositories.NotificationRepository;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class InboxNotificationStrategy implements NotificationStrategy {
    
    private final NotificationRepository notificationRepository;
    
    @Override
    public boolean deliver(Notification notification) {
        log.info("Delivering notification to inbox...");

        try {
            notificationRepository.save(notification);
            return true;
        } catch (Exception e) {
            log.error("Error saving notification to database", e);
            return false;
        }
    }
    
    @Override
    public String getStrategyType() {
        return NotificationStrategyType.INBOX;
    }
}
