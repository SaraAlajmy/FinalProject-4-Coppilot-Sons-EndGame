package com.example.chat_service.services;

import com.example.chat_service.clients.UserClient;
import com.example.chat_service.exceptions.UnauthorizedOperationException;
import com.example.chat_service.models.Message;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

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
    public void sendMessage(String senderId, String receiverId, String content) {

        if(userClient.isBlocked(senderId, receiverId)) {
            throw new RuntimeException("Sender is blocked by the receiver");
        }

        realMessageService.sendMessage(senderId, receiverId, content);
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




}
