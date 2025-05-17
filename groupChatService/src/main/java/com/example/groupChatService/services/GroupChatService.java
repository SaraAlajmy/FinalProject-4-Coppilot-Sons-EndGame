package com.example.groupChatService.services;


import com.example.groupChatService.dto.GroupChatRequest;
import com.example.groupChatService.dto.GroupUpdateRequest;
import com.example.groupChatService.models.GroupChat;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.example.groupChatService.repositories.GroupChatRepo;
import org.example.shared.Utils;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import static org.springframework.util.StringUtils.capitalize;

@Service
public class GroupChatService {
    private final GroupChatRepo groupChatRepo;

    @Autowired
    public GroupChatService(GroupChatRepo groupChatRepo) {
        this.groupChatRepo = groupChatRepo;
    }

    public GroupChat addGroupChat(GroupChatRequest groupChatRequest, String userId) {
        String name = groupChatRequest.getName();
        List<String> admins = new ArrayList<String>();
        List<String> members = groupChatRequest.getMembers() == null ? new ArrayList<String>() : groupChatRequest.getMembers();
        admins.add(userId);
        members.add(userId);
        if (name == null || name.isEmpty()) {
            throw new RuntimeException("Group chat name cannot be null or empty");
        }
        if (userId == null || userId.isEmpty()) {
            throw new RuntimeException("Creator ID cannot be null or empty");
        }
        GroupChat.groupChatBuilder builder = new GroupChat.groupChatBuilder(name, userId, admins, members);
        if (groupChatRequest.getDescription() != null) {
            builder.setDescription(groupChatRequest.getDescription());
        }
        if (groupChatRequest.getEmoji() != null) {
            builder.setEmoji(groupChatRequest.getEmoji());
        }
        if (groupChatRequest.getColorTheme() != null) {
            builder.setColorTheme(groupChatRequest.getColorTheme());
        }
        if (groupChatRequest.getAdminOnlyMessages() != null) {
            builder.setAdminOnlyMessages(groupChatRequest.getAdminOnlyMessages());
        }
        GroupChat groupChat = builder.build();
        System.out.println("Group chat created: " + groupChat);
        return groupChatRepo.save(groupChat);
    }

    public List<GroupChat> getAllGroupChat() {
        return groupChatRepo.findAll();
    }

    public GroupChat getGroupChatById(String id) {
        GroupChat groupChat = groupChatRepo.findById(id).orElse(null);
        if (groupChat == null) {
            throw new RuntimeException("Group chat not found with id: " + id);
        }
        return groupChat;
    }

    public GroupChat updateGroupChat(String id,String userId, GroupUpdateRequest groupUpdateRequest) {
        GroupChat groupChat = groupChatRepo.findById(id).orElseThrow(() -> new RuntimeException("Group chat not found with id:" + id));
        if (!groupChat.getMembers().contains(userId)) {
            throw new RuntimeException("You are not a member of this group chat");
        }
        GroupChat.groupChatBuilder builder = groupChat.toBuilder();
        builder.setId(id);
        Utils.copyPropertiesWithReflection(groupUpdateRequest, builder);
        return groupChatRepo.save(builder.build());
    }

    public GroupChat activateAdminOnlyMessages(String id, String userId) {
        GroupChat groupChat = groupChatRepo.findById(id).orElseThrow(() -> new RuntimeException("Group chat not found with id:" + id));
        if (!groupChat.getAdmins().contains(userId)) {
            throw new RuntimeException("You are not an admin of this group chat");
        }
        GroupChat.groupChatBuilder builder = groupChat.toBuilder();
        builder.setId(id);
        builder.setAdminOnlyMessages(true);
        return groupChatRepo.save(builder.build());
    }

    public GroupChat unactivateAdminOnlyMessages(String id, String userId) {
        GroupChat groupChat = groupChatRepo.findById(id).orElseThrow(() -> new RuntimeException("Group chat not found with id:" + id));
        if (!groupChat.getAdmins().contains(userId)) {
            throw new RuntimeException("You are not an admin of this group chat");
        }
        GroupChat.groupChatBuilder builder = groupChat.toBuilder();
        builder.setId(id);
        builder.setAdminOnlyMessages(false);
        return groupChatRepo.save(builder.build());
    }

    public GroupChat addMember(String id, String userId, String memberId) {
        GroupChat groupChat = groupChatRepo.findById(id).orElseThrow(() -> new RuntimeException("Group chat not found with id:" + id));
        if (!groupChat.getAdmins().contains(userId)) {
            throw new RuntimeException("You are not an admin of this group chat");
        }
        if (groupChat.getMembers().contains(memberId)) {
            throw new RuntimeException("member with id " + memberId + " is already a member of this group");
        }
        GroupChat.groupChatBuilder builder = groupChat.toBuilder();
        builder.setId(id);
        builder.addMember(memberId);
        return groupChatRepo.save(builder.build());
    }

    public GroupChat removeMember(String id, String userId, String memberId) {
        GroupChat groupChat = groupChatRepo.findById(id).orElseThrow(() -> new RuntimeException("Group chat not found with id:" + id));
        if (!groupChat.getAdmins().contains(userId)) {
            throw new RuntimeException("You are not an admin of this group chat");
        }
        if (!groupChat.getMembers().contains(memberId)) {
            throw new RuntimeException("member with id " + memberId + " is not member of this group");
        }
        GroupChat.groupChatBuilder builder = groupChat.toBuilder();
        builder.setId(id);
        builder.removeMember(memberId);
        builder.removeAdmin(memberId);
        return groupChatRepo.save(builder.build());
    }

    public GroupChat makeAdmin(String id, String userId, String memberId) {
        GroupChat groupChat = groupChatRepo.findById(id).orElseThrow(() -> new RuntimeException("Group chat not found with id:" + id));
        if (!groupChat.getAdmins().contains(userId)) {
            throw new RuntimeException("You are not an admin of this group chat");
        }
        if (!groupChat.getMembers().contains(memberId)) {
            throw new RuntimeException("member with id " + memberId + " is not member of this group");
        }
        GroupChat.groupChatBuilder builder = groupChat.toBuilder();
        builder.setId(id);
        builder.makeAdmin(memberId);
        return groupChatRepo.save(builder.build());
    }

    public GroupChat removeAdmin(String id, String userId, String memberId) {
        GroupChat groupChat = groupChatRepo.findById(id).orElseThrow(() -> new RuntimeException("Group chat not found with id:" + id));
        if (!groupChat.getAdmins().contains(userId)) {
            throw new RuntimeException("You are not an admin of this group chat");
        }
        if (!groupChat.getMembers().contains(memberId)) {
            throw new RuntimeException("member with id " + memberId + " is not member of this group");
        }
        GroupChat.groupChatBuilder builder = groupChat.toBuilder();
        builder.setId(id);
        builder.removeAdmin(memberId);
        return groupChatRepo.save(builder.build());
    }

    public String deleteGroupChat(String id, String userId) {
        GroupChat groupChat = groupChatRepo.findById(id).orElseThrow(() -> new RuntimeException("Group chat not found with id:" + id));
        if (!groupChat.getCreatorId().equals(userId)) {
            return "You can not delete the group chat because you are not the owner";
        }
        groupChatRepo.deleteById(id);
        return "Group chat deleted successfully with id: " + id;
    }

    public List<GroupChat> getMemberGroupChats(String memberId) {
        List<GroupChat> groupChats = groupChatRepo.findByMembersContaining(memberId);
        if (groupChats.isEmpty()) {
            throw new RuntimeException("No group chats found for member with id: " + memberId);
        }
        return groupChats;

    }


}
