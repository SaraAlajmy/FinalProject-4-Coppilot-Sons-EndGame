package com.example.clients;

import com.example.models.EmailRequest;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@FeignClient(name = "notification-service", url = "${notification.service.url}") // or use service discovery
    public interface EmailClient {

        @PostMapping("/notifications/reset-password")
        void sendEmail(@RequestParam String resetPasswordToken,
                       @RequestParam String recipientEmail,
                       @RequestParam String recipientName);
    }

