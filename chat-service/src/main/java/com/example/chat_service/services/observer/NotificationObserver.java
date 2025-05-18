package com.example.chat_service.services.observer;

import com.example.chat_service.models.Message;
import lombok.extern.slf4j.Slf4j;
import org.example.shared.dto.DirectMessageNotificationDTO;
import org.example.shared.producer.NotificationProducer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class NotificationObserver implements Observer {

    @Autowired
    private NotificationProducer notificationProducer;

    @Override
    public void update(Message message) {
        DirectMessageNotificationDTO notification = messageToNotification(message);
        log.info("Sending message notification to  rabbit mq " + notification.getMessageId());
        notificationProducer.sendMessage(notification);
    }


    private DirectMessageNotificationDTO messageToNotification(Message message) {
        return new DirectMessageNotificationDTO(
            message.getReceiverId(),
            message.getSenderId(),
            message.getSenderUserName(),
            message.getId(),
            message.getContent(),
            message.getCreatedAt(),
            message.getChatId()
        );
    }
}
