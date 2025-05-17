package com.example.groupChatService.dto;

public class SendMessageRequest {
    private String content;

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
