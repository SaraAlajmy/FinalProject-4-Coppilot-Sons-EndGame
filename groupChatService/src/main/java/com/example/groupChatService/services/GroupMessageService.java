package com.example.groupChatService.services;

import com.example.groupChatService.clients.UserClient;
import com.example.groupChatService.models.GroupChat;
import com.example.groupChatService.models.GroupMessage;
import com.example.groupChatService.repositories.GroupChatRepo;
import com.example.groupChatService.repositories.GroupMessageRepo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.example.groupChatService.dto.SendMessageRequest;
import com.example.groupChatService.annotations.AdminOnly;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class GroupMessageService {
    private final GroupMessageRepo groupMessageRepo;
    private final GroupChatRepo groupChatRepo;
    private final List<MessageListener> listeners = new ArrayList<>();
    private final UserClient userClient;

    @Autowired
    public GroupMessageService(GroupMessageRepo groupMessageRepo, GroupChatRepo groupChatRepo,
                               UserClient userClient
    ) {
        this.groupMessageRepo = groupMessageRepo;
        this.groupChatRepo = groupChatRepo;
        this.userClient = userClient;
    }

    public void addListener(MessageListener listener) {
        listeners.add(listener);
    }

    public void removeListener(MessageListener listener) {
        listeners.remove(listener);
    }

    public GroupMessage addGroupMessage(GroupMessage groupMessage) {
        return groupMessageRepo.save(groupMessage);
    }

    public List<GroupMessage> getAllGroupMessages() {
        return groupMessageRepo.findAll();
    }

    public GroupMessage getGroupMessageById(String id, String senderId) {
        GroupMessage groupMessage = groupMessageRepo.findById(id)
                .orElseThrow(() -> new RuntimeException(
                        "Group message not found with id:" + id));
        if(!isGroupMember(id, senderId)) {
            throw new RuntimeException("Sender is not a member of the group");
        }
        return groupMessage;
    }

    public GroupMessage editGroupMessage(String id, String content, String userId) {
        if(!isGroupMember(id, userId)) {
            throw new RuntimeException("User is not a member of the group");
        }
        GroupMessage existingGroupMessage = groupMessageRepo.findById(id)
            .orElseThrow(() -> new RuntimeException(
                    "Group message not found with id:" +
                        id));
        if(!existingGroupMessage.getSenderId().equals(userId)) {
            throw new RuntimeException("User is not the sender of the message");
        }
        existingGroupMessage.setContent(content);
        return groupMessageRepo.save(existingGroupMessage);
    }

    public String deleteGroupMessage(String id, String userId) {
        if(!isGroupMember(id, userId)) {
            throw new RuntimeException("User is not a member of the group");
        }
        groupMessageRepo.deleteById(id);
        return "Group message with id: " + id + " deleted successfully";
    }

    public GroupMessage archiveGroupMessage(String id, String userId) {
        if(!isGroupMember(id, userId)) {
            throw new RuntimeException("User is not a member of the group");
        }
        GroupMessage groupMessage = groupMessageRepo.findById(id)
                .orElseThrow(() -> new RuntimeException(
                        "Group message not found with id:" + id));
        groupMessage.setArchived(true);
        groupMessageRepo.save(groupMessage);
        return groupMessage;
    }

    public GroupMessage unarchiveGroupMessage(String id, String userId) {
        if(!isGroupMember(id, userId)) {
            throw new RuntimeException("User is not a member of the group");
        }
        GroupMessage groupMessage = groupMessageRepo.findById(id)
                .orElseThrow(() -> new RuntimeException(
                        "Group message not found with id:" + id));
        groupMessage.setArchived(false);
        groupMessageRepo.save(groupMessage);
        return groupMessage;
    }

    public List<GroupMessage> getArchivedGroupMessages(String groupId, String userId) {
        GroupChat group = groupChatRepo.findById(groupId)
                .orElseThrow(() -> new RuntimeException("Group not found"));
        if (!group.getMembers().contains(userId)) {
            throw new RuntimeException("User is not a member of the group");
        }
        return groupMessageRepo.findByGroupIdAndArchived(groupId, true);
    }

    public List<GroupMessage> getUnarchivedGroupMessages(String groupId, String userId) {
        GroupChat group = groupChatRepo.findById(groupId)
                .orElseThrow(() -> new RuntimeException("Group not found"));
        if (!group.getMembers().contains(userId)) {
            throw new RuntimeException("User is not a member of the group");
        }
        return groupMessageRepo.findByGroupIdAndArchived(groupId, false);
    }

    public List<GroupMessage> filterGroupMessagesBySenderId(String groupId, String senderId, String userId) {
        GroupChat group = groupChatRepo.findById(groupId)
                .orElseThrow(() -> new RuntimeException("Group not found"));
        if (!group.getMembers().contains(userId)) {
            throw new RuntimeException("User is not a member of the group");
        }
        if (!group.getMembers().contains(senderId)) {
            throw new RuntimeException("Sender is not a member of the group");
        }
        return groupMessageRepo.findByGroupIdAndSenderId(groupId, senderId);
    }

    public GroupMessage sendMessage(SendMessageRequest request, String senderUsername, String senderId,
            String groupId) {
        GroupChat group = groupChatRepo.findById(groupId)
                .orElseThrow(() -> new RuntimeException("Group not found"));

        if (!group.getMembers().contains(senderId)) {
            throw new RuntimeException("Sender is not a member of the group");
        }

        if (group.isAdminOnlyMessages() && !group.getAdmins().contains(senderId)) {
            throw new RuntimeException("Only admins can send messages in this group");
        }

        List<String> mentionedUserIds = extractMentions(request.getContent());
        for (String mentioned : mentionedUserIds) {
            if (!group.getMembers().contains(mentioned)) {
                throw new RuntimeException("Mentioned user with id " + mentioned + " is not in the group");
            }
        }
        GroupMessage message = new GroupMessage(
                groupId,
                senderId,
                request.getContent(),
                mentionedUserIds);
        GroupMessage saved = groupMessageRepo.save(message);

        for (MessageListener listener : listeners) {
            listener.onNewMessage(saved, group, senderUsername);
        }
        return saved;
    }

    private List<String> extractMentions(String content) {
        List<String> mentionsUsernames = new ArrayList<>();
        Pattern pattern = Pattern.compile("@(\\w+)");
        Matcher matcher = pattern.matcher(content);
        while (matcher.find()) {
            mentionsUsernames.add(matcher.group(1));
        }
        List<String> mentionedIds = new ArrayList<>();
        var usernamesToIds = userClient.getUsersIdsByUsernames(mentionsUsernames);
        for (String username : mentionsUsernames) {
            if (usernamesToIds.containsKey(username)) {
                mentionedIds.add(usernamesToIds.get(username));
            } else {
                throw new RuntimeException("User with username " + username + " not found");
            }
        }

        return mentionedIds;
    }

    private boolean isGroupMember(String messageId, String userId) {
        GroupMessage message = groupMessageRepo.findById(messageId)
                .orElseThrow(() -> new RuntimeException("Message not found"));
        GroupChat group = groupChatRepo.findById(message.getGroupId())
                .orElseThrow(() -> new RuntimeException("Group not found"));
        return group.getMembers().contains(userId);
    }

}
