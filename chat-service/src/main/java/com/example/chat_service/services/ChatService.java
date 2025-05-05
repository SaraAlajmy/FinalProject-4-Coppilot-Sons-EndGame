package com.example.chat_service.services;

import com.example.chat_service.clients.UserClient;
import com.example.chat_service.models.Chat;
import com.example.chat_service.repositories.ChatRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ChatService {
     private final ChatRepository chatRepository;
    private final UserClient userClient;

    @Autowired
    public ChatService(ChatRepository chatRepository, UserClient userClient) {
        this.chatRepository = chatRepository;
        this.userClient = userClient;
    }

    /**
     * Creates a new chat between two users or retrieves an existing one.
     *
     * @param userId1 The ID of the first user.
     * @param userId2 The ID of the second user.
     * @return The created or retrieved chat.
     */
    public Chat createOrGetChat(String userId1, String userId2) {
        Optional<Chat> chat = chatRepository.findByParticipantOneIdAndParticipantTwoId(userId1, userId2);
        if (chat.isPresent()) return chat.get();

        // Try reverse order
        chat = chatRepository.findByParticipantOneIdAndParticipantTwoId(userId2, userId1);
        if (chat.isPresent()) return chat.get();

        // Create new chat if no block exists
        Chat newChat = new Chat(null, userId1, userId2);
        return chatRepository.save(newChat);
    }

    public List<Chat> getChatsForUser(String userId) {
        return chatRepository.findByParticipantOneIdOrParticipantTwoId(userId, userId);
    }
}
