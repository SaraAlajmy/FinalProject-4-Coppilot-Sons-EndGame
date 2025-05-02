package com.example.groupChatService.controllers;

import com.example.groupChatService.models.GroupMessage;
import com.example.groupChatService.services.GroupMessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/groupMessage")
public class GroupMessageController {
    private final GroupMessageService groupMessageService;
    @Autowired

    public GroupMessageController(GroupMessageService groupMessageService) {
        this.groupMessageService = groupMessageService;
    }

    @GetMapping("/allMessages")
    public List<GroupMessage> getAllGroupMessages() {
        try {
            return groupMessageService.getAllGroupMessages();
        } catch (Exception e) {
            System.out.println("Error fetching group messages: " + e.getMessage());
            return null;
        }
    }
    @GetMapping("/{groupId}")
    public List<GroupMessage> getGroupMessagesByGroupChatId(@PathVariable String groupId) {
        try {
            return groupMessageService.getGroupMessagesByGroupId(groupId);
        } catch (Exception e) {
            System.out.println("Error fetching group messages by group chat ID: " + e.getMessage());
            return null;
        }
    }
    @GetMapping("/{id}")
    public List<GroupMessage> getGroupMessageById(@PathVariable String id) {
        try {
            return groupMessageService.getUnarchivedGroupMessages(id);
        } catch (Exception e) {
            System.out.println("Error fetching group message by ID: " + e.getMessage());
            return null;
        }
    }
    @PostMapping("/")
    public GroupMessage addGroupMessage(@RequestBody GroupMessage groupMessage) {
        try {
            return groupMessageService.addGroupMessage(groupMessage);
        } catch (Exception e) {
            System.out.println("Error adding group message: " + e.getMessage());
            return null;
        }
    }
    @PutMapping("/{id}")
    public GroupMessage editGroupMessage(@PathVariable String id, @RequestBody String content) {
        try {
            return groupMessageService.editGroupMessage(id, content);
        } catch (Exception e) {
            System.out.println("Error editing group message: " + e.getMessage());
            return null;
        }
    }
    @DeleteMapping("/{id}")
    public void deleteGroupMessage(@PathVariable String id) {
        try {
            groupMessageService.deleteGroupMessage(id);
        } catch (Exception e) {
            System.out.println("Error deleting group message: " + e.getMessage());
        }
    }
    @PutMapping("/archive/{id}")
    public void archiveGroupMessage(@PathVariable String id) {
//        try {
            groupMessageService.archiveGroupMessage(id);
//        } catch (Exception e) {
//            System.out.println("Error archiving group message: " + e.getMessage());
//        }
    }
    @PutMapping("/unarchive/{id}")
    public void unarchiveGroupMessage(@PathVariable String id) {
//        try {
            groupMessageService.unarchiveGroupMessage(id);
//        } catch (Exception e) {
//            System.out.println("Error unarchiving group message: " + e.getMessage());
    }

    @GetMapping("/archived/{groupId}")
    public List<GroupMessage> getArchivedGroupMessages(@PathVariable String groupId) {
//        try {
            return groupMessageService.getArchivedGroupMessages(groupId);
//        } catch (Exception e) {
//            System.out.println("Error fetching archived group messages: " + e.getMessage());
//            return null;
//        }
    }

    @GetMapping("/filter/{groupId}/{senderId}")
    public List<GroupMessage> filterGroupMessagesBySenderId(@PathVariable String groupId, @PathVariable String senderId) {
//        try {
                return groupMessageService.filterGroupMessagesBySenderId(groupId, senderId);
//        } catch (Exception e) {
//            System.out.println("Error filtering group messages by sender ID: " + e.getMessage());
//            return null;
//        }

    }






}
