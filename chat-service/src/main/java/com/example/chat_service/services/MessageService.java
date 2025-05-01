package com.example.chat_service.services;


import com.example.chat_service.models.Message;

import java.time.LocalDateTime;
import java.util.List;

public interface MessageService {
    void sendMessage(String senderId, String receiverId, String content);

    void deleteMessage(String messageId);


    void markAsFavorite(String messageId);

    void unmarkAsFavorite(String messageId);

    List<Message> getMessages(String chatId);


    List<Message> getFavoriteMessages(String senderId);

    List<Message> filterByDate(String userId, LocalDateTime startDate, LocalDateTime endDate);

}
