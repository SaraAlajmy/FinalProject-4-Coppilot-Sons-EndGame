package com.example.chat_service.controllers;

import com.example.chat_service.dto.MessageRequestDTO;
import com.example.chat_service.models.Message;
import com.example.chat_service.services.chat.MessageServiceProxy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/messages") // Base route
public class MessageController {

    @Autowired
    private MessageServiceProxy messageService;

    @PostMapping("/send")
    public void sendMessage(@RequestBody MessageRequestDTO dto, @RequestHeader("userId") String userId, @RequestHeader("userName") String userName) {
        if (!dto.getSenderId().equals(userId)) {
            throw new IllegalArgumentException("Sender ID does not match the authenticated user ID.");
        }
        messageService.sendMessage(dto, userName);
    }

    @DeleteMapping("/{messageId}")
        public void deleteMessage(@PathVariable String messageId, @RequestHeader("userId") String userId) {
            messageService.deleteMessage(messageId, userId);
    }

    @PostMapping("/{messageId}/favorite")
    public void markAsFavorite(@PathVariable String messageId, @RequestHeader("userId") String userId) {
        messageService.markAsFavorite(messageId, userId);
    }

    @DeleteMapping("/{messageId}/favorite")
    public void unmarkAsFavorite(@PathVariable String messageId, @RequestHeader("userId") String userId) {
        messageService.unmarkAsFavorite(messageId, userId);
    }

    @ResponseBody
    @GetMapping("/chat/{chatId}")
    public List<Message> getMessages(@PathVariable String chatId, @RequestHeader("userId") String userId) {
        return messageService.getMessages(chatId, userId);
    }

    @ResponseBody
    @GetMapping("/favorites")
    public List<Message> getFavoriteMessages(@RequestHeader("userId") String userId) {
        return messageService.getFavoriteMessages(userId);
    }

    @GetMapping("/filter")
    public List<Message> filterByDate(
            @RequestHeader("userId") String userId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) String startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) String endDate) {

        LocalDateTime start = LocalDateTime.parse(startDate);
        LocalDateTime end = LocalDateTime.parse(endDate);
        return messageService.filterByDate(userId, start, end);
    }

    @ResponseBody
    @GetMapping("/search")
    public List<Message> searchMessages(
            @RequestHeader("userId") String userId,
            @RequestParam String keyword) {
        return messageService.searchMessages(userId, keyword);
    }
}
