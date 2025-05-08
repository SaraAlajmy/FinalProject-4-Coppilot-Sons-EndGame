package com.example.clients;

import com.example.models.EmailRequest;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;


@FeignClient(name = "notification-service", url = "http://notification-service") // or use service discovery
    public interface EmailClient {

        @PostMapping("/api/email/send")
        void sendEmail(@RequestBody EmailRequest emailRequest);
    }
