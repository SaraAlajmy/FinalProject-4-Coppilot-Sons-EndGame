package com.example.chat_service.services;

import com.example.chat_service.dto.MessageRequestDTO;
import com.example.chat_service.models.Message;
import com.example.chat_service.repositories.MessageRepository;
import org.springframework.amqp.rabbit.listener.MessageAckListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class RealMessageService implements MessageService {
    MessageRepository messageRepository;
    NotificationObserver notificationObserver;


    @Autowired
    public RealMessageService(MessageRepository messageRepository) {
        this.messageRepository = messageRepository;
    }

    @Override
    public void sendMessage(MessageRequestDTO dto, String senderUserName) {
        //TODO: use chat service to get chatId
        String chatId = dto.getChatId();
        Message message = new Message(
                chatId,
                dto.getSenderId(),
                senderUserName,
                dto.getReceiverId(),
                dto.getContent()
        );

        messageRepository.save(message);
        notificationObserver.createNotification(message);
    }

    @Override
    public void deleteMessage(String messageId) {
        Message message = messageRepository.findById(messageId).orElseThrow(() -> new RuntimeException("Message not found"));
        message.setDeleted(true);
        messageRepository.save(message);
    }


    @Override
    public void markAsFavorite(String messageId) {
        Message message = messageRepository.findById(messageId).orElseThrow(() -> new RuntimeException("Message not found"));
        message.setFavorite(true);
        messageRepository.save(message);
    }


    public void unmarkAsFavorite(String messageId) {
        Message message = messageRepository.findById(messageId).orElseThrow(() -> new RuntimeException("Message not found"));
        message.setFavorite(false);
        messageRepository.save(message);
    }

    @Override
    public List<Message> getMessages(String chatId) {
        return messageRepository.findByChatIdAndIsDeletedFalse(chatId);
    }

    @Override
    public List<Message> getFavoriteMessages(String senderId) {
        return messageRepository.findBySenderIdAndIsFavoriteTrueAndIsDeletedFalse(senderId);
    }

    @Override
    public List<Message> filterByDate(String chatId, LocalDateTime startDate, LocalDateTime endDate) {
        return messageRepository.findByReceiverIdAndIsDeletedFalseAndCreatedAtBetween(chatId, startDate, endDate);
    }

    @Override
    public List<Message> searchMessages(String userId, String keyword) {
        return messageRepository.searchMessages(userId, keyword);
    }

}
