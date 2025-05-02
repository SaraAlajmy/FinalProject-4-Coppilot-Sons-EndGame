package com.example.groupChatService.services;

import com.example.groupChatService.models.GroupMessage;
import com.example.groupChatService.repositories.GroupMessageRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class GroupMessageService {
    private final GroupMessageRepo groupMessageRepo;
    @Autowired

    public GroupMessageService(GroupMessageRepo groupMessageRepo) {
        this.groupMessageRepo = groupMessageRepo;
    }
    public GroupMessage addGroupMessage(GroupMessage groupMessage) {
        return groupMessageRepo.save(groupMessage);
    }
    public List<GroupMessage> getAllGroupMessages() {
        return groupMessageRepo.findAll();
    }
    public GroupMessage getGroupMessageById(String id) {
        GroupMessage groupMessage=groupMessageRepo.findById(id).orElseThrow(() -> new RuntimeException("Group message not found with id:" + id));
        return groupMessage;
    }

    public GroupMessage editGroupMessage(String id, String content) {
        GroupMessage existingGroupMessage = groupMessageRepo.findById(id).orElseThrow(() -> new RuntimeException("Group message not found with id:" + id));
        existingGroupMessage.setContent(content);
        return groupMessageRepo.save(existingGroupMessage);
    }
    public String deleteGroupMessage(String id) {
        GroupMessage groupMessage = groupMessageRepo.findById(id).orElseThrow(() -> new RuntimeException("Group message not found with id:" + id));
        groupMessageRepo.deleteById(id);
        return "Group message with id: " + id + " deleted successfully";
    }


}
