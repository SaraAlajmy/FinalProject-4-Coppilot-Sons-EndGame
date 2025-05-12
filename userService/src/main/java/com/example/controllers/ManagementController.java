package com.example.controllers;

import com.example.services.ManagementService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/manageUser")
public class ManagementController {

    private final ManagementService managementService;

    @Autowired
    public ManagementController(ManagementService managementService) {
        this.managementService = managementService;
    }

    @PutMapping("/block/{blockedId}")
    public ResponseEntity<String> blockUser(@RequestHeader UUID userId, @PathVariable UUID blockedId) {
        try {
            managementService.blockUser(userId, blockedId);
            return ResponseEntity.ok("User blocked successfully");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error blocking user: " + e.getMessage());
        }
    }

    @PutMapping("/unblock/{blockedId}")
    public ResponseEntity<String> unblockUser(@RequestHeader UUID userId, @PathVariable UUID blockedId) {
        try {
            managementService.unBlockUser(userId, blockedId);
            return ResponseEntity.ok("User unblocked successfully");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error unblocking user: " + e.getMessage());
        }
    }


}