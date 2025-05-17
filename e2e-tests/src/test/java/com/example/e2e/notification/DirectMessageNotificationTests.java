package com.example.e2e.notification;

import com.example.e2e.base.BaseApiTest;
import com.example.e2e.service.*;
import io.restassured.specification.RequestSpecification;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

public class DirectMessageNotificationTests extends BaseApiTest {
    private UserTestService userTestService;
    private ChatTestService chatTestService;
    private MessageTestService messageTestService;
    private NotificationTestService notificationTestService;
    private MailhogService maildropService;

    private static final String MAILDROP_DOMAIN = "@maildrop.cc";
    private static final String MAILDROP_MAILBOX1 = "mathew1";
    private static final String MAILDROP_MAILBOX2 = "mathew2";
    private static final String MAILDROP_MAILBOX3 = "mathew3";
    private static final String MAILDROP_MAILBOX4 = "mathew4";

    private static final String EMAIL1 = MAILDROP_MAILBOX1 + MAILDROP_DOMAIN;
    private static final String EMAIL2 = MAILDROP_MAILBOX2 + MAILDROP_DOMAIN;
    private static final String EMAIL3 = MAILDROP_MAILBOX3 + MAILDROP_DOMAIN;
    private static final String EMAIL4 = MAILDROP_MAILBOX4 + MAILDROP_DOMAIN;

    private static final String[] MAILDROP_MAILBOXES = {
        MAILDROP_MAILBOX1, MAILDROP_MAILBOX2, MAILDROP_MAILBOX3, MAILDROP_MAILBOX4,
    };

    private String recipientId;
    private String recipientEmail;
    private String senderId;
    private String senderUsername;
    private String senderEmail;
    private String chatId;
    private String messageContent;
    private Map<String, Object> expectedNotification;

    @Override
    protected void setupServiceSpecificConfig() {
        RequestSpecification userServiceSpec = getSpecForService(USER_SERVICE_PORT);
        RequestSpecification chatServiceSpec = getSpecForService(CHAT_SERVICE_PORT);
        RequestSpecification notificationServiceSpec = getSpecForService(NOTIFICATION_SERVICE_PORT);

        userTestService = new UserTestService(userServiceSpec);
        chatTestService = new ChatTestService(chatServiceSpec, userTestService);
        messageTestService = new MessageTestService(chatServiceSpec);
        notificationTestService = new NotificationTestService(notificationServiceSpec);
        maildropService = new MailhogService();
    }

    @BeforeEach
    public void setUp() {
        // Create chat
        var chatTestResult = chatTestService.createRandomChat(EMAIL1, EMAIL2);

        var recipient = chatTestResult.user1();
        var sender = chatTestResult.user2();
        var chat = chatTestResult.chat();

        recipientId = recipient.get("id").toString();
        recipientEmail = recipient.get("email").toString();
        senderId = sender.get("id").toString();
        senderUsername = sender.get("username").toString();
        senderEmail = sender.get("email").toString();
        chatId = chat.get("chatId").toString();
        messageContent = "Hello! This is a test message.";

        expectedNotification = Map.of(
            "type", "direct_message",
            "senderUserId", senderId,
            "senderUsername", senderUsername,
            "recipientUserId", recipientId,
            "recipientEmail", recipientEmail,
            "messageText", messageContent,
            "chatId", chatId,
            "read", false
        );

        // By default mute email notifications
        notificationTestService.disableNotification(recipientId, "direct_message", "email");
        notificationTestService.disableNotification(senderId, "direct_message", "email");
    }

    @AfterEach
    public void cleanup() {
        userTestService.cleanup();
        notificationTestService.cleanup();
        maildropService.emptyMailbox();
    }

    @Test
    @DisplayName("When a user sends a direct message, and both inbox and email are enabled for direct messages, recipient should receive both notification types")
    public void shouldReceiveNotificationForNewDirectMessageWhenBothEnabled() {
        notificationTestService.enableNotification(recipientId, "direct_message", "inbox");
        notificationTestService.enableNotification(recipientId, "direct_message", "email");

        sendDirectMessage();

        assertNotificationReceivedInInbox(recipientId);
        assertNotificationReceivedInEmail(recipientEmail);
        assertNotificationNotReceivedInInbox(senderId);
        assertNotificationNotReceivedInEmail(senderEmail);
    }

    @Test
    @DisplayName("When a direct notification is read, the unread count changes")
    public void shouldChangeUnreadCountWhenDirectNotificationReceived() {
        var unreadCountBefore = notificationTestService.getUnreadNotificationCount(recipientId);

        sendDirectMessage();

        var unreadCount = notificationTestService.getUnreadNotificationCount(recipientId);

        assertThat(unreadCount).isEqualTo(unreadCountBefore + 1);
    }

    @Test
    @DisplayName("When a direct message is marked as read, the unread count shows as 0")
    public void shouldChangeUnreadCountWhenDirectNotificationMarkedAsRead() {
        sendDirectMessage();

        var unreadNotification = getNotificationFromUnread(recipientId);
        assertThat(unreadNotification).isNotNull();
        var notificationId = unreadNotification.get("id").toString();

        var unreadCountBefore = notificationTestService.getUnreadNotificationCount(recipientId);
        notificationTestService.markNotificationAsRead(notificationId);

        var unreadCountAfter = notificationTestService.getUnreadNotificationCount(recipientId);

        assertThat(unreadCountAfter).isEqualTo(unreadCountBefore - 1);
    }

    @Test
    @DisplayName("When a user sends a direct message, and only inbox is enabled but email is disabled, recipient should only receive inbox notification")
    public void shouldReceiveOnlyInboxNotificationWhenEmailDisabled() {
        // Enable inbox but disable email notifications
        notificationTestService.enableNotification(recipientId, "direct_message", "inbox");
        notificationTestService.disableNotification(recipientId, "direct_message", "email");

        sendDirectMessage();

        assertNotificationReceivedInInbox(recipientId);
        assertNotificationNotReceivedInEmail(recipientEmail);
        assertNotificationNotReceivedInInbox(senderId);
        assertNotificationNotReceivedInEmail(senderEmail);
    }

    @Test
    @DisplayName("When a user sends a direct message, and only email is enabled but inbox is disabled, recipient should only receive email notification")
    public void shouldReceiveOnlyEmailNotificationWhenInboxDisabled() {
        // Enable email but disable inbox notifications
        notificationTestService.disableNotification(recipientId, "direct_message", "inbox");
        notificationTestService.enableNotification(recipientId, "direct_message", "email");

        sendDirectMessage();

        assertNotificationNotReceivedInInbox(recipientId);
        assertNotificationReceivedInEmail(recipientEmail);
        assertNotificationNotReceivedInInbox(senderId);
        assertNotificationNotReceivedInEmail(senderEmail);
    }

    @Test
    @DisplayName("When a user sends a direct message, and both inbox and email are disabled for direct messages, recipient should receive no notifications")
    public void shouldReceiveNoNotificationsWhenBothDisabled() {
        // Disable both inbox and email notifications
        notificationTestService.disableNotification(recipientId, "direct_message", "inbox");
        notificationTestService.disableNotification(recipientId, "direct_message", "email");

        sendDirectMessage();

        assertNotificationNotReceivedInInbox(recipientId);
        assertNotificationNotReceivedInEmail(recipientEmail);
        assertNotificationNotReceivedInInbox(senderId);
        assertNotificationNotReceivedInEmail(senderEmail);
    }

    @Test
    @DisplayName("When all notifications are muted, recipient should receive no notifications even if individual settings are enabled")
    public void shouldReceiveNoNotificationsWhenMuted() {
        // Enable notifications but then mute all
        notificationTestService.enableNotification(recipientId, "direct_message", "inbox");
        notificationTestService.enableNotification(recipientId, "direct_message", "email");
        notificationTestService.muteAllNotifications(recipientId);

        sendDirectMessage();

        waitFor(500);

        assertNotificationNotReceivedInInbox(recipientId);
        assertNotificationNotReceivedInEmail(recipientEmail);
        assertNotificationNotReceivedInInbox(senderId);
        assertNotificationNotReceivedInEmail(senderEmail);
    }

    @Test
    @DisplayName("When notifications are unmuted after being muted, recipient should receive notifications again")
    public void shouldReceiveNotificationsAfterUnmuting() {
        // First mute all notifications
        notificationTestService.enableNotification(recipientId, "direct_message", "inbox");
        notificationTestService.enableNotification(recipientId, "direct_message", "email");
        notificationTestService.muteAllNotifications(recipientId);

        sendDirectMessage();

        assertNotificationNotReceivedInInbox(recipientId);
        assertNotificationNotReceivedInEmail(recipientEmail);
        assertNotificationNotReceivedInInbox(senderId);
        assertNotificationNotReceivedInEmail(senderEmail);

        // Now unmute all notifications
        notificationTestService.unmuteAllNotifications(recipientId);

        sendDirectMessage();

        assertNotificationReceivedInInbox(recipientId);
        assertNotificationReceivedInEmail(recipientEmail);
    }

    private Map<String, Object> sendDirectMessage() {
        var message = messageTestService.sendDirectMessage(
            senderId,
            senderUsername,
            recipientId,
            messageContent
        );

        waitFor(500);

        return message;
    }

    private void assertNotificationReceivedInInbox(String userId) {
        // Wait for notification to arrive
        var unreadNotification = getNotificationFromUnread(userId);
        var notificationInAllNotifications = getNotificationFromAllNotifications(userId);

        // Assert notification fields
        assertNotificationFields(unreadNotification);
        assertNotificationFields(notificationInAllNotifications);
    }

    private void assertNotificationReceivedInEmail(String email) {
        var foundMail = maildropService.getMatchingMail(
            email,
            "New direct message from " + senderUsername,
            messageContent
        );

        assertThat(foundMail).isNotNull();
    }

    private void assertNotificationNotReceivedInInbox(String userId) {
        try {
            // Wait for notification to arrive
            var unreadNotification = getNotificationFromUnread(userId);
            var notificationInAllNotifications = getNotificationFromAllNotifications(userId);

            assertThat(unreadNotification).isNull();
            assertThat(notificationInAllNotifications).isNull();
        } catch (Exception e) {
            // Ignore exception, as we expect no notification to be received
        }
    }

    private void assertNotificationNotReceivedInEmail(String email) {
        var foundMail = maildropService.getMatchingMail(
            email,
            "New direct message from " + senderUsername,
            messageContent
        );

        assertThat(foundMail).isNull();
    }

    private void assertNotificationFields(
        Map<String, Object> actualNotification
    ) {
        assertThat(actualNotification).containsKeys("id", "timestamp", "messageId");
        assertThat(actualNotification).containsAllEntriesOf(expectedNotification);
    }

    private void waitFor(long ms) {
        try {
            Thread.sleep(ms);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    private Map<String, Object> getNotificationFromAllNotifications(String userId) {
        var notifications = notificationTestService.getAllNotifications(userId);
        if (notifications.isEmpty()) {
            return null;
        }

        if (notifications.size() > 1) {
            throw new RuntimeException("Multiple notifications found");
        }

        return notifications.getFirst();

    }

    private Map<String, Object> getNotificationFromUnread(String userId) {
        var notifications =
            notificationTestService.getUnreadNotifications(userId);

        if (notifications.isEmpty()) {
            return null;
        }

        if (notifications.size() > 1) {
            throw new RuntimeException("Multiple notifications found");
        }

        return notifications.getFirst();
    }
}
