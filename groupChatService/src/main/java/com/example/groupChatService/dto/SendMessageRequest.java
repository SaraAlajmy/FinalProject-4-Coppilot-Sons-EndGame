package com.example.groupChatService.dto;

public class SendMessageRequest {
    private String groupId;
    private String senderId;
    private String content;

    public SendMessageRequest(String groupId, String senderId, String content) {
        this.groupId = groupId;
        this.senderId = senderId;
        this.content = content;
    }

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public String getSenderId() {
        return senderId;
    }

    public void setSenderId(String senderId) {
        this.senderId = senderId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
