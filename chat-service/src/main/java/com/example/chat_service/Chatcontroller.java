package com.example.chat_service;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class Chatcontroller {
    @GetMapping("/chat")
    public String chat() {
        return "Hello from Chat Service!";
    }
}
