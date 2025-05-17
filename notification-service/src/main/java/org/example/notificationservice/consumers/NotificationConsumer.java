package org.example.notificationservice.consumers;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.notificationservice.services.NotificationService;
import org.example.shared.config.RabbitMQConfig;
import org.example.shared.dto.NotificationDTO;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class NotificationConsumer {
    private final NotificationService notificationService;

    @RabbitListener(queues = RabbitMQConfig.NOTIFICATION_QUEUE)
    public void consumeNotification(NotificationDTO notificationDto) {
        log.info("Received notification: {}", notificationDto);
        notificationService.createNotification(notificationDto);
    }
}
