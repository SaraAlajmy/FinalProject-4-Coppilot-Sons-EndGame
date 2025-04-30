package org.example.notificationservice.services;

import lombok.extern.slf4j.Slf4j;
import org.example.notificationservice.factories.NotificationSettingsDTOFactory;
import org.example.notificationservice.factories.UserDTOFactory;
import org.example.notificationservice.models.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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

        return switch (notification.getType()) {
            case NotificationType.DIRECT_MESSAGE ->
                    handleMessageNotification((DirectMessageNotification) notification, userNotificationSetting);
            case NotificationType.GROUP_MESSAGE ->
                    handleGroupMessageNotification((GroupMessageNotification) notification, userNotificationSetting);
            case NotificationType.GROUP_MENTION ->
                    handleGroupMentionNotification((GroupMentionNotification) notification, userNotificationSetting);
            default -> false;
        };
    }
    //TODO: Change to use the UserClient when user-service is ready
    private String getSenderName (String senderId){
        return userDTOFactory.createUserDTO(senderId).getName();

    }
    private boolean handleMessageNotification(DirectMessageNotification directMessageNotification, NotificationSettingsDTO userNotificationSetting) {
        boolean success=true;
        directMessageNotification.setSenderName(getSenderName(directMessageNotification.getSenderUserId()));
        if (userNotificationSetting.getDirectMessageEmail()) {
            if(!notificationDeliveryService.deliverNotificationUsingStrategy(
                    directMessageNotification, NotificationStrategyType.EMAIL)) {
                success = false;
                log.error("Failed to deliver Direct Message Notification by Email for recipient {}", directMessageNotification.getRecipientUserId());
            }
        }
        if (userNotificationSetting.getDirectMessageInbox()) {
            if(!notificationDeliveryService.deliverNotificationUsingStrategy(
                    directMessageNotification, NotificationStrategyType.INBOX)) {
                success = false;
                log.error("Failed to deliver Direct Message Notification in Inbox for recipient {}", directMessageNotification.getRecipientUserId());

            }
        }
        return success;
    }

    private boolean handleGroupMessageNotification(GroupMessageNotification groupMessageNotification, NotificationSettingsDTO userNotificationSetting) {
        boolean success=true;
        groupMessageNotification.setSenderName(getSenderName(groupMessageNotification.getSenderUserId()));
        if (userNotificationSetting.getGroupMessageEmail()) {
           if(!notificationDeliveryService.deliverNotificationUsingStrategy(
                    groupMessageNotification, NotificationStrategyType.EMAIL)) {
                success = false;
               log.error("Failed to deliver Group Message Notification by Email for recipient {}", groupMessageNotification.getRecipientUserId());

           }
        }
        if (userNotificationSetting.getGroupMessageInbox()) {
            if(!notificationDeliveryService.deliverNotificationUsingStrategy(
                    groupMessageNotification, NotificationStrategyType.INBOX)){
                success = false;
                log.error("Failed to deliver Group Message Notification in Inbox for recipient {}", groupMessageNotification.getRecipientUserId());
            }
        }
        return success;
    }

    private boolean handleGroupMentionNotification(GroupMentionNotification groupMentionNotification, NotificationSettingsDTO userNotificationSetting) {
        boolean success=true;
        groupMentionNotification.setSenderName(getSenderName(groupMentionNotification.getSenderUserId()));
        if (userNotificationSetting.getGroupMentionEmail()) {
            if(!notificationDeliveryService.deliverNotificationUsingStrategy(
                    groupMentionNotification, NotificationStrategyType.EMAIL)) {
                success = false;
                log.error("Failed to deliver Group Mention Notification by Email for recipient {}", groupMentionNotification.getRecipientUserId());
            }
        }
        if (userNotificationSetting.getGroupMentionInbox()) {
            if(!notificationDeliveryService.deliverNotificationUsingStrategy(
                    groupMentionNotification, NotificationStrategyType.INBOX)){
                success = false;
                log.error("Failed to deliver Group Mention Notification in Inbox for recipient {}", groupMentionNotification.getRecipientUserId());

            }
        }
        return success;
    }
}