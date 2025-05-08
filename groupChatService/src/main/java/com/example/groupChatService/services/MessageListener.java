package com.example.groupChatService.services;

import com.example.groupChatService.models.GroupMessage;

public interface MessageListener {
    void onNewMessage(GroupMessage message);
}
