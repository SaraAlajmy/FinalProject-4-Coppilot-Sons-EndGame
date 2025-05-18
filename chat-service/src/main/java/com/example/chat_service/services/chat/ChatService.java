package com.example.chat_service.services.chat;

import com.example.chat_service.controllers.ChatController;
import com.example.chat_service.exceptions.UnauthorizedOperationException;
import com.example.chat_service.models.Chat;
import com.example.chat_service.models.Message;
import com.example.chat_service.repositories.ChatRepository;
import com.example.chat_service.repositories.MessageRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestHeader;

import java.util.List;
import java.util.Optional;

@Service
public class ChatService {
    private final ChatRepository chatRepository;

    private final MessageRepository messageRepository;

    private static final Logger logger = LoggerFactory.getLogger(ChatService.class);

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
        if (chat.isPresent()) {
            logger.info("Chat already exists between {} and {}", userId1, userId2);
            return chat.get();
        }

        // Create new chat if no block exists
        Chat newChat = new Chat(null, userId1, userId2);
        logger.info("Creating new chat between {} and {}", userId1, userId2);
        return chatRepository.save(newChat);
    }

    public List<Chat> getChatsForUser(String userId) {
        logger.info("Retrieving chats for user {}", userId);
        return chatRepository.findByParticipantOneIdOrParticipantTwoId(userId, userId);
    }

    public List<Message> getLatestMessages(String userId, String chatId, String lastMessageId) {
        Optional<Message> lastMessage = messageRepository.findById(lastMessageId);
        if (lastMessage.isEmpty()) {
            logger.error("Last message with ID {} not found in chat {}", lastMessageId, chatId);
            throw new RuntimeException("Last message not found");
        }
        Chat chat = getChatById(chatId);
        if (!chat.getParticipantOneId().equals(userId) && !chat.getParticipantTwoId().equals(userId)) {
            logger.error("User {} is not a participant in chat {}", userId, chatId);
            throw new UnauthorizedOperationException("User is not a participant in this chat");
        }
        logger.info("Retrieving messages for chat {} after message {}", chatId, lastMessageId);
        return messageRepository.findByChatIdAndCreatedAtAfter(chatId, lastMessage.get().getCreatedAt());
    }

    public Chat getChatById(String chatId) {
        logger.info("Retrieving chat with ID {}", chatId);
        return chatRepository.findById(chatId).orElseThrow(() -> new RuntimeException("Chat not found"));
    }
}
