package com.example.chat_service.repositories;

import com.example.chat_service.models.Message;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface MessageRepository extends MongoRepository<Message, String> {

    List<Message> findByChatIdAndIsDeletedFalse(String chatId);

    List<Message> findByReceiverIdAndIsDeletedFalseAndCreatedAtBetween(
            String receiverId, LocalDateTime startDate, LocalDateTime endDate
    );


    List<Message> findByChatIdAndCreatedAtAfter(String chatId, LocalDateTime createdAt);

    @Query("{ '$and': [ { '$or': [ {'senderId': ?0}, {'receiverId': ?0} ] }, { 'content': { '$regex': ?1, '$options': 'i' } } ] }")
    List<Message> searchMessages(String userId, String keyword);
}
