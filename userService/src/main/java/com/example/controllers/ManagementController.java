package com.example.controllers;

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
    public ResponseEntity<String> blockUser(@PathVariable Long blockerId, @PathVariable Long blockedId) {
        try {
            managementService.blockUser(blockerId, blockedId);
            return ResponseEntity.ok("User blocked successfully");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error blocking user: " + e.getMessage());
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


}
