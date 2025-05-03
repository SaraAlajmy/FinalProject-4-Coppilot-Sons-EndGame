package com.example.chat_service.repositories;

import com.example.chat_service.models.Message;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface MessageRepository extends MongoRepository<Message, String> {

    List<Message> findByChatIdAndIsDeletedFalse(String chatId);

    List<Message> findByReceiverIdAndIsDeletedFalseAndCreatedAtBetween(
            String receiverId, LocalDateTime startDate, LocalDateTime endDate
    );

    List<Message> findBySenderIdOrReceiverIdAndIsFavoriteTrueAndIsDeletedFalse(String userId);


    

}
