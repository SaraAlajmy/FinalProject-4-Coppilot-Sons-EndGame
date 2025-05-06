package org.example.notificationservice.services;

import lombok.extern.slf4j.Slf4j;
import org.example.notificationservice.factories.UserDTOFactory;
import org.example.notificationservice.models.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@Slf4j
public class NotificationService {
    private final NotificationDeliveryService notificationDeliveryService;
    private final UserDTOFactory userDTOFactory;
    private final NotificationSettingsService notificationSettingsService;

    @Autowired
    public NotificationService(
        NotificationDeliveryService notificationDeliveryService,
        UserDTOFactory userDTOFactory,
        NotificationSettingsService notificationSettingsService
    ) {
        this.notificationDeliveryService = notificationDeliveryService;
        this.userDTOFactory = userDTOFactory;
        this.notificationSettingsService = notificationSettingsService;
    }

    //TODO: Change to listen to the notification queue
    public boolean createNotification(Notification notification) {

        //TODO: GET the user Info from the UserClient when user-service is ready
        UserDTO receiver = userDTOFactory.createUserDTO(notification.getRecipientUserId());
        notification.setRecipientEmail(receiver.getEmail());
        notification.setTimestamp(LocalDateTime.now());

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
