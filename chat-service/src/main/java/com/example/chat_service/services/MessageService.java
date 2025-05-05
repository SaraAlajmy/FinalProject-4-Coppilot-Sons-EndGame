package com.example.chat_service.services;


import com.example.chat_service.models.Message;

import java.time.LocalDateTime;
import java.util.List;

public interface MessageService {
    void sendMessage(String senderId, String receiverId, String content);

    void deleteMessage(String messageId, String userId);


    void markAsFavorite(String messageId, String userId);

    void unmarkAsFavorite(String messageId, String userId);

    List<Message> getMessages(String chatId, String userId);


    List<Message> getFavoriteMessages(String userId);

    List<Message> filterByDate(String receiverId, LocalDateTime startDate, LocalDateTime endDate);

}
