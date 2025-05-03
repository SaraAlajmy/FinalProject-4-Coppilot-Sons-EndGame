package com.example.chat_service.controllers;

import com.example.chat_service.dto.MessageRequestDTO;
import com.example.chat_service.models.Message;
import com.example.chat_service.services.MessageService;
import com.example.chat_service.services.MessageServiceProxy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/messages") // Base route
public class MessageController {

    @Autowired
    private MessageServiceProxy messageService;

    @PostMapping("/send")
    public void sendMessage(@RequestBody MessageRequestDTO dto, @RequestHeader("userId") String userId) {
        if (!dto.getSenderId().equals(userId)) {
            throw new IllegalArgumentException("Sender ID does not match the authenticated user ID.");
        }
        messageService.sendMessage(dto.getSenderId(), dto.getReceiverId(), dto.getContent());
    }

    @DeleteMapping("/{messageId}")
    public void deleteMessage(@PathVariable String messageId) {
        messageService.deleteMessage(messageId);
    }

    @PostMapping("/{messageId}/favorite")
    public void markAsFavorite(@PathVariable String messageId) {
        messageService.markAsFavorite(messageId);
    }

    @DeleteMapping("/{messageId}/favorite")
    public void unmarkAsFavorite(@PathVariable String messageId) {
        messageService.unmarkAsFavorite(messageId);
    }

    @GetMapping("/chat/{chatId}")
    public List<Message> getMessages(@PathVariable String chatId) {
        return messageService.getMessages(chatId);
    }

    @GetMapping("/favorites/{senderId}")
    public List<Message> getFavoriteMessages(@PathVariable String senderId) {
        return messageService.getFavoriteMessages(senderId);
    }

    @GetMapping("/filter")
    public List<Message> filterByDate(
            @RequestHeader("userId") String userId,
            @RequestParam String startDate,
            @RequestParam String endDate) {


        LocalDateTime start = LocalDateTime.parse(startDate);
        LocalDateTime end = LocalDateTime.parse(endDate);
        return messageService.filterByDate(userId, start, end);
    }
}
