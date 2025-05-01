package org.example.notificationservice.controllers;

import lombok.RequiredArgsConstructor;
import org.example.notificationservice.models.Notification;
import org.example.notificationservice.services.NotificationQueryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/notifications")
@RequiredArgsConstructor
public class NotificationController {
private NotificationQueryService notificationQueryService;
@Autowired
public NotificationController(NotificationQueryService notificationQueryService) {
    this.notificationQueryService = notificationQueryService;
}

@GetMapping("/unread")
public ResponseEntity<List<Notification>> getAllUnreadNotifications(@RequestParam String userId) {
    List<Notification> notifications = notificationQueryService.getAllUnreadNotifications(userId);
    return ResponseEntity.ok(notifications);
}
@GetMapping("/unread/grouped")
public ResponseEntity<Map<String, List<Notification>>> getAllUnreadNotificationsGroupedBySender(@RequestParam String userId) {
    Map<String, List<Notification>> notifications = notificationQueryService.getAllUnreadNotificationsGroupedBySender(userId);
    return ResponseEntity.ok(notifications);
}
}
