package com.example.chat_service.services;

import com.example.chat_service.models.Message;
import com.example.chat_service.repositories.MessageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class RealMessageService implements MessageService {
    MessageRepository messageRepository;

    @Autowired
    public RealMessageService(MessageRepository messageRepository) {
        this.messageRepository = messageRepository;
    }

    @Override
    public void sendMessage(String senderId, String receiverId, String content) {
        //TODO: use chat service to get chatId
        String chatId = senderId + "_" + receiverId;
        messageRepository.save(new Message(chatId,senderId, receiverId, content));
    }

    @Override
    public void deleteMessage(String messageId, String userId) {
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

    @Override
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
    public List<Message> getFavoriteMessages(String userId) {
        return messageRepository.findBySenderIdOrReceiverIdAndIsFavoriteTrueAndIsDeletedFalse(userId);
    }

    @Override
    public List<Message> filterByDate(String receiverId, LocalDateTime startDate, LocalDateTime endDate) {
        return messageRepository.findByReceiverIdAndIsDeletedFalseAndCreatedAtBetween(receiverId, startDate, endDate);
    }

    public boolean isMessageOwner(String messageId, String userId) {
        Message message = messageRepository.findById(messageId).orElseThrow(() -> new RuntimeException("Message not found"));
        return message.getSenderId().equals(userId) || message.getReceiverId().equals(userId);
    }

}
