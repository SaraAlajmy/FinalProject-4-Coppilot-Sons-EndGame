package com.example.groupChatService.services;

import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import com.example.groupChatService.dto.GroupNotificationDTO;
import com.example.groupChatService.models.GroupChat;
import com.example.groupChatService.models.GroupMessage;
import com.example.groupChatService.rabbitmq.NotificationProducer;
import com.example.groupChatService.repositories.GroupChatRepo;

@Component
public class NotificationListener implements MessageListener {

    private final GroupChatRepo groupChatRepo;
    private final NotificationProducer notificationProducer;

    @Autowired
    public NotificationListener(GroupChatRepo groupChatRepo, NotificationProducer notificationProducer) {
        this.notificationProducer = notificationProducer;
        this.groupChatRepo = groupChatRepo;
    }


    @Override
    public void onNewMessage(GroupMessage message, GroupChat groupChat, String senderUsername) {
        // get all users other than the sender of the message
        GroupChat group = groupChatRepo.findById(message.getGroupId()).orElseThrow(() -> new RuntimeException("Group not found with id:" + message.getGroupId()));

        List<String> recipientsUserIds = new ArrayList<>();
        for (String memberId : group.getMembers()) {
            if (!memberId.equals(message.getSenderId())) {
                recipientsUserIds.add(memberId);
            }
        }
        List<String> mentionedUserIds = message.getMentionedUserIds();

        GroupNotificationDTO notification = new GroupNotificationDTO();
        notification.setType("group_mention");
        notification.setSenderUserId(senderUsername);
        notification.setMessageId(message.getId());
        notification.setMessageText(message.getContent());
        notification.setMessageTimestamp(message.getCreatedAt());
        notification.setGroupId(message.getGroupId());
        notification.setGroupName(group.getName());
        notification.setGroupIcon(group.getEmoji());

        for (String recipientUserId : recipientsUserIds) {
            if (mentionedUserIds.contains(recipientUserId)) {
                recipientsUserIds.remove(recipientUserId);
                notification.setRecipientUserId(recipientUserId);
                // send notification to mentioned users
                try {
                    notificationProducer.sendMessage(notification);
                } catch (Exception e) {
                    throw new RuntimeException("Error sending notification: " + e.getMessage());
                }
            }
        }

        notification.setType("group_message");
        for(String recipientUserId : recipientsUserIds) {
             notification.setRecipientUserId(recipientUserId);
            // send notification to other group members
            try {
                notificationProducer.sendMessage(notification);
            } catch (Exception e) {
                throw new RuntimeException("Error sending notification: " + e.getMessage());
            }
        }

    }

}
