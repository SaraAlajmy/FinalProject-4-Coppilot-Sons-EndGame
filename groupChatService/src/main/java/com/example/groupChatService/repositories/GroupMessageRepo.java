package com.example.groupChatService.repositories;

import com.example.groupChatService.models.GroupMessage;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface GroupMessageRepo extends MongoRepository<GroupMessage, String> {
    // Custom query methods can be defined here if needed
    // For example, to find messages by group ID:
    List<GroupMessage> findByGroupId(String groupId);
    List<GroupMessage> findByGroupIdAndArchived(String groupId, boolean archived);
    List<GroupMessage> findByGroupIdAndSenderId(String groupId, String senderId);
}
