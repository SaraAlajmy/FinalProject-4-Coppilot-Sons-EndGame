package com.example.chat_service.services;

import com.example.chat_service.dto.MessageRequestDTO;
import com.example.chat_service.models.Message;
import com.example.chat_service.repositories.MessageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class RealMessageService implements MessageService, MessageSubject {
    MessageRepository messageRepository;
    List<Observer> observers;


    @Autowired
    public RealMessageService(MessageRepository messageRepository) {
        this.messageRepository = messageRepository;
        observers = new ArrayList<>();
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
        notifyObservers(message);
    }

    @Override
    public void deleteMessage(String messageId, String userId) {
        Message message = messageRepository.findById(messageId).orElseThrow(() -> new RuntimeException("Message not found"));
        message.setDeleted(true);
        messageRepository.save(message);
    }


    @Override
    public void markAsFavorite(String messageId, String userId) {

        Message message = messageRepository.findById(messageId).orElseThrow(() -> new RuntimeException("Message not found"));
        message.setFavorite(true);
        messageRepository.save(message);
    }

    @Override
    public void unmarkAsFavorite(String messageId, String userId) {
        Message message = messageRepository.findById(messageId).orElseThrow(() -> new RuntimeException("Message not found"));
        message.setFavorite(false);
        messageRepository.save(message);
    }

    @Override
    public List<Message> getMessages(String chatId, String userId) {
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

    public boolean isChatParticipant(String chatId, String userId) {
        //TODO: use chat service to get chat
        return true;
    }

    @Override
    public List<Message> searchMessages(String userId, String keyword) {
        return messageRepository.searchMessages(userId, keyword);
    }

    @Override
    public void addObserver(Observer observer) {
        observers.add(observer);
    }

    @Override
    public void removeObserver(Observer observer) {
        observers.remove(observer);
    }

    @Override
    public void notifyObservers(Message message) {
        for (Observer observer : observers) {
            observer.update(message);
        }
    }
}
