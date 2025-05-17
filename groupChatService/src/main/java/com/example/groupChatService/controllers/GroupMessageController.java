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
    public List<GroupMessage> getGroupMessageById(@PathVariable String groupId) {
        return groupMessageService.getUnarchivedGroupMessages(groupId);
    }

    @PostMapping("/")
    public GroupMessage addGroupMessage(@RequestBody GroupMessage groupMessage) {
        return groupMessageService.addGroupMessage(groupMessage);
    }

    @PutMapping("/{id}")
    public GroupMessage editGroupMessage(@PathVariable String id, @RequestBody Map<String, String> body) {
        String content = body.get("content");
        return groupMessageService.editGroupMessage(id, content);
    }

    @DeleteMapping("/{id}")
    public void deleteGroupMessage(@PathVariable String id) {
        groupMessageService.deleteGroupMessage(id);
    }

    @PutMapping("/archive/{id}")
    public void archiveGroupMessage(@PathVariable String id) {
        groupMessageService.archiveGroupMessage(id);
    }

    @PutMapping("/unarchive/{id}")
    public void unarchiveGroupMessage(@PathVariable String id) {
        groupMessageService.unarchiveGroupMessage(id);
    }

    @GetMapping("/archived/{groupId}")
    public List<GroupMessage> getArchivedGroupMessages(@PathVariable String groupId) {
        return groupMessageService.getArchivedGroupMessages(groupId);
    }

    @GetMapping("/filter/{groupId}/{senderId}")
    public List<GroupMessage> filterGroupMessagesBySenderId(@PathVariable String groupId, @PathVariable String senderId) {
        return groupMessageService.filterGroupMessagesBySenderId(groupId, senderId);
    }

    @PostMapping("/send/{groupId}")
    public GroupMessage sendMessage(
        @RequestBody SendMessageRequest request,
        @RequestHeader("userName") String userName,
        @RequestHeader("senderId") String senderId,
        @PathVariable String groupId
    ) {
        return groupMessageService.sendMessage(request, userName, senderId, groupId);
    }
    
    
}
