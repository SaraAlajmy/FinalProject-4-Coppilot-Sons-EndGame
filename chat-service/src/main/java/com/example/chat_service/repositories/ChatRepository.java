package com.example.chat_service.repositories;

import com.example.chat_service.models.Chat;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ChatRepository extends MongoRepository<Chat, String> {

    // Don't think we'll need any more queries for now

    // Gets chat between two users
    Optional<Chat> findByParticipantOneIdAndParticipantTwoId(String participantOneId, String participantTwoId);

    // get all chats for a user
    List<Chat> findByParticipantOneIdOrParticipantTwoId(String participantOneId, String participantTwoId);

    // This is the same as the above method, but using a custom query
//    @Query("{$or: [{'participantOneId': ?0}, {'participantTwoId': ?0}]}") // ?0 matches the first parameter (userId)
//    List<Chat> findByUserId(String userId);
}
