package com.example.chat_service.services;

import com.example.chat_service.dto.NotificationDTO;
import com.example.chat_service.models.Message;
import com.example.chat_service.rabbitmq.NotificationProducer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Service
public class NotificationObserver implements Observer {

    @Autowired
    private NotificationProducer notificationProducer;

    @Override
    public void update(Message message) {
        NotificationDTO notification = messageToNotification(message);
        notificationProducer.sendMessage(notification);
    }


    private NotificationDTO messageToNotification(Message message) {
        return new NotificationDTO(
                message.getReceiverId(),
                "direct_message",
                message.getSenderId(),
                message.getSenderUserName(),
                message.getId(),
                message.getContent(),
                message.getCreatedAt(),
                message.getChatId()
        );
    }


}
