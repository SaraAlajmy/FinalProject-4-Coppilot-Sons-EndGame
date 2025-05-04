package com.example.chat_service.services;

import com.example.chat_service.clients.UserClient;
import com.example.chat_service.dto.MessageRequestDTO;
import com.example.chat_service.models.Message;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class MessageServiceProxy implements MessageService {

    private final MessageService realMessageService;
    private final UserClient userClient;

    @Autowired
    public MessageServiceProxy(MessageService realMessageService, UserClient userClient) {
        this.realMessageService = realMessageService;
        this.userClient = userClient;
    }

    @Override
    public void sendMessage(MessageRequestDTO dto ,String senderUserName) {

        if(userClient.isBlocked(dto.getSenderId(), dto.getReceiverId())) {
            throw new RuntimeException("Sender is blocked by the receiver");
        }

        realMessageService.sendMessage(dto, senderUserName);
    }

    @Override
    public void deleteMessage(String messageId) {

        realMessageService.deleteMessage(messageId);

    }


    @Override
    public void markAsFavorite(String messageId) {
        realMessageService.markAsFavorite(messageId);
    }

    @Override
    public void unmarkAsFavorite(String messageId) {
        realMessageService.unmarkAsFavorite(messageId);
    }

    @Override
    public List<Message> getMessages(String chatId) {
        return realMessageService.getMessages(chatId);
    }

    @Override
    public List<Message> filterByDate(String userId, LocalDateTime startDate, LocalDateTime endDate) {
        return realMessageService.filterByDate(userId, startDate, endDate);
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
