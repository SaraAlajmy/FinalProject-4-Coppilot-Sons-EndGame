package com.example.chat_service.services.chat;


import com.example.chat_service.dto.MessageRequestDTO;
import com.example.chat_service.models.Message;

import java.time.LocalDateTime;
import java.util.List;

public interface MessageService {
    Message sendMessage(MessageRequestDTO dto, String senderId, String senderUserName);

    void deleteMessage(String messageId, String userId);

    void editMessage(String messageId, String userId, String newContent);


    void markAsFavorite(String messageId, String userId);

    void unmarkAsFavorite(String messageId, String userId);

    List<Message> getMessages(String chatId, String userId);


    List<Message> getFavoriteMessages(String userId);

    List<Message> filterByDate(String receiverId, LocalDateTime startDate, LocalDateTime endDate);

    List<Message> searchMessages(String userId, String keyword);
}
