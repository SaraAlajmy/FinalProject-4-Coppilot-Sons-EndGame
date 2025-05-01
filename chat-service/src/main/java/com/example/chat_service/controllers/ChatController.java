package com.example.chat_service.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/chat")
public class ChatController {


    @GetMapping("/")
    public String index(@RequestParam(value = "userId") String userId) {
        return "chat/index";
    }

}
