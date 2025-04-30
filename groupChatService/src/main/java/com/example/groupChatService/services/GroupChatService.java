package com.example.groupChatService.services;


import com.example.groupChatService.models.GroupChat;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.example.groupChatService.repositories.GroupChatRepo;

import java.util.List;

@Service
public class GroupChatService {
    private final GroupChatRepo groupChatRepo;
    @Autowired
    public GroupChatService(GroupChatRepo groupChatRepo) {
        this.groupChatRepo = groupChatRepo;
    }
    public List<GroupChat> getAllGroupChat(){
        return groupChatRepo.findAll();
    }
    public GroupChat getGroupChatById(String id) {
        GroupChat groupChat = groupChatRepo.findById(id).orElse(null);
        if (groupChat == null) {
            throw new RuntimeException("Group chat not found with id: " + id);
        }
        return groupChat;
    }
    public String deleteGroupChat(String id) {
        GroupChat groupChat = groupChatRepo.findById(id).orElse(null);
        if (groupChat == null) {
            throw new RuntimeException("Group chat not found with id: " + id);
        }
        groupChatRepo.deleteById(id);
        return "Group chat deleted successfully with id: " + id;
    }





}
