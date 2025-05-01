package org.example.notificationservice.services;

import org.example.notificationservice.models.MessageNotification;
import org.example.notificationservice.models.Notification;
import org.example.notificationservice.repositories.NotificationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class NotificationQueryService {

    private final NotificationRepository notificationRepository;

    @Autowired
    public NotificationQueryService(NotificationRepository notificationRepository) {
        this.notificationRepository = notificationRepository;
    }

    // Get all notifications for a user
    public List<Notification> getAllUnreadNotifications(String recipientUserId) {
        return notificationRepository.findByRecipientUserIdAndIsRead(recipientUserId,false);

    }

    // Get unread notifications grouped by sender
    public Map<String, List<Notification>> getAllUnreadNotificationsGroupedBySender(String userId) {
        List<Notification> unreadNotifications = getAllUnreadNotifications(userId);

        return unreadNotifications.stream()
                .collect(Collectors.groupingBy(notification -> {
                    if (notification instanceof MessageNotification) {
                        return ((MessageNotification) notification).getSenderUserId();
                    }
                    return "Unknown Sender";
                }));
    }
}
