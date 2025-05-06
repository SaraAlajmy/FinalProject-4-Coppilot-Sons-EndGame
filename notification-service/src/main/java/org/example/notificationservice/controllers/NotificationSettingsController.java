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
    
    @GetMapping()
    public ResponseEntity<UserNotificationSettings> getUserSettings(@RequestHeader String userId) {
        return ResponseEntity.ok(notificationSettingsService.getUserNotificationSettings(userId));
    }
    
    @GetMapping("/{notificationType}/{strategyType}")
    public ResponseEntity<Boolean> getNotificationStatus(
            @RequestHeader String userId,
            @PathVariable String notificationType,
            @PathVariable String strategyType) {
        boolean enabled = notificationSettingsService.isNotificationEnabled(userId, notificationType, strategyType);
        return ResponseEntity.ok(enabled);
    }
    
    @PostMapping("/{notificationType}/{strategyType}/enable")
    public ResponseEntity<Void> enableNotification(
            @RequestHeader String userId,
            @PathVariable String notificationType,
            @PathVariable String strategyType) {
        notificationSettingsService.enableNotification(userId, notificationType, strategyType);
        return ResponseEntity.ok().build();
    }
    
    @PostMapping("/{notificationType}/{strategyType}/disable")
    public ResponseEntity<Void> disableNotification(
            @RequestHeader String userId,
            @PathVariable String notificationType,
            @PathVariable String strategyType) {
        notificationSettingsService.disableNotification(userId, notificationType, strategyType);
        return ResponseEntity.ok().build();
    }
    
    @PostMapping("/mute-all")
    public ResponseEntity<Void> muteAllNotifications(@RequestHeader String userId) {
        notificationSettingsService.muteAllNotifications(userId);
        return ResponseEntity.ok().build();
    }
    
    @PostMapping("/unmute-all")
    public ResponseEntity<Void> unmuteAllNotifications(@RequestHeader String userId) {
        notificationSettingsService.unmuteAllNotifications(userId);
        return ResponseEntity.ok().build();
    }
}
