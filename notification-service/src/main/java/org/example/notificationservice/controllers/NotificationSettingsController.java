package org.example.notificationservice.controllers;

import lombok.RequiredArgsConstructor;
import org.example.notificationservice.models.UserNotificationSettings;
import org.example.notificationservice.services.NotificationSettingsService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/notification-settings")
@RequiredArgsConstructor
public class NotificationSettingsController {
    private final NotificationSettingsService notificationSettingsService;
    
    @GetMapping("/{userId}")
    public ResponseEntity<UserNotificationSettings> getUserSettings(@PathVariable String userId) {
        return ResponseEntity.ok(notificationSettingsService.getUserNotificationSettings(userId));
    }
    
    @GetMapping("/{userId}/{notificationType}/{strategyType}")
    public ResponseEntity<Boolean> getNotificationStatus(
            @PathVariable String userId,
            @PathVariable String notificationType,
            @PathVariable String strategyType) {
        boolean enabled = notificationSettingsService.isNotificationEnabled(userId, notificationType, strategyType);
        return ResponseEntity.ok(enabled);
    }
    
    @PostMapping("/{userId}/{notificationType}/{strategyType}/enable")
    public ResponseEntity<Void> enableNotification(
            @PathVariable String userId,
            @PathVariable String notificationType,
            @PathVariable String strategyType) {
        notificationSettingsService.enableNotification(userId, notificationType, strategyType);
        return ResponseEntity.ok().build();
    }
    
    @PostMapping("/{userId}/{notificationType}/{strategyType}/disable")
    public ResponseEntity<Void> disableNotification(
            @PathVariable String userId,
            @PathVariable String notificationType,
            @PathVariable String strategyType) {
        notificationSettingsService.disableNotification(userId, notificationType, strategyType);
        return ResponseEntity.ok().build();
    }
    
    @PostMapping("/{userId}/mute-all")
    public ResponseEntity<Void> muteAllNotifications(@PathVariable String userId) {
        notificationSettingsService.muteAllNotifications(userId);
        return ResponseEntity.ok().build();
    }
    
    @PostMapping("/{userId}/unmute-all")
    public ResponseEntity<Void> unmuteAllNotifications(@PathVariable String userId) {
        notificationSettingsService.unmuteAllNotifications(userId);
        return ResponseEntity.ok().build();
    }
}
