package org.example.notificationservice.controllers;

import lombok.RequiredArgsConstructor;
import org.example.notificationservice.models.DirectMessageNotification;
import org.example.notificationservice.models.GroupMentionNotification;
import org.example.notificationservice.models.GroupMessageNotification;
import org.example.notificationservice.models.Notification;
import org.example.notificationservice.repositories.NotificationRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/test-api/notifications")
@RequiredArgsConstructor
public class NotificationController {
    private final NotificationRepository notificationRepository;

    @GetMapping
    public ResponseEntity<List<Notification>> getAllNotifications() {
        List<Notification> notifications = notificationRepository.findAll();
        // Log the class type of each notification to verify correct deserialization
        notifications.forEach(notification ->
                                  System.out.println("Notification type: " +
                                                     notification.getClass().getSimpleName() +
                                                     ", DB type: " + notification.getType()));
        return ResponseEntity.ok(notifications);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Notification> getNotificationById(@PathVariable String id) {
        return notificationRepository.findById(id)
                                     .map(notification -> {
                                         System.out.println("Retrieved notification class: " +
                                                            notification.getClass()
                                                                        .getSimpleName());
                                         return ResponseEntity.ok(notification);
                                     })
                                     .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Notification> createNotification(@RequestBody Notification notification) {
        System.out.println(
            "Received notification class: " + notification.getClass().getSimpleName());
        Notification savedNotification = notificationRepository.save(notification);
        return ResponseEntity.ok(savedNotification);
    }

    // Test endpoint to create sample notifications of different types
    @PostMapping("/test")
    public ResponseEntity<List<Notification>> createTestNotifications() {
        LocalDateTime now = LocalDateTime.now();

        DirectMessageNotification directMsg = DirectMessageNotification.builder()
                                                                       .recipientUserId("user123")
                                                                       .timestamp(now)
                                                                       .isRead(false)
                                                                       .type("direct_message")
                                                                       .senderUserId("sender456")
                                                                       .senderName("John Doe")
                                                                       .messageId("msg789")
                                                                       .messageText("Hello there!")
                                                                       .messageTimestamp(now)
                                                                       .build();

        GroupMessageNotification groupMsg = GroupMessageNotification.builder()
                                                                    .recipientUserId("user123")
                                                                    .timestamp(now)
                                                                    .isRead(false)
                                                                    .type("group_message")
                                                                    .build();

        GroupMentionNotification mention = GroupMentionNotification.builder()
                                                                   .recipientUserId("user123")
                                                                   .timestamp(now)
                                                                   .isRead(false)
                                                                   .type("group_mention")
                                                                   .build();

        List<Notification> saved =
            notificationRepository.saveAll(List.of(directMsg, groupMsg, mention));
        return ResponseEntity.ok(saved);
    }
}
