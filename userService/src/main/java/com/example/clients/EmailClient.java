package com.example.clients;

import com.example.models.EmailRequest;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;


@FeignClient(name = "notification-service", url = "${notification.service.url}") // or use service discovery
    public interface EmailClient {

        @PostMapping("/api/email/send")
        void sendEmail(@RequestBody EmailRequest emailRequest);
    }

