package com.example.groupChatService.dto;

public class SendMessageRequest {
    private String content;

    // Empty constructor for Spring serialization to work
    public SendMessageRequest() {
    }

    public SendMessageRequest(String content) {
        this.content = content;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
