package com.example.groupChatService.controllers;

import com.example.groupChatService.dto.GroupChatRequest;
import com.example.groupChatService.dto.GroupUpdateRequest;
import com.example.groupChatService.models.GroupChat;
import com.example.groupChatService.services.GroupChatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/groupChat")
public class GroupChatController {
    private final GroupChatService groupChatService;
    @Autowired
    public GroupChatController(GroupChatService groupChatService) {
        this.groupChatService = groupChatService;
    }
    @GetMapping("/allGroupChat")
    public List<GroupChat> getAllGroupChat() {
        try {
            return groupChatService.getAllGroupChat();
        } catch (Exception e) {
            System.out.println("Error fetching group chats: " + e.getMessage());
            return null;
        }

    }
    @GetMapping("/{id}")
    public GroupChat getGroupChatById(@PathVariable String id) {
        try {
            return groupChatService.getGroupChatById(id);
        } catch (Exception e) {
            System.out.println("Error fetching group chat by ID: " + e.getMessage());
            return null;
        }
    }
    @DeleteMapping("/{id}")
    public String deleteGroupChatById(@PathVariable String id,@RequestHeader("userId") String userId) {
        try {
            return groupChatService.deleteGroupChat(id,userId);
        } catch (Exception e) {
            return "Error deleting group chat by ID: " + e.getMessage();
        }
    }
    @PostMapping("/addGroupChat")
    public GroupChat addGroupChat(@RequestBody GroupChatRequest groupChatRequest,@RequestHeader("userId") String userId){
        try {
            return groupChatService.addGroupChat(groupChatRequest,userId);
        } catch (Exception e) {
            System.out.println("Error adding group chat: " + e.getMessage());
            return null;
        }
    }
    @PutMapping("/update/{id}")
    public GroupChat updateGroupChat(@PathVariable String id,@RequestBody GroupUpdateRequest groupUpdateRequest){
        try{
            return groupChatService.updateGroupChat(id,groupUpdateRequest);
        }catch (Exception e) {
            System.out.println("Error updating group chat: " + e.getMessage());
            return null;
        }
    }
    @GetMapping("/getGroupChatByMemberId/{id}")
    public List<GroupChat> getGroupChatByMemberId(@PathVariable String id) {
        try {
            return groupChatService.getMemberGroupChats(id);
        } catch (Exception e) {
            System.out.println("Error fetching group chat by member ID: " + e.getMessage());
            return null;
        }
    }
}
