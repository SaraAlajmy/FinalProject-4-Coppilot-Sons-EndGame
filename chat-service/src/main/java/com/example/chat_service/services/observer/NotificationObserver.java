package com.example.chat_service.services.observer;

import com.example.chat_service.models.Message;
import org.example.shared.dto.DirectMessageNotificationDTO;
import org.example.shared.producer.NotificationProducer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Service
public class NotificationObserver implements Observer {

    @Autowired
    private NotificationProducer notificationProducer;

    @Override
    public void update(Message message) {
        DirectMessageNotificationDTO notification = messageToNotification(message);
        notificationProducer.sendMessage(notification);
    }


    private DirectMessageNotificationDTO messageToNotification(Message message) {
        return DirectMessageNotificationDTO
            .builder()
            .recipientUserId(message.getReceiverId())
            .senderUserId(message.getSenderId())
            .senderUsername(message.getSenderUserName())
            .messageId(message.getId())
            .messageText(message.getContent())
            .messageTimestamp(message.getCreatedAt())
            .chatId(message.getChatId())
            .build();
    }


}
