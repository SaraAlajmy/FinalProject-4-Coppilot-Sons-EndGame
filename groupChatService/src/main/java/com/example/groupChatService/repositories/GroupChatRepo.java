package com.example.groupChatService.repositories;

import com.example.groupChatService.models.GroupChat;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface GroupChatRepo extends MongoRepository<GroupChat, String> {

    List<GroupChat> findByMembersContaining(String userId);
}
