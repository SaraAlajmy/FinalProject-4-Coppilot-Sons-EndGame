package com.example.chat_service.services.chat;

import com.example.chat_service.dto.MessageRequestDTO;
import com.example.chat_service.models.Message;
import com.example.chat_service.models.Chat;
import com.example.chat_service.models.FavouriteMessage;
import com.example.chat_service.repositories.MessageRepository;
import com.example.chat_service.repositories.FavouriteMessageRepository;
import com.example.chat_service.services.observer.MessageSubject;
import com.example.chat_service.services.observer.Observer;
import com.example.chat_service.exceptions.FavouriteMessageException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class RealMessageService implements MessageService, MessageSubject {
    private final MessageRepository messageRepository;
    private final FavouriteMessageRepository favouriteMessageRepository;
    private final ChatService chatService;
    private final List<Observer> observers;

    @Autowired
    public RealMessageService(MessageRepository messageRepository, FavouriteMessageRepository favouriteMessageRepository, ChatService chatService) {
        this.messageRepository = messageRepository;
        this.favouriteMessageRepository = favouriteMessageRepository;
        this.chatService = chatService;
        this.observers = new ArrayList<>();
    }

    @Override
    public Message sendMessage(MessageRequestDTO dto, String senderUserName) {
        String chatId = chatService.createOrGetChat(dto.getSenderId(), dto.getReceiverId()).getChatId();
        Message message = new Message(
                chatId,
                dto.getSenderId(),
                senderUserName,
                dto.getReceiverId(),
                dto.getContent()
        );

        messageRepository.save(message);
        notifyObservers(message);
        return message;
        
    }


    @Override
    public void editMessage(String messageId, String userId, String newContent) {
        Message message = messageRepository.findById(messageId).orElseThrow(() -> new RuntimeException("Message not found"));
        message.setContent(newContent);
        messageRepository.save(message);
    }

    @Override
    public void deleteMessage(String messageId, String userId) {
        Message message = messageRepository.findById(messageId).orElseThrow(() -> new RuntimeException("Message not found"));
        message.setDeleted(true);
        messageRepository.save(message);
    }

    @Override
    public void markAsFavorite(String messageId, String userId) {
        Message message = messageRepository.findById(messageId)
                .orElseThrow(() -> new FavouriteMessageException("Message not found"));

        if (favouriteMessageRepository.existsByMessageIdAndUserId(messageId, userId)) {
            throw new FavouriteMessageException("Message is already favorited by this user");
        }

        try {
            FavouriteMessage favouriteMessage = new FavouriteMessage();
            favouriteMessage.setMessage(message);
            favouriteMessage.setUserId(userId);
            favouriteMessageRepository.save(favouriteMessage);
        } catch (Exception e) {
            throw new FavouriteMessageException("Failed to mark message as favorite", e);
        }
    }

    @Override
    public void unmarkAsFavorite(String messageId, String userId) {
        if (!favouriteMessageRepository.existsByMessageIdAndUserId(messageId, userId)) {
            throw new FavouriteMessageException("Message is not favorited by this user");
        }

        try {
            favouriteMessageRepository.deleteByMessageIdAndUserId(messageId, userId);
        } catch (Exception e) {
            throw new FavouriteMessageException("Failed to unmark message as favorite", e);
        }
    }

    @Override
    public List<Message> getMessages(String chatId, String userId) {
        return messageRepository.findByChatIdAndIsDeletedFalse(chatId);
    }

    @Override
    public List<Message> getFavoriteMessages(String userId) {
        try {
            return favouriteMessageRepository.findByUserId(userId)
                    .stream()
                    .map(FavouriteMessage::getMessage)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            throw new FavouriteMessageException("Failed to retrieve favorite messages", e);
        }
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
        Chat chat = chatService.getChatById(chatId);
        return chat.getParticipantOneId().equals(userId) || chat.getParticipantTwoId().equals(userId);
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
