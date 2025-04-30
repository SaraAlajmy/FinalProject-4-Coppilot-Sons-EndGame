package com.example.groupChatService.repositories;

import com.example.groupChatService.models.GroupChat;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface GroupChatRepo extends MongoRepository<GroupChat, String> {
}
