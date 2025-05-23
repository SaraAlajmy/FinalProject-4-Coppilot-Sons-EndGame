package org.example.notificationservice.controllers;

import lombok.AllArgsConstructor;
import org.example.notificationservice.factories.NotificationDataFactory;
import org.example.notificationservice.models.Notification;
import org.example.notificationservice.services.NotificationDeliveryService;
import org.example.notificationservice.services.NotificationService;
import org.example.shared.dto.NotificationDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@RestController
@RequestMapping("/debug")
@AllArgsConstructor
public class DebugController {
    private NotificationDataFactory notificationDataFactory;
    private NotificationDeliveryService notificationDeliveryService;
    private NotificationService notificationService;

    @PostMapping("/send-sample-notification")
    public ResponseEntity<Void> sendNotification(
        @RequestParam String strategyType
    ) {
        Notification notification = notificationDataFactory.createRandomNotification();

        boolean result = notificationDeliveryService.deliverNotificationUsingStrategy(
            notification,
            strategyType
        );

        if (result) {
            return ResponseEntity.ok().build();
        } else {
            return ResponseEntity.status(500).build();
        }
    }

    @PostMapping("/createSampleNotification")
    public ResponseEntity<Void> createSampleNotification(@RequestBody NotificationDTO notificationDto) {
        if (notificationService.createNotification(notificationDto)) {
            return ResponseEntity.ok().build();
        } else {
            return ResponseEntity.status(500).build();
        }


    }
}
