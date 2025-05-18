package com.example.chat_service.controllers;

import com.example.chat_service.dto.MessageRequestDTO;
import com.example.chat_service.exceptions.UnauthorizedOperationException;
import com.example.chat_service.models.Message;
import com.example.chat_service.services.chat.MessageServiceProxy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/messages")
public class MessageController {
    private static final Logger logger = LoggerFactory.getLogger(MessageController.class);

    @Autowired
    private MessageServiceProxy messageService;

    @PostMapping("/send")
    public ResponseEntity<?> sendMessage(@RequestBody MessageRequestDTO dto, @RequestHeader("userId") String userId, @RequestHeader("userName") String userName) {
        logger.info("Received message send request from user {} to user {}", dto.getSenderId(), dto.getReceiverId());
        
        if (!dto.getSenderId().equals(userId)) {
            logger.warn("Unauthorized attempt: Sender ID {} does not match authenticated user ID {}", dto.getSenderId(), userId);
            throw new UnauthorizedOperationException("Sender ID does not match the authenticated user ID.");
        }
        
        Message message = messageService.sendMessage(dto, userName);
        logger.info("Message sent successfully from {} to {}", dto.getSenderId(), dto.getReceiverId());
        return new ResponseEntity<>(message, HttpStatus.CREATED);
    }

    @PostMapping("/edit/{messageId}")
    public ResponseEntity<?> editMessage(@PathVariable String messageId, @RequestBody String newContent, @RequestHeader("userId") String userId) {
        messageService.editMessage(messageId, userId, newContent);
        logger.info("Message with ID {} edited successfully by user {}", messageId, userId);
        return new ResponseEntity<>("Message edited successfully", HttpStatus.OK);
    }

    @DeleteMapping("/{messageId}")
    public ResponseEntity<?> deleteMessage(@PathVariable String messageId, @RequestHeader("userId") String userId) {
        messageService.deleteMessage(messageId, userId);
        logger.info("Message with ID {} deleted successfully by user {}", messageId, userId);
        return new ResponseEntity<>("Message deleted successfully", HttpStatus.OK);
    }

    @PostMapping("/{messageId}/favorite")
    public ResponseEntity<?> markAsFavorite(@PathVariable String messageId, @RequestHeader("userId") String userId) {
        messageService.markAsFavorite(messageId, userId);
        logger.info("Message with ID {} marked as favorite by user {}", messageId, userId);
        return new ResponseEntity<>("Message marked as favorite successfully", HttpStatus.OK);
    }

    @DeleteMapping("/{messageId}/favorite")
    public ResponseEntity<?> unmarkAsFavorite(@PathVariable String messageId, @RequestHeader("userId") String userId) {
        messageService.unmarkAsFavorite(messageId, userId);
        logger.info("Message with ID {} unmarked as favorite by user {}", messageId, userId);
        return new ResponseEntity<>("Message unmarked as favorite successfully", HttpStatus.OK);
    }

    @ResponseBody
    @GetMapping("/by-chat/{chatId}")
    public List<Message> getMessagesByChat(@PathVariable String chatId, @RequestHeader("userId") String userId) {
        List<Message> messages = messageService.getMessages(chatId, userId);
        logger.info("Getting messages for chat {} for user {}", chatId, userId);
        return messages;
    }

    @ResponseBody
    @GetMapping("/favorites")
    public List<Message> getFavoriteMessages(@RequestHeader("userId") String userId) {
        List<Message> favoriteMessages = messageService.getFavoriteMessages(userId);
        logger.info("Getting favorite messages for user {}", userId);
        return favoriteMessages;
    }

    @GetMapping("/filter")
    public List<Message> filterByDate(
            @RequestHeader("userId") String userId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) String startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) String endDate) {

        LocalDateTime start = LocalDateTime.parse(startDate);
        LocalDateTime end = LocalDateTime.parse(endDate);
        List<Message> messages = messageService.filterByDate(userId, start, end);
        logger.info("Filtering messages for user {} between {} and {}", userId, start, end);
        return messages;
    }

    @ResponseBody
    @GetMapping("/search")
    public List<Message> searchMessages(
            @RequestHeader("userId") String userId,
            @RequestParam String keyword) {
        List<Message> messages = messageService.searchMessages(userId, keyword);
        logger.info("Searching messages for user {} with keyword {}", userId, keyword);
        return messages;
    }
}
