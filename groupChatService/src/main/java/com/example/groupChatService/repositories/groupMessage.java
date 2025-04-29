package com.example.groupChatService.repositories;

import org.springframework.data.mongodb.repository.MongoRepository;

public interface groupMessage extends MongoRepository<groupMessage, String> {
    // Custom query methods can be defined here if needed
    // For example, to find messages by group ID:
    // List<GroupMessage> findByGroupId(String groupId);
}
