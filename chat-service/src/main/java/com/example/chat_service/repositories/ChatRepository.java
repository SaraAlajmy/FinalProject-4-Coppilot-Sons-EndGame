package com.example.chat_service.repositories;

import com.example.chat_service.models.Chat;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ChatRepository extends MongoRepository<Chat, String> {

    // find chat by participantOneId and participantTwoId
    @Query(value = "{ 'participantOneId': ?0, 'participantTwoId': ?1 } or { 'participantOneId': ?1, 'participantTwoId': ?0 }")
    Optional<Chat> findByParticipantOneIdAndParticipantTwoId(String participantOneId, String participantTwoId);

    // get all chats for a user
    List<Chat> findByParticipantOneIdOrParticipantTwoId(String participantOneId, String participantTwoId);

}
