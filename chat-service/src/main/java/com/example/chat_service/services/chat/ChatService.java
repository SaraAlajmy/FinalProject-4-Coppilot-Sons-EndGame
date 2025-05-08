package com.example.chat_service.services.chat;

import com.example.chat_service.models.Chat;
import com.example.chat_service.models.Message;
import com.example.chat_service.repositories.ChatRepository;
import com.example.chat_service.repositories.MessageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ChatService {
    private final ChatRepository chatRepository;

    private final MessageRepository messageRepository;

    @Autowired
    public ChatService(ChatRepository chatRepository, MessageRepository messageRepository) {
        this.chatRepository = chatRepository;
        this.messageRepository = messageRepository;
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

        // Create new chat if no block exists
        Chat newChat = new Chat(null, userId1, userId2);
        return chatRepository.save(newChat);
    }

    public List<Chat> getChatsForUser(String userId) {
        return chatRepository.findByParticipantOneIdOrParticipantTwoId(userId, userId);
    }

    public List<Message> getLatestMessages(String chatId, String lastMessageId) {
        Optional<Message> lastMessage = messageRepository.findById(lastMessageId);
        if (lastMessage.isEmpty()) {
            throw new RuntimeException("Last message not found");
        }
        return messageRepository.findByChatIdAndCreatedAtAfter(chatId, lastMessage.get().getCreatedAt());
    }
}
