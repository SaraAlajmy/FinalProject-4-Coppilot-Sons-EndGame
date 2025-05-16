package com.example.groupChatService.services;

import com.example.groupChatService.models.GroupChat;
import lombok.RequiredArgsConstructor;
import org.example.shared.dto.GroupMentionNotificationDTO;
import org.example.shared.dto.GroupMessageNotificationDTO;
import org.example.shared.dto.NotificationDTO;
import org.example.shared.producer.NotificationProducer;
import org.springframework.stereotype.Component;

import com.example.groupChatService.models.GroupMessage;

@Component
@RequiredArgsConstructor
public class NotificationListener implements MessageListener {
    private final NotificationProducer notificationProducer;

    @Override
    public void onNewMessage(GroupMessage message, GroupChat groupChat, String senderUsername) {
        groupChat.getMembers().forEach(memberId -> {
            if (!memberId.equals(message.getSenderId())) {
                NotificationDTO notificationDTO = notificationDtoFromGroupMessage(
                    message,
                    groupChat,
                    senderUsername,
                    memberId
                );
                notificationProducer.sendMessage(notificationDTO);
            }
        });
    }

    private NotificationDTO notificationDtoFromGroupMessage(
        GroupMessage groupMessage,
        GroupChat groupChat,
        String senderUsername,
        String receiverId
    ) {
        if (groupMessage.getMentionedUserIds().contains(receiverId)) {
            return new GroupMentionNotificationDTO(
                receiverId,
                groupMessage.getSenderId(),
                senderUsername,
                groupMessage.getId(),
                groupMessage.getContent(),
                groupMessage.getCreatedAt(),
                groupChat.getId(),
                groupChat.getName(),
                groupChat.getEmoji()
            );
        }

        return new GroupMessageNotificationDTO(
            receiverId,
            groupMessage.getSenderId(),
            senderUsername,
            groupMessage.getId(),
            groupMessage.getContent(),
            groupMessage.getCreatedAt(),
            groupChat.getId(),
            groupChat.getName(),
            groupChat.getEmoji()
        );

    }

}
