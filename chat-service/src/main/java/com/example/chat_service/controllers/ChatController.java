package com.example.chat_service.controllers;

import com.example.chat_service.dto.CreateChatRequestDTO;
import com.example.chat_service.models.Chat;
import com.example.chat_service.models.Message;
import com.example.chat_service.services.chat.ChatService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/chats")
public class ChatController {

    private static final Logger logger = LoggerFactory.getLogger(ChatController.class);

    private final ChatService chatService;

    @Autowired
    public ChatController(ChatService chatService) {
        this.chatService = chatService;
    }

    /**
     * Creates a new chat between two users or returns the existing one.
     * Expects a JSON body like: {"userId1": "someId", "userId2": "anotherId"}
     *
     * @param request DTO containing the IDs of the two participants.
     * @return ResponseEntity containing the created or fetched Chat and HTTP status.
     */
    @PostMapping
    @ResponseBody
    public ResponseEntity<Chat> createOrGetChat(@RequestBody CreateChatRequestDTO request) {
        Chat chat = chatService.createOrGetChat(request.getUserId1(), request.getUserId2());
        logger.info("Chat created or fetched successfully between {} and {}", request.getUserId1(), request.getUserId2());
        return ResponseEntity.ok(chat);
    }

    /**
     * Retrieves all chats for a specific user.
     *
     * @param userId The ID of the user whose chats are to be retrieved.
     * @return ResponseEntity containing a list of Chats and HTTP status.
     */
    @GetMapping("/user") // Changed path to be relative to /chats
    @ResponseBody
    public ResponseEntity<List<Chat>> getChatsForUser(@RequestHeader("userId") String userId) {
        List<Chat> chats = chatService.getChatsForUser(userId);
        logger.info("Retrieved {} chats for user {}", chats.size(), userId);
        return ResponseEntity.ok(chats);
    }


    @GetMapping("/{chatId}/messages/{lastMessageId}")
    @ResponseBody
    public ResponseEntity<List<Message>> getLatestMessages(@PathVariable String chatId, @PathVariable String lastMessageId) {
        List<Message> messages = chatService.getLatestMessages(chatId, lastMessageId);
        logger.info("Retrieved {} latest messages for chat {} after message {}", messages.size(), chatId, lastMessageId);
        return ResponseEntity.ok(messages);
    }
}
