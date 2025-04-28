package com.example.controllers;

import com.example.models.NotificationSettings;
import com.example.services.ManagementService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/manageUser")
public class ManagementController {

    private final ManagementService managementService;

    @Autowired
    public ManagementController(ManagementService managementService) {
        this.managementService = managementService;
    }

    @PutMapping("/block/{blockerId}/{blockedId}")
    public String blockUser(@PathVariable Long blockerId, @PathVariable Long blockedId) {
        try {
            managementService.blockUser(blockerId, blockedId);
            return "User blocked successfully";
        } catch (Exception e) {
            return "Error blocking user: " + e.getMessage();
        }
    }

    @PutMapping("/unblock/{blockerId}/{blockedId}")
    public String unblockUser(@PathVariable Long blockerId, @PathVariable Long blockedId) {
        try {
            managementService.unBlockUser(blockerId, blockedId);
            return "User unblocked successfully";
        } catch (Exception e) {
            return "Error unblocking user: " + e.getMessage();
        }
    }

    @PutMapping("/mute/{userId}")
    public String muteNotifications(@PathVariable Long userId) {
        try {
            managementService.muteNotifications(userId);
            return "Notifications muted successfully";
        } catch (Exception e) {
            return "Error muting notifications: " + e.getMessage();
        }
    }

    @PutMapping("/unmute/{userId}")
    public String unmuteNotifications(@PathVariable Long userId) {
        try {
            managementService.unmuteNotifications(userId);
            return "Notifications unmuted successfully";
        } catch (Exception e) {
            return "Error unmuting notifications: " + e.getMessage();
        }
    }

    @PutMapping("/updateNotificationSettings/{userId}")
    public ResponseEntity<String> updateNotificationSettings(
            @PathVariable Long userId,
            @RequestBody NotificationSettings updateDTO) {
        try {
            managementService.updateNotificationSettings(userId, updateDTO);
            return ResponseEntity.ok("Notification settings updated successfully");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error updating notification settings: " + e.getMessage());
        }
    }


}
