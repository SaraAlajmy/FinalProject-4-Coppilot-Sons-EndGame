package com.example.chat_service.services.chat;

import com.example.chat_service.clients.UserClient;
import com.example.chat_service.dto.MessageRequestDTO;
import com.example.chat_service.exceptions.UnauthorizedOperationException;
import com.example.chat_service.exceptions.UserBlockedException;
import com.example.chat_service.models.Message;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
public class MessageServiceProxy implements MessageService {

    private final RealMessageService realMessageService;
    private final UserClient userClient;

    private static final Logger logger = LoggerFactory.getLogger(MessageServiceProxy.class);


    @Autowired
    public MessageServiceProxy(RealMessageService realMessageService, UserClient userClient) {
        this.realMessageService = realMessageService;
        this.userClient = userClient;
    }

    @Override
    public Message sendMessage(MessageRequestDTO dto, String senderId, String senderUserName) {
        try {
            boolean areBlocking = Boolean.TRUE.equals(userClient.areBlocking(UUID.fromString(dto.getReceiverId()), UUID.fromString(senderId)).getBody());
            if (areBlocking) {
                throw new UserBlockedException("Messaging is restricted since either you or the user has blocked the other.");
            }
            Message message = realMessageService.sendMessage(dto, senderId, senderUserName);
            logger.info("Message sent successfully from {} to {}", senderId, dto.getReceiverId());
            return message;
        } catch (Exception e) {
            if (e instanceof UserBlockedException) {
                logger.error("Sender {} is blocked by receiver {}", senderId, dto.getReceiverId());
                throw new UserBlockedException("Messaging is restricted since either you or the user has blocked the other.", e);
            }
            logger.error("unexpected error while sending message from {} to {}: {}", senderId, dto.getReceiverId(), e.getMessage());
            throw new RuntimeException("Unexpected error while sending message", e);
        }

    }

    @Override
    public void editMessage(String messageId, String userId, String newContent) {
        if (!realMessageService.isMessageSender(messageId, userId)) {
            logger.info("User {} is not authorized to edit message {}", userId, messageId);
            throw new UnauthorizedOperationException("User not authorized to edit this message");
        }
        realMessageService.editMessage(messageId, userId, newContent);
    }

    @Override
    public void deleteMessage(String messageId, String userId) {
        if (!realMessageService.isMessageSender(messageId, userId)) {
            logger.info("User {} is not authorized to delete message {}", userId, messageId);
            throw new UnauthorizedOperationException("User not authorized to delete this message");
        }
        realMessageService.deleteMessage(messageId, userId);
        logger.info("Message {} deleted successfully by user {}", messageId, userId);
    }


    @Override
    public void markAsFavorite(String messageId, String userId) {
        if (!realMessageService.isMessageOwner(messageId, userId)) {
            logger.info("User {} is not authorized to mark message {} as favorite", userId, messageId);
            throw new UnauthorizedOperationException("User not authorized to mark this message as favorite");
        }
        realMessageService.markAsFavorite(messageId, userId);
        logger.info("Message {} marked as favorite by user {}", messageId, userId);
    }

    @Override
    public void unmarkAsFavorite(String messageId, String userId) {
        if (!realMessageService.isMessageOwner(messageId, userId)) {
            logger.info("User {} is not authorized to unmark message {} as favorite", userId, messageId);
            throw new UnauthorizedOperationException("User not authorized to unmark this message as favorite");
        }
        realMessageService.unmarkAsFavorite(messageId, userId);
        logger.info("Message {} unmarked as favorite by user {}", messageId, userId);
    }

    @Override
    public List<Message> getMessages(String chatId, String userId) {
        if (!realMessageService.isChatParticipant(chatId, userId)) {
            logger.info("User {} is not authorized to access chat {}", userId, chatId);
            throw new UnauthorizedOperationException("User not authorized to access this chat");
        }
        logger.info("Getting messages for chat {} for user {}", chatId, userId);
        return realMessageService.getMessages(chatId, userId);
    }

    @Override
    public List<Message> filterByDate(String receiverId, LocalDateTime startDate, LocalDateTime endDate) {
        logger.info("Filtering messages for user {} between {} and {}", receiverId, startDate, endDate);
        return realMessageService.filterByDate(receiverId, startDate, endDate);
    }


    @Override
    public List<Message> getFavoriteMessages(String userId) {
        logger.info("Getting favorite messages for user {}", userId);
        return realMessageService.getFavoriteMessages(userId);
    }


    @Override
    public List<Message> searchMessages(String userId, String keyword) {
        logger.info("Searching messages for user {} with keyword {}", userId, keyword);
        return realMessageService.searchMessages(userId, keyword);
    }


}
