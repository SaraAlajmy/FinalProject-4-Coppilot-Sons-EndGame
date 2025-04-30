package com.example.groupChatService.dto;

import java.util.List;

public class GroupUpdateRequest {
    private String name;
    private String description;
    private String emoji;
    private List<String> members;
    private List<String> admins;
    private Boolean adminOnlyMessages;

    public GroupUpdateRequest() {
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

    public List<String> getAdmins() {
        return admins;
    }

    public void setAdmins(List<String> admins) {
        this.admins = admins;
    }
    public Boolean getAdminOnlyMessages() {
        return adminOnlyMessages;
    }

    public void setAdminOnlyMessages(Boolean adminOnlyMessages) {
        this.adminOnlyMessages = adminOnlyMessages;
    }
}


