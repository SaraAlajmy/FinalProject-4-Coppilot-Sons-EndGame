package org.example.notificationservice.factories;

import com.github.javafaker.Faker;
import org.example.notificationservice.models.*;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Component
public class NotificationDataFactory {
    private final Faker faker = new Faker();

    /**
     * Creates a list of random notifications, with a mix of types.
     */
    public List<Notification> createRandomNotifications(int count) {
        List<Notification> notifications = new ArrayList<>();

        for (int i = 0; i < count; i++) {
            notifications.add(createRandomNotification());
        }

        return notifications;
    }

    public Notification createRandomNotification() {
        int type = faker.number().numberBetween(0, 3);

        return switch (type) {
            case 0 -> createDirectMessageNotification();
            case 1 -> createGroupMessageNotification();
            case 2 -> createGroupMentionNotification();
            default -> createDirectMessageNotification();
        };
    }

    /**
     * Creates a DirectMessageNotification with random data
     */
    public DirectMessageNotification createDirectMessageNotification() {
        String recipientId = "user_" + faker.number().randomNumber(5, false);
        String senderId = "user_" + faker.number().randomNumber(5, false);

        return DirectMessageNotification.builder()
                                        .recipientUserId(recipientId)
                                        .timestamp(randomDateTime())
                                        .isRead(faker.bool().bool())
                                        .type(NotificationType.DIRECT_MESSAGE)
                                        .senderUserId(senderId)
                                        .senderName(faker.name().fullName())
                                        .messageId(
                                            "msg_" + UUID.randomUUID().toString().substring(0, 8))
                                        .messageText(faker.lorem().paragraph())
                                        .messageTimestamp(randomDateTime())
                                        .chatId("chat_" + faker.number().randomNumber(4, false))
                                        .build();
    }

    /**
     * Creates a GroupMessageNotification with random data
     */
    public GroupMessageNotification createGroupMessageNotification() {
        String recipientId = "user_" + faker.number().randomNumber(5, false);
        String senderId = "user_" + faker.number().randomNumber(5, false);
        String groupId = "group_" + faker.number().randomNumber(4, false);

        return GroupMessageNotification.builder()
                                       .recipientUserId(recipientId)
                                       .timestamp(randomDateTime())
                                       .isRead(faker.bool().bool())
                                       .type(NotificationType.GROUP_MESSAGE)
                                       .senderUserId(senderId)
                                       .senderName(faker.name().fullName())
                                       .messageId(
                                           "msg_" + UUID.randomUUID().toString().substring(0, 8))
                                       .messageText(faker.lorem().paragraph())
                                       .messageTimestamp(randomDateTime())
                                       .groupId(groupId)
                                       .groupName(faker.company().name() + " Team")
                                       .groupIcon(faker.internet().avatar())
                                       .build();
    }

    /**
     * Creates a GroupMentionNotification with random data
     */
    public GroupMentionNotification createGroupMentionNotification() {
        String recipientId = "user_" + faker.number().randomNumber(5, false);
        String senderId = "user_" + faker.number().randomNumber(5, false);
        String groupId = "group_" + faker.number().randomNumber(4, false);

        return GroupMentionNotification.builder()
                                       .recipientUserId(recipientId)
                                       .timestamp(randomDateTime())
                                       .isRead(faker.bool().bool())
                                       .type(NotificationType.GROUP_MENTION)
                                       .senderUserId(senderId)
                                       .senderName(faker.name().fullName())
                                       .messageId(
                                           "msg_" + UUID.randomUUID().toString().substring(0, 8))
                                       .messageText(
                                           "@user" + recipientId + " " + faker.lorem().paragraph())
                                       .messageTimestamp(randomDateTime())
                                       .groupId(groupId)
                                       .groupName(faker.company().name() + " Team")
                                       .groupIcon(faker.internet().avatar())
                                       .build();
    }

    /**
     * Creates a random date-time within the past 30 days
     */
    public LocalDateTime randomDateTime() {
        return LocalDateTime.ofInstant(
            faker.date().past(30, TimeUnit.DAYS).toInstant(),
            ZoneId.systemDefault()
        );
    }
}
