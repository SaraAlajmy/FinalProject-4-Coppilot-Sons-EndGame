package com.example.chat_service.services.observer;

import com.example.chat_service.models.Message;

public interface Observer {

    void update(Message message);
}
