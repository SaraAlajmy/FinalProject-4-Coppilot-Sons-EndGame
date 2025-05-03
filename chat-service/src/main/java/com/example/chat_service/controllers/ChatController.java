package com.example.chat_service.controllers;

import com.example.chat_service.dto.CreateChatRequestDTO;
import com.example.chat_service.models.Chat;
import com.example.chat_service.services.ChatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/chats") // Base path for chat-related endpoints
public class ChatController {

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
    public ResponseEntity<Chat> createOrGetChat(@RequestBody CreateChatRequestDTO request) {
        try {
            Chat chat = chatService.createOrGetChat(request.getUserId1(), request.getUserId2());
            // TODO: Do we care about returning different status codes for created vs. fetched?
            return ResponseEntity.ok(chat);
        } catch (RuntimeException e) {
            // Handle the block exception from ChatService
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(null); // Or return an error object
        }
    }

    /**
     * Retrieves all chats for a specific user.
     *
     * @param userId The ID of the user whose chats are to be retrieved.
     * @return ResponseEntity containing a list of Chats and HTTP status.
     */
    @GetMapping("/user/{userId}") // Changed path to be relative to /chats
    public ResponseEntity<List<Chat>> getChatsForUser(@PathVariable String userId) {
        List<Chat> chats = chatService.getChatsForUser(userId);
        return ResponseEntity.ok(chats);
    }
}