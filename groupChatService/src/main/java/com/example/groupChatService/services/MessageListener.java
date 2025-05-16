package com.example.groupChatService.services;

import com.example.groupChatService.models.GroupChat;
import com.example.groupChatService.models.GroupMessage;

public interface MessageListener {
    void onNewMessage(GroupMessage message, GroupChat chat, String senderUsername);
}
