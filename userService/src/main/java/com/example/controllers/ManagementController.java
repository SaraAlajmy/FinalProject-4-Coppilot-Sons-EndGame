package com.example.controllers;

import com.example.services.ManagementService;
import org.springframework.beans.factory.annotation.Autowired;
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


}
