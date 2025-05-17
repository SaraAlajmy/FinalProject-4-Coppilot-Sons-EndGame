package com.example.chat_service.services.chat;

import com.example.chat_service.clients.UserClient;
import com.example.chat_service.dto.MessageRequestDTO;
import com.example.chat_service.exceptions.UnauthorizedOperationException;
import com.example.chat_service.exceptions.UserBlockedException;
import com.example.chat_service.models.Message;
import lombok.extern.slf4j.Slf4j;
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

    @Autowired
    public MessageServiceProxy(RealMessageService realMessageService, UserClient userClient) {
        this.realMessageService = realMessageService;
        this.userClient = userClient;
    }

    @Override
    public Message sendMessage(MessageRequestDTO dto, String senderUserName) {
        try {
            boolean areBlocking=Boolean.TRUE.equals(userClient.areBlocking(UUID.fromString(dto.getReceiverId()), UUID.fromString(dto.getSenderId())).getBody());
            if(areBlocking) {
                logger.info("User: " + senderUserName + " and User " + dto.getReceiverId() +" are blocking each other");
                throw new UserBlockedException("Sender is blocked by the receiver");
            }
            log.info("User: " + senderUserName + " and User " + dto.getReceiverId() +" are not blocking each other");
            Message message = realMessageService.sendMessage(dto, senderUserName);
            return message;
        } catch (Exception e) {
            if (e instanceof UserBlockedException) {
                throw e;
            }
            throw new UserBlockedException("Error checking user block status", e);
        }
        
    }

    @Override
    public void editMessage(String messageId, String userId, String newContent) {
        if (!realMessageService.isMessageSender(messageId, userId)) {
            throw new UnauthorizedOperationException("User not authorized to edit this message");
        }
        realMessageService.editMessage(messageId, userId, newContent);
    }

    @Override
    public void deleteMessage(String messageId, String userId) {

        if (!realMessageService.isMessageSender(messageId, userId)) {
            throw new UnauthorizedOperationException("User not authorized to delete this message");
        }
        realMessageService.deleteMessage(messageId, userId);

    }


    @Override
    public void markAsFavorite(String messageId, String userId) {
        if (!realMessageService.isMessageOwner(messageId, userId)) {
            throw new UnauthorizedOperationException("User not authorized to mark this message as favorite");
        }
        realMessageService.markAsFavorite(messageId, userId);
    }

    @Override
    public void unmarkAsFavorite(String messageId, String userId) {
        if (!realMessageService.isMessageOwner(messageId, userId)) {
            throw new UnauthorizedOperationException("User not authorized to unmark this message as favorite");
        }
        realMessageService.unmarkAsFavorite(messageId, userId);
    }

    @Override
    public List<Message> getMessages(String chatId, String userId) {
        if (!realMessageService.isChatParticipant(chatId, userId)) {
            throw new UnauthorizedOperationException("User not authorized to access this chat");
        }
        return realMessageService.getMessages(chatId, userId);
    }

    @Override
    public List<Message> filterByDate(String receiverId, LocalDateTime startDate, LocalDateTime endDate) {
        return realMessageService.filterByDate(receiverId, startDate, endDate);
    }



    @Override
    public List<Message> getFavoriteMessages(String userId) {
        return realMessageService.getFavoriteMessages(userId);
    }


    @Override
    public List<Message> searchMessages(String userId, String keyword) {
        return realMessageService.searchMessages(userId, keyword);
    }


}
