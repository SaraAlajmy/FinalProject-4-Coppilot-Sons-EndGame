package org.example.notificationservice.controllers;

import lombok.RequiredArgsConstructor;
import org.example.notificationservice.models.Notification;
import org.example.notificationservice.services.NotificationQueryService;
import org.example.notificationservice.services.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/notifications")
@RequiredArgsConstructor
public class NotificationController {
private NotificationQueryService notificationQueryService;
private NotificationService notificationService;
@Autowired
public NotificationController(NotificationQueryService notificationQueryService) {
    this.notificationQueryService = notificationQueryService;
}

@GetMapping("/unread")
public ResponseEntity<List<Notification>> getAllUnreadNotifications(@RequestHeader String userId) {
    List<Notification> notifications = notificationQueryService.getAllUnreadNotifications(userId);
    return ResponseEntity.ok(notifications);
}
@GetMapping("/unread/grouped")
public ResponseEntity<Map<String, List<Notification>>> getAllUnreadNotificationsGroupedBySender(@RequestHeader String userId) {
    Map<String, List<Notification>> notifications = notificationQueryService.getAllUnreadNotificationsGroupedBySender(userId);
    return ResponseEntity.ok(notifications);
}
@PostMapping("/reset-password")
public ResponseEntity<String> sendResetPasswordNotification(@RequestParam String resetLink,
                                                            @RequestParam String recipientEmail,
                                                            @RequestParam String recipientName) {
     if(notificationService.sendResetPasswordNotification(resetLink, recipientEmail,recipientName))
        return ResponseEntity.ok("Reset password notification sent successfully.");
    else
        return ResponseEntity.status(500).body("Failed to send reset password notification.");
}
}
