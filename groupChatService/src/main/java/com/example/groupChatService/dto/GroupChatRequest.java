package com.example.groupChatService.dto;

import java.util.List;

public class GroupChatRequest {
    private String name;
    private String description;
    private String emoji;
    private String colorTheme;
    private List<String> members;
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

    public String getColorTheme() {
        return colorTheme;
    }

    public void setColorTheme(String colorTheme) {
        this.colorTheme = colorTheme;
    }

    public Boolean getAdminOnlyMessages() {
        return adminOnlyMessages;
    }

    public void setAdminOnlyMessages(Boolean adminOnlyMessages) {
        this.adminOnlyMessages = adminOnlyMessages;
    }
}
