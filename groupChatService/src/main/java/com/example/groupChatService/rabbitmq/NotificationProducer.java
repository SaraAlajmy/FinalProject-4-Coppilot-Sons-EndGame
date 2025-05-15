package com.example.groupChatService.rabbitmq;

import com.example.groupChatService.dto.GroupNotificationDTO;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class NotificationProducer {

    @Autowired
    private RabbitTemplate rabbitTemplate;

    public void sendMessage(GroupNotificationDTO data) {
        rabbitTemplate.convertAndSend(
                RabbitMQConfig.EXCHANGE,
                RabbitMQConfig.MESSAGE_ROUTING_KEY,
                data
        );
    }

}
