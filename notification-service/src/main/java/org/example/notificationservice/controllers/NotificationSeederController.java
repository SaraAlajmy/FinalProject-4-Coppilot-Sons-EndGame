package org.example.notificationservice.controllers;

import lombok.RequiredArgsConstructor;
import org.example.notificationservice.factories.NotificationDataFactory;
import org.example.notificationservice.models.Notification;
import org.example.notificationservice.repositories.NotificationRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Used in development and testing to create sample notifications.
 *
 * Usage:
 * - Create random notifications: POST /api/seed/notifications?count=10
 * - Create a direct message notification: POST /api/seed/notifications/direct-message
 * - Create a group message notification: POST /api/seed/notifications/group-message
 * - Create a group mention notification: POST /api/seed/notifications/group-mention
 */
@RestController
@RequestMapping("/api/seed/notifications")
@RequiredArgsConstructor
public class NotificationSeederController {
    private final NotificationRepository notificationRepository;
    private final NotificationDataFactory notificationDataFactory;

    @PostMapping
    public ResponseEntity<List<Notification>> seedNotifications(@RequestParam(defaultValue = "15") int count) {
        List<Notification> notifications = notificationDataFactory.createRandomNotifications(count);
        List<Notification> savedNotifications = notificationRepository.saveAll(notifications);
        return ResponseEntity.ok(savedNotifications);
    }

    @PostMapping("/direct-message")
    public ResponseEntity<Notification> seedDirectMessageNotification() {
        Notification notification = notificationDataFactory.createDirectMessageNotification();
        return ResponseEntity.ok(notificationRepository.save(notification));
    }

    @PostMapping("/group-message")
    public ResponseEntity<Notification> seedGroupMessageNotification() {
        Notification notification = notificationDataFactory.createGroupMessageNotification();
        return ResponseEntity.ok(notificationRepository.save(notification));
    }

    @PostMapping("/group-mention")
    public ResponseEntity<Notification> seedGroupMentionNotification() {
        Notification notification = notificationDataFactory.createGroupMentionNotification();
        return ResponseEntity.ok(notificationRepository.save(notification));
    }

    @DeleteMapping
    public ResponseEntity<Void> deleteAllNotifications() {
        notificationRepository.deleteAll();
        return ResponseEntity.noContent().build();
    }
}
