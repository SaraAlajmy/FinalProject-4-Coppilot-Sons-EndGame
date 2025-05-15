package com.example.groupChatService.services;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

import com.example.groupChatService.models.GroupMessage;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

@Component
public class NotificationListener implements MessageListener {

    private final RabbitTemplate rabbitTemplate;
    private final ObjectMapper objectMapper;

    public NotificationListener(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
        this.objectMapper = new ObjectMapper();
        this.objectMapper.registerModule(new JavaTimeModule());
    }


    @Override
    public void onNewMessage(GroupMessage message) {
        try {
            String json = objectMapper.writeValueAsString(message);
            System.out.println("🔔 NotificationListener received new message: " + json);
            rabbitTemplate.convertAndSend("notificationQueue", json);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
    }
}
