package com.example.chat_service.repositories;

import com.example.chat_service.models.FavouriteMessage;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FavouriteMessageRepository extends MongoRepository<FavouriteMessage, String> {
    List<FavouriteMessage> findByUserId(String userId);
    Optional<FavouriteMessage> findByMessageIdAndUserId(String messageId, String userId);
    void deleteByMessageIdAndUserId(String messageId, String userId);
    boolean existsByMessageIdAndUserId(String messageId, String userId);
    void deleteAllByMessageId(String messageId);
}
