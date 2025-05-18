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
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
@Slf4j
@Service
public class RealMessageService implements MessageService, MessageSubject {
    private final MessageRepository messageRepository;
    private final FavouriteMessageRepository favouriteMessageRepository;
    private final ChatService chatService;
    private final List<Observer> observers;

    private static final Logger logger = LoggerFactory.getLogger(RealMessageService.class);

    @Autowired
    public RealMessageService(MessageRepository messageRepository, FavouriteMessageRepository favouriteMessageRepository, ChatService chatService) {
        this.messageRepository = messageRepository;
        this.favouriteMessageRepository = favouriteMessageRepository;
        this.chatService = chatService;
        this.observers = new ArrayList<>();
    }

    @Override
    public Message sendMessage(MessageRequestDTO dto, String senderId, String senderUserName) {
        String chatId = chatService.createOrGetChat(senderId, dto.getReceiverId()).getChatId();
        Message message = new Message(
                chatId,
                senderId,
                senderUserName,
                dto.getReceiverId(),
                dto.getContent()
        );

        messageRepository.save(message);
        notifyObservers(message);
        logger.info("Message sent from {} to {}: {}", senderId, dto.getReceiverId(), dto.getContent());
        return message;
        
    }


    @Override
    public void editMessage(String messageId, String userId, String newContent) {
        Message message = messageRepository.findById(messageId).orElseThrow(() -> new RuntimeException("Message not found"));
        message.setContent(newContent);
        messageRepository.save(message);
        logger.info("Editing message with ID {} edited by user {}: {}", messageId, userId, newContent);
    }

    @Override
    public void deleteMessage(String messageId, String userId) {
        try {
            messageRepository.deleteById(messageId);
            deleteFavouriteMessage(messageId, userId);
            logger.info("Deleting message with ID {} by user {}", messageId, userId);
        } catch (Exception e) {
            logger.error("Failed to delete message with ID {}: {}", messageId, e.getMessage());
            throw new FavouriteMessageException("Failed to delete message", e);
        }
    }

    public void deleteFavouriteMessage(String messageId, String userId) {
        favouriteMessageRepository.deleteByMessageIdAndUserId(messageId, userId);
        logger.info("Deleting favourite message with ID {} for user {}", messageId, userId);
    }

    @Override
    public void markAsFavorite(String messageId, String userId) {
        Message message = messageRepository.findById(messageId)
                .orElseThrow(() -> new FavouriteMessageException("Message not found"));

        if (favouriteMessageRepository.existsByMessageIdAndUserId(messageId, userId)) {
            logger.info("Message is already favorite by this user");
            throw new FavouriteMessageException("Message is already favorite by this user");
        }

        try {
            FavouriteMessage favouriteMessage = new FavouriteMessage();
            favouriteMessage.setMessage(message);
            favouriteMessage.setUserId(userId);
            favouriteMessageRepository.save(favouriteMessage);
            logger.info("Marking message with ID {} as favorite by user {}", messageId, userId);
        } catch (Exception e) {
            logger.error("Failed to mark message with ID {} as favorite: {}", messageId, e.getMessage());
            throw new FavouriteMessageException("Failed to mark message as favorite", e);
        }
    }

    @Override
    public void unmarkAsFavorite(String messageId, String userId) {
        if (!favouriteMessageRepository.existsByMessageIdAndUserId(messageId, userId)) {
            logger.info("Message is not favorite by this user");
            throw new FavouriteMessageException("Message is not favorite by this user");
        }

        try {
            favouriteMessageRepository.deleteByMessageIdAndUserId(messageId, userId);
            logger.info("Unmarking message with ID {} as favorite by user {}", messageId, userId);
        } catch (Exception e) {
            logger.error("Failed to unmark message with ID {} as favorite: {}", messageId, e.getMessage());
            throw new FavouriteMessageException("Failed to unmark message as favorite", e);
        }
    }

    @Override
    public List<Message> getMessages(String chatId, String userId) {
        logger.info("Getting messages for chat {} for user {}", chatId, userId);
        return messageRepository.findByChatIdAndIsDeletedFalse(chatId);
    }

    @Override
    public List<Message> getFavoriteMessages(String userId) {
        try {
            logger.info("Getting favorite messages for user {}", userId);
            return favouriteMessageRepository.findByUserId(userId)
                    .stream()
                    .map(FavouriteMessage::getMessage)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            logger.error("Failed to retrieve favorite messages for user {}: {}", userId, e.getMessage());
            throw new FavouriteMessageException("Failed to retrieve favorite messages", e);
        }
    }

    @Override
    public List<Message> filterByDate(String receiverId, LocalDateTime startDate, LocalDateTime endDate) {
        logger.info("Filtering messages for user {} between {} and {}", receiverId, startDate, endDate);
        return messageRepository.findByReceiverIdAndIsDeletedFalseAndCreatedAtBetween(receiverId, startDate, endDate);
    }

    public boolean isMessageOwner(String messageId, String userId) {
        Message message = messageRepository.findById(messageId).orElseThrow(() -> new RuntimeException("Message not found"));
        logger.info("Checking if user {} is the owner of message {}", userId, messageId);
        return message.getSenderId().equals(userId) || message.getReceiverId().equals(userId);
    }

    public boolean isChatParticipant(String chatId, String userId) {
        Chat chat = chatService.getChatById(chatId);
        logger.info("Checking if user {} is a participant in chat {}", userId, chatId);
        return chat.getParticipantOneId().equals(userId) || chat.getParticipantTwoId().equals(userId);
    }

    public boolean isMessageSender(String messageId, String userId) {
        Message message = messageRepository.findById(messageId).orElseThrow(() -> new RuntimeException("Message not found"));
        logger.info("Checking if user {} is the sender of message {}", userId, messageId);
        return message.getSenderId().equals(userId);
    }

    @Override
    public List<Message> searchMessages(String userId, String keyword) {
        logger.info("Searching messages for user {} with keyword {}", userId, keyword);
        return messageRepository.searchMessages(userId, keyword);
    }

    @Override
    public void addObserver(Observer observer) {
        logger.info("Adding observer: {}", observer.getClass().getSimpleName());
        observers.add(observer);
    }

    @Override
    public void removeObserver(Observer observer) {
        logger.info("Removing observer: {}", observer.getClass().getSimpleName());
        observers.remove(observer);
    }

    @Override
    public void notifyObservers(Message message) {
        for (Observer observer : observers) {
            logger.info("Notifying observer: {}", observer.getClass().getSimpleName());
            observer.update(message);
        }
    }
}
