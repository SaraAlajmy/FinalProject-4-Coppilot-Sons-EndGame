package com.example.groupChatService.controllers;

import com.example.groupChatService.dto.GroupChatRequest;
import com.example.groupChatService.dto.GroupUpdateRequest;
import com.example.groupChatService.models.GroupChat;
import com.example.groupChatService.services.GroupChatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;


@RestController
@RequestMapping("/groupChat")
public class GroupChatController {
    private final GroupChatService groupChatService;
    @Autowired
    public GroupChatController(GroupChatService groupChatService) {
        this.groupChatService = groupChatService;
    }
    @GetMapping("/allGroupChat")
    public ResponseEntity<?> getAllGroupChat() {
        try {
            List<GroupChat> groupChats = groupChatService.getAllGroupChat();
            return ResponseEntity.ok(groupChats);
        } catch (Exception e) {
            System.err.println("Error fetching group chats: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Something went wrong: " + e.getMessage());
        }
    }
    @GetMapping("/{id}")
    public ResponseEntity<?> getGroupChatById(@PathVariable String id,@RequestHeader("userId") String userId) {
        try {
            GroupChat groupChat = groupChatService.getGroupChatById(id,userId);
            return ResponseEntity.ok(groupChat); // 200 OK
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(e.getMessage()); // 404 Not Found with message
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Unexpected error: " + e.getMessage()); // 500
        }
    }
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteGroupChatById(@PathVariable String id, @RequestHeader("userId") String userId) {
        try {
            String result = groupChatService.deleteGroupChat(id, userId);
            return ResponseEntity.ok(result); // 200 OK with success message
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error deleting group chat: " + e.getMessage());
        }
    }
    @PostMapping("/addGroupChat")
    public ResponseEntity<?> addGroupChat(@RequestBody GroupChatRequest groupChatRequest, @RequestHeader("userId") String userId) {
        try {
            GroupChat createdGroupChat = groupChatService.addGroupChat(groupChatRequest, userId);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdGroupChat); // 201 Created with created object
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error adding group chat: " + e.getMessage());
        }
    }
    @PutMapping("/update/{id}")
    public ResponseEntity<?> updateGroupChat(@PathVariable String id,@RequestHeader("userId") String userId, @RequestBody GroupUpdateRequest groupUpdateRequest) {
        try {
            GroupChat updatedGroupChat = groupChatService.updateGroupChat(id, userId,groupUpdateRequest);
            return ResponseEntity.ok(updatedGroupChat); // 200 OK with updated object
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error updating group chat: " + e.getMessage());
        }
    }
    @PutMapping("/activateAdminOnlyMessages/{id}")
    public ResponseEntity<?> activateAdminOnlyMessages(@PathVariable String id, @RequestHeader("userId") String userId) {
        try {
            GroupChat updatedGroupChat = groupChatService.activateAdminOnlyMessages(id, userId);
            return ResponseEntity.ok(updatedGroupChat);  // 200 OK with updated group chat
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error activating admin-only messages: " + e.getMessage());
        }
    }

    @PutMapping("/unactivateAdminOnlyMessages/{id}")
    public ResponseEntity<?> unactivateAdminOnlyMessages(@PathVariable String id, @RequestHeader("userId") String userId) {
        try {
            GroupChat updatedGroupChat = groupChatService.unactivateAdminOnlyMessages(id, userId);
            return ResponseEntity.ok(updatedGroupChat);  // 200 OK with updated group chat
        } catch (RuntimeException e) {
            // e.g. group chat not found or unauthorized action
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error unactivating admin-only messages: " + e.getMessage());
        }
    }

    @PutMapping("/addMember/{id}")
    public ResponseEntity<?> addMember(@PathVariable String id, @RequestHeader("userId") String userId, @RequestBody Map<String, String> body) {
        try {
            String memberId = body.get("memberId");
            GroupChat updatedGroupChat = groupChatService.addMember(id, userId, memberId);
            return ResponseEntity.ok(updatedGroupChat);  // 200 OK with updated group chat
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error adding member to group chat: " + e.getMessage());
        }
    }

    @PutMapping("/removeMember/{id}")
    public ResponseEntity<?> removeMember(@PathVariable String id, @RequestHeader("userId") String userId, @RequestBody Map<String, String> body) {
        try {
            String memberId = body.get("memberId");
            GroupChat updatedGroupChat = groupChatService.removeMember(id, userId, memberId);
            return ResponseEntity.ok(updatedGroupChat);  // 200 OK with updated group chat
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error removing member from group chat: " + e.getMessage());
        }
    }

    @PutMapping("/makeAdmin/{id}")
    public ResponseEntity<?> makeAdmin(@PathVariable String id, @RequestHeader("userId") String userId, @RequestBody Map<String, String> body) {
        try {
            String memberId = body.get("memberId");
            GroupChat updatedGroupChat = groupChatService.makeAdmin(id, userId, memberId);
            return ResponseEntity.ok(updatedGroupChat);  // 200 OK with updated group chat
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error making admin for group chat: " + e.getMessage());
        }
    }

    @PutMapping("/removeAdmin/{id}")
    public ResponseEntity<?> removeAdmin(@PathVariable String id, @RequestHeader("userId") String userId, @RequestBody Map<String, String> body) {
        try {
            String memberId = body.get("memberId");
            GroupChat updatedGroupChat = groupChatService.removeAdmin(id, userId, memberId);
            return ResponseEntity.ok(updatedGroupChat);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error removing admin for group chat: " + e.getMessage());
        }
    }

    @GetMapping("/getGroupChatByMemberId")
    public ResponseEntity<?> getGroupChatByMemberId(@RequestHeader("userId") String id) {
        try {
            List<GroupChat> groupChats = groupChatService.getMemberGroupChats(id);
            return ResponseEntity.ok(groupChats);  // 200 OK with the list
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error fetching group chats by member ID: " + e.getMessage());
        }
    }

}
