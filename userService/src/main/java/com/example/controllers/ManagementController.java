package com.example.controllers;

import com.example.services.ManagementService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/manageUser")
public class ManagementController {

    private final ManagementService managementService;

    @Autowired
    public ManagementController(ManagementService managementService) {
        this.managementService = managementService;
    }

    @PutMapping("/block/{blockerId}/{blockedId}")
    public ResponseEntity<String> blockUser(@PathVariable Long blockerId, @PathVariable Long blockedId) {
        try {
            managementService.blockUser(blockerId, blockedId);
            return ResponseEntity.ok("User blocked successfully");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error blocking user: " + e.getMessage());
        }
    }

    @PutMapping("/unblock/{blockerId}/{blockedId}")
    public ResponseEntity<String> unblockUser(@PathVariable Long blockerId, @PathVariable Long blockedId) {
        try {
            managementService.unBlockUser(blockerId, blockedId);
            return ResponseEntity.ok("User unblocked successfully");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error unblocking user: " + e.getMessage());
        }
    }

    @PutMapping("/mute/{userId}")
    public ResponseEntity<String> muteNotifications(@PathVariable Long userId) {
        try {
            managementService.muteNotifications(userId);
            return ResponseEntity.ok("Notifications muted successfully");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error muting notifications: " + e.getMessage());
        }
    }

    @PutMapping("/unmute/{userId}")
    public ResponseEntity<String> unmuteNotifications(@PathVariable Long userId) {
        try {
            managementService.unmuteNotifications(userId);
            return ResponseEntity.ok("Notifications unmuted successfully");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error unmuting notifications: " + e.getMessage());
        }
    }


}
