package org.example.notificationservice.services;

import org.example.notificationservice.models.MessageNotification;
import org.example.notificationservice.models.Notification;
import org.example.notificationservice.repositories.NotificationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Map;
import java.util.Optional;
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
        return notificationRepository.findByRecipientUserIdAndIsRead(recipientUserId, false);

    }

    // Get unread notifications grouped by sender
    public Map<String, List<Notification>> getAllUnreadNotificationsGroupedBySender(String userId) {
        List<Notification> unreadNotifications = getAllUnreadNotifications(userId);

        return unreadNotifications.stream().collect(Collectors.groupingBy(notification -> {
            if (notification instanceof MessageNotification) {
                return ((MessageNotification) notification).getSenderUserId();
            }
            return "Unknown Sender";
        }));
    }

    public List<Notification> getAllNotifications(String recipientUserId) {
        return notificationRepository.findByRecipientUserId(recipientUserId);
    }

    public int getUnreadNotificationCount(String recipientUserId) {
        return (int) notificationRepository.findByRecipientUserIdAndIsRead(recipientUserId, false)
                                           .size();
    }

    public String markNotificationAsRead(String notificationId) {
        Notification notification = notificationRepository.findById(notificationId)
                                                          .orElseThrow(() -> new IllegalArgumentException(
                                                              "Notification not found"));
        notification.setRead(true);
        notificationRepository.save(notification);
        return "Notification Marked";
    }

    public String markAllNotificationsAsRead(String recipientUserId) {
        List<Notification> notifications =
            notificationRepository.findByRecipientUserIdAndIsRead(recipientUserId, false);

        if (notifications.isEmpty()) {
            return "No notifications to mark as read";
        }
        for (Notification notification : notifications) {
            notification.setRead(true);
            notificationRepository.save(notification);
        }

        return "All unread notifications marked as read successfully";
    }

    public Notification updateNotification(String id, Notification updated) {
        Optional<Notification> existingOpt = notificationRepository.findById(id);

        if (existingOpt.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Notification not found");
        }

        return notificationRepository.save(updated);
    }

    public void deleteNotification(String id) {
        if (!notificationRepository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Notification not found");
        }
        notificationRepository.deleteById(id);
    }
}

