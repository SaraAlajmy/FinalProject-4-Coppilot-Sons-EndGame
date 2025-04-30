package com.example.groupChatService.dto;

import java.util.List;

public class GroupChatRequest {
    private String name;
    private String description;
    private String emoji;
    private List<String> members;
    private String creatorId;
    private Boolean adminOnlyMessages;

    public GroupChatRequest() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getEmoji() {
        return emoji;
    }

    public void setEmoji(String emoji) {
        this.emoji = emoji;
    }

    public List<String> getMembers() {
        return members;
    }

    public void setMembers(List<String> members) {
        this.members = members;
    }

    public String getCreatorId() {
        return creatorId;
    }

    public void setCreatorId(String creatorId) {
        this.creatorId = creatorId;
    }

    public Boolean getAdminOnlyMessages() {
        return adminOnlyMessages;
    }

    public void setAdminOnlyMessages(Boolean adminOnlyMessages) {
        this.adminOnlyMessages = adminOnlyMessages;
    }
}
