package com.example.chat_service.services.chat;

import com.example.chat_service.clients.UserClient;
import com.example.chat_service.dto.MessageRequestDTO;
import com.example.chat_service.exceptions.UnauthorizedOperationException;
import com.example.chat_service.models.Message;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

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
    public void sendMessage(MessageRequestDTO dto ,String senderUserName) {

        if(userClient.isBlocked(UUID.fromString(dto.getReceiverId()), UUID.fromString(dto.getSenderId()))) {
            throw new RuntimeException("Sender is blocked by the receiver");
        }

        realMessageService.sendMessage(dto, senderUserName);
    }

    @Override
    public void editMessage(String messageId, String userId, String newContent) {
        if (!realMessageService.isMessageOwner(messageId, userId)) {
            throw new UnauthorizedOperationException("User not authorized to edit this message");
        }
        realMessageService.editMessage(messageId, userId, newContent);
    }

    @Override
    public void deleteMessage(String messageId, String userId) {

        if (!realMessageService.isMessageOwner(messageId, userId)) {
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
