package org.example.notificationservice.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.notificationservice.clients.UserClient;
import org.example.notificationservice.models.*;
import org.example.shared.config.RabbitMQConfig;
import org.example.shared.dto.NotificationDTO;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@Slf4j
@RequiredArgsConstructor
public class NotificationService {
    private final NotificationDeliveryService notificationDeliveryService;
    private final NotificationSettingsService notificationSettingsService;
    private final UserClient userClient;
    private final ObjectMapper objectMapper;

    public boolean createNotification(NotificationDTO notificationDto) {
        Notification notification = convertDtoToNotification(notificationDto);

        boolean success = true;
        for (String strategyType : NotificationStrategyType.getAllTypes()) {
            if (notificationSettingsService.isNotificationEnabled(
                notification.getRecipientUserId(),
                notification.getType(), strategyType
            )) {
                if (!notificationDeliveryService.deliverNotificationUsingStrategy(
                    notification,
                    strategyType
                )) {
                    success = false;
                    log.error(
                        "Failed to deliver Notification of type {} using strategy {} for recipient {}",
                        notification.getType(),
                        strategyType,
                        notification.getRecipientUserId()
                    );
                }

            }
        }

        return success;
    }

    private Notification convertDtoToNotification(NotificationDTO notificationDto) {
        // This would be better with a factory, but for simplicity, object mapping
        Notification notification = objectMapper.convertValue(notificationDto, Notification.class);

        var email = userClient.getUserEmailById(notificationDto.getRecipientUserId()).getBody();

        notification.setRecipientEmail(email);
        notification.setTimestamp(LocalDateTime.now());

        return notification;
    }

    public boolean sendResetPasswordNotification(
        String resetLink,
        String receipientEmail,
        String receipientName
    ) {

        Notification notification = ResetPasswordNotification
            .builder()
            .resetPasswordLink(resetLink)
            .recipientEmail(receipientEmail)
            .recipientName(receipientName)
            .timestamp(LocalDateTime.now())
            .type(NotificationType.RESET_PASSWORD)
            .build();
        if (!notificationDeliveryService.deliverNotificationUsingStrategy(
            notification,
            NotificationStrategyType.EMAIL
        )) {
            log.error(
                "Failed to deliver Reset Password Notification using Email strategy for recipient {}",
                notification.getRecipientUserId()
            );
            return false;
        } else {
            log.info(
                "Reset Password Notification sent successfully to {}",
                notification.getRecipientUserId()
            );
            return true;
        }

    }

}
