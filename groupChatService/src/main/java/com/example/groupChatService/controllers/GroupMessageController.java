package com.example.groupChatService.controllers;
import com.example.groupChatService.dto.SendMessageRequest;
import com.example.groupChatService.models.GroupMessage;
import com.example.groupChatService.services.GroupMessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

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
        return groupMessageService.getAllGroupMessages();
    }

    @GetMapping("/{groupId}")
    public List<GroupMessage> getGroupMessageById(
        @PathVariable String groupId,
        @RequestHeader("userId") String userId) {
        return groupMessageService.getUnarchivedGroupMessages(groupId, userId);
    }

    @PostMapping("/")
    public GroupMessage addGroupMessage(@RequestBody GroupMessage groupMessage) {
        return groupMessageService.addGroupMessage(groupMessage);
    }

    @PutMapping("/{id}")
    public GroupMessage editGroupMessage(
        @PathVariable String id, 
        @RequestBody Map<String, String> body,
        @RequestHeader("userId") String userId
    ) {
        String content = body.get("content");
        return groupMessageService.editGroupMessage(id, content, userId);
    }

    @DeleteMapping("/{id}")
    public void deleteGroupMessage(@PathVariable String id,
        @RequestHeader("userId") String userId
    ) {
        groupMessageService.deleteGroupMessage(id, userId);
    }

    @PutMapping("/archive/{id}")
    public GroupMessage archiveGroupMessage(
        @PathVariable String id,
        @RequestHeader("userId") String userId
    ) {
        return groupMessageService.archiveGroupMessage(id, userId);
    }

    @PutMapping("/unarchive/{id}")
    public GroupMessage unarchiveGroupMessage(
        @PathVariable String id,
        @RequestHeader("userId") String userId
    ) {
        return groupMessageService.unarchiveGroupMessage(id, userId);
    }

    @GetMapping("/archived/{groupId}")
    public List<GroupMessage> getArchivedGroupMessages(
        @PathVariable String groupId,
        @RequestHeader("userId") String userId
        ) {
        return groupMessageService.getArchivedGroupMessages(groupId, userId);
    }

    @GetMapping("/filter/{groupId}/{senderId}")
    public List<GroupMessage> filterGroupMessagesBySenderId(@PathVariable String groupId, @PathVariable String senderId,
        @RequestHeader("userId") String userId) {
        return groupMessageService.filterGroupMessagesBySenderId(groupId, senderId, userId);
    }

    @PostMapping("/send/{groupId}")
    public GroupMessage sendMessage(
        @RequestBody SendMessageRequest request,
        @RequestHeader("username") String userName,
        @RequestHeader("userId") String senderId,
        @PathVariable String groupId
    ) {
        return groupMessageService.sendMessage(request, userName, senderId, groupId);
    }
    
    
}
