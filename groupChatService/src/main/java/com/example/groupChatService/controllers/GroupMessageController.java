package com.example.groupChatService.controllers;
import com.example.groupChatService.dto.SendMessageRequest;
import com.example.groupChatService.models.GroupMessage;
import com.example.groupChatService.services.GroupMessageService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/groupMessage")
public class GroupMessageController {
    private final GroupMessageService groupMessageService;
    private static final Logger logger = LoggerFactory.getLogger(GroupMessageController.class);

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
    public String deleteGroupMessage(@PathVariable String id,
        @RequestHeader("userId") String userId
    ) {
        return groupMessageService.deleteGroupMessage(id, userId);
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
        logger.info("Received group message send request from user {} to group {}", senderId, groupId);
        GroupMessage createdMessage= groupMessageService.sendMessage(request, userName, senderId, groupId);
        logger.info("Message sent successfully from user {} to group {}", senderId, groupId);
        return createdMessage;
    }
    
    
}
