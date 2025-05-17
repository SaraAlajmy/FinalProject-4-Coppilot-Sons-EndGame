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

    public GroupMessage getGroupMessageById(String id) {
        GroupMessage groupMessage = groupMessageRepo.findById(id)
                .orElseThrow(() -> new RuntimeException(
                        "Group message not found with id:" + id));
        return groupMessage;
    }

    public GroupMessage editGroupMessage(String id, String content) {
        GroupMessage existingGroupMessage = groupMessageRepo.findById(id)
                .orElseThrow(() -> new RuntimeException(
                        "Group message not found with id:" +
                                id));
        existingGroupMessage.setContent(content);
        return groupMessageRepo.save(existingGroupMessage);
    }

    public String deleteGroupMessage(String id) {
        GroupMessage groupMessage = groupMessageRepo.findById(id)
                .orElseThrow(() -> new RuntimeException(
                        "Group message not found with id:" + id));
        groupMessageRepo.deleteById(id);
        return "Group message with id: " + id + " deleted successfully";
    }

    public String archiveGroupMessage(String id) {
        GroupMessage groupMessage = groupMessageRepo.findById(id)
                .orElseThrow(() -> new RuntimeException(
                        "Group message not found with id:" + id));
        // TODO: validate user is in this group
        groupMessage.setArchived(true);
        groupMessageRepo.save(groupMessage);
        return "Group message with id: " + id + " archived successfully";
    }

    public String unarchiveGroupMessage(String id) {
        GroupMessage groupMessage = groupMessageRepo.findById(id)
                .orElseThrow(() -> new RuntimeException(
                        "Group message not found with id:" + id));
        // TODO: validate user is in this group
        groupMessage.setArchived(false);
        groupMessageRepo.save(groupMessage);
        return "Group message with id: " + id + " unarchived successfully";
    }

    public List<GroupMessage> getArchivedGroupMessages(String groupId) {
        return groupMessageRepo.findByGroupIdAndArchived(groupId, true);
    }

    public List<GroupMessage> getUnarchivedGroupMessages(String groupId) {
        return groupMessageRepo.findByGroupIdAndArchived(groupId, false);
    }

    public List<GroupMessage> filterGroupMessagesBySenderId(String groupId, String senderId) {
        return groupMessageRepo.findByGroupIdAndSenderId(groupId, senderId);
    }

    public GroupMessage sendMessage(SendMessageRequest request, String senderUsername, String senderId,
            String groupId) {
        GroupChat group = groupChatRepo.findById(request.getGroupId())
                .orElseThrow(() -> new RuntimeException("Group not found"));

        if (!group.getMembers().contains(request.getSenderId())) {
            throw new RuntimeException("Sender is not a member of the group");
        }

        if (group.isAdminOnlyMessages() && !group.getAdmins().contains(request.getSenderId())) {
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

}
