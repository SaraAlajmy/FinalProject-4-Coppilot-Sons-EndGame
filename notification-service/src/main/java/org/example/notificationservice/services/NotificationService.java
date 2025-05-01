package org.example.notificationservice.services;

import lombok.extern.slf4j.Slf4j;
import org.example.notificationservice.factories.NotificationSettingsDTOFactory;
import org.example.notificationservice.factories.UserDTOFactory;
import org.example.notificationservice.models.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;

@Service
@Slf4j
public class NotificationService {
    private final NotificationDeliveryService notificationDeliveryService;
    private final NotificationSettingsDTOFactory notificationSettingsDTOFactory;
    private final UserDTOFactory userDTOFactory;

    @Autowired
    public NotificationService(NotificationDeliveryService notificationDeliveryService,
                               NotificationSettingsDTOFactory notificationSettingsDTOFactory,
                               UserDTOFactory userDTOFactory) {
        this.notificationDeliveryService = notificationDeliveryService;
        this.notificationSettingsDTOFactory = notificationSettingsDTOFactory;
        this.userDTOFactory = userDTOFactory;
    }

    //TODO: Change to listen to the notification queue
    public boolean createNotification(Notification notification) {
        NotificationSettingsDTO userNotificationSetting = notificationSettingsDTOFactory.createNotificationSettingsDTO();
        if (userNotificationSetting.getMuteNotifications()) {
            return true;
        }
        //TODO: GET the user Info from the UserClient when user-service is ready
        UserDTO receiver = userDTOFactory.createUserDTO(notification.getRecipientUserId());
        notification.setRecipientEmail(receiver.getEmail());
        if (notification instanceof MessageNotification) {
            String senderName = getSenderName(((MessageNotification) notification).getSenderUserId());
            ((MessageNotification) notification).setSenderName(senderName);
        }
        notification.setTimestamp(LocalDateTime.now());


        return switch (notification.getType()) {
            case NotificationType.DIRECT_MESSAGE ->
                    sendNotification( notification,userNotificationSetting.getDirectMessageEmail(),
                            userNotificationSetting.getDirectMessageInbox());
            case NotificationType.GROUP_MESSAGE ->
                    sendNotification( notification, userNotificationSetting.getGroupMessageEmail(),
                            userNotificationSetting.getGroupMessageInbox());
            case NotificationType.GROUP_MENTION ->
                    sendNotification(notification, userNotificationSetting.getGroupMentionEmail(),
                            userNotificationSetting.getGroupMentionInbox());
            default -> false;
        };
    }
    //TODO: Change to use the UserClient when user-service is ready
    private String getSenderName (String senderId){
        return userDTOFactory.createUserDTO(senderId).getName();

    }
    private boolean sendNotification(Notification notification, boolean shouldSendByEmail, boolean shouldSendToInbox) {
        boolean success=true;
        if (shouldSendByEmail) {
            if(!notificationDeliveryService.deliverNotificationUsingStrategy(
                    notification, NotificationStrategyType.EMAIL)) {
                success = false;
                log.error("Failed to deliver Notification by Email for recipient {}", notification.getRecipientUserId());
            }
        }
        if (shouldSendToInbox) {
            if(!notificationDeliveryService.deliverNotificationUsingStrategy(
                    notification, NotificationStrategyType.INBOX)) {
                success = false;
                log.error("Failed to deliver Notification in Inbox for recipient {}", notification.getRecipientUserId());

            }
        }
        return success;
    }
}