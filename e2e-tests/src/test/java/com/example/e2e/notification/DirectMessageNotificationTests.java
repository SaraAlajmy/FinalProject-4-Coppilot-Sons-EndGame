package com.example.e2e.notification;

import com.example.e2e.base.BaseApiTest;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

public class DirectMessageNotificationTests extends BaseApiTest {
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

    private Map<String, Object> sender;
    private Map<String, Object> recipient;

    private String recipientId;
    private String recipientEmail;
    private String senderId;
    private String senderUsername;
    private String senderEmail;
    private String chatId;
    private String messageContent;
    private Map<String, Object> expectedNotification;

    @BeforeEach
    public void setUp() {
        // Create chat
        var chatTestResult = chatTestService.createRandomChat(EMAIL1, EMAIL2);

        recipient = chatTestResult.user1();
        sender = chatTestResult.user2();
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

        loginAs(recipient);

        // By default mute email notifications
        notificationTestService.disableNotification("direct_message", "email");
        notificationTestService.disableNotification("direct_message", "email");
    }

    @AfterEach
    public void cleanup() {
//        userTestService.cleanup();
        notificationTestService.cleanup();
        mailhogService.emptyMailbox();
    }

    @Test
    @DisplayName("When a user sends a direct message, and both inbox and email are enabled for direct messages, recipient should receive both notification types")
    public void shouldReceiveNotificationForNewDirectMessageWhenBothEnabled() {
        // Enable inbox and email notifications
        notificationTestService.enableNotification("direct_message", "inbox");
        notificationTestService.enableNotification("direct_message", "email");

        sendDirectMessage();

        assertNotificationReceivedInInbox(recipient);
        assertNotificationReceivedInEmail(recipientEmail);
        assertNotificationNotReceivedInInbox(sender);
        assertNotificationNotReceivedInEmail(senderEmail);
    }

    @Test
    @DisplayName("When a direct notification is read, the unread count changes")
    public void shouldChangeUnreadCountWhenDirectNotificationReceived() {
        var unreadCountBefore = notificationTestService.getUnreadNotificationCount();

        sendDirectMessage();

        var unreadCount = notificationTestService.getUnreadNotificationCount();

        assertThat(unreadCount).isEqualTo(unreadCountBefore + 1);
    }

    @Test
    @DisplayName("When a direct message is marked as read, the unread count shows as 0")
    public void shouldChangeUnreadCountWhenDirectNotificationMarkedAsRead() {
        sendDirectMessage();

        var unreadNotification = getNotificationFromUnread();
        assertThat(unreadNotification).isNotNull();
        var notificationId = unreadNotification.get("id").toString();

        var unreadCountBefore = notificationTestService.getUnreadNotificationCount();
        notificationTestService.markNotificationAsRead(notificationId);

        var unreadCountAfter = notificationTestService.getUnreadNotificationCount();

        assertThat(unreadCountAfter).isEqualTo(unreadCountBefore - 1);
    }

    @Test
    @DisplayName("When a user sends a direct message, and only inbox is enabled but email is disabled, recipient should only receive inbox notification")
    public void shouldReceiveOnlyInboxNotificationWhenEmailDisabled() {
        // Enable inbox but disable email notifications
        notificationTestService.enableNotification("direct_message", "inbox");
        notificationTestService.disableNotification("direct_message", "email");

        sendDirectMessage();

        assertNotificationReceivedInInbox(recipient);
        assertNotificationNotReceivedInEmail(recipientEmail);
        assertNotificationNotReceivedInInbox(sender);
        assertNotificationNotReceivedInEmail(senderEmail);
    }

    @Test
    @DisplayName("When a user sends a direct message, and only email is enabled but inbox is disabled, recipient should only receive email notification")
    public void shouldReceiveOnlyEmailNotificationWhenInboxDisabled() {
        // Enable email but disable inbox notifications
        notificationTestService.disableNotification("direct_message", "inbox");
        notificationTestService.enableNotification("direct_message", "email");

        sendDirectMessage();

        assertNotificationNotReceivedInInbox(recipient);
        assertNotificationReceivedInEmail(recipientEmail);
        assertNotificationNotReceivedInInbox(sender);
        assertNotificationNotReceivedInEmail(senderEmail);
    }

    @Test
    @DisplayName("When a user sends a direct message, and both inbox and email are disabled for direct messages, recipient should receive no notifications")
    public void shouldReceiveNoNotificationsWhenBothDisabled() {
        // Disable both inbox and email notifications
        notificationTestService.disableNotification("direct_message", "inbox");
        notificationTestService.disableNotification("direct_message", "email");

        sendDirectMessage();

        assertNotificationNotReceivedInInbox(recipient);
        assertNotificationNotReceivedInEmail(recipientEmail);
        assertNotificationNotReceivedInInbox(sender);
        assertNotificationNotReceivedInEmail(senderEmail);
    }

    @Test
    @DisplayName("When all notifications are muted, recipient should receive no notifications even if individual settings are enabled")
    public void shouldReceiveNoNotificationsWhenMuted() {
        // Enable notifications but then mute all
        notificationTestService.enableNotification("direct_message", "inbox");
        notificationTestService.enableNotification("direct_message", "email");
        notificationTestService.muteAllNotifications();

        sendDirectMessage();

        waitFor(500);

        assertNotificationNotReceivedInInbox(recipient);
        assertNotificationNotReceivedInEmail(recipientEmail);
        assertNotificationNotReceivedInInbox(sender);
        assertNotificationNotReceivedInEmail(senderEmail);
    }

    @Test
    @DisplayName("When notifications are unmuted after being muted, recipient should receive notifications again")
    public void shouldReceiveNotificationsAfterUnmuting() {
        // First mute all notifications
        notificationTestService.enableNotification("direct_message", "inbox");
        notificationTestService.enableNotification("direct_message", "email");
        notificationTestService.muteAllNotifications();

        sendDirectMessage();

        assertNotificationNotReceivedInInbox(recipient);
        assertNotificationNotReceivedInEmail(recipientEmail);
        assertNotificationNotReceivedInInbox(sender);
        assertNotificationNotReceivedInEmail(senderEmail);

        // Now unmute all notifications
        notificationTestService.unmuteAllNotifications();

        sendDirectMessage();

        assertNotificationReceivedInInbox(recipient);
        assertNotificationReceivedInEmail(recipientEmail);
    }

    @Test
    @DisplayName("When 3 direct messages are sent to the same recipient, they should be received as separate notifications")
    public void shouldReceiveSeparateNotificationsForMultipleMessages() {
        // Enable inbox and email notifications
        notificationTestService.enableNotification("direct_message", "inbox");
        notificationTestService.enableNotification("direct_message", "email");

        for (int i = 0; i < 3; i++) {
            sendDirectMessage();
        }

        var unreadNotifications = notificationTestService.getUnreadNotifications();
        assertThat(unreadNotifications).hasSize(3);
        for (var notification : unreadNotifications) {
            assertNotificationFields(notification);
        }

        var emailNotifications = mailhogService.getMatchingMails(
            recipientEmail,
            "New direct message from " + senderUsername,
            messageContent
        );

        assertThat(emailNotifications).hasSize(3);
    }

    @Test
    @DisplayName("When multiple users send direct messages, get unread notifications grouped by sender should work")
    public void shouldGroupUnreadNotificationsBySender() {
        var sender2 = userTestService.registerUser();
        var sender3 = userTestService.registerUser();

        var senders = List.of(sender, sender2, sender3);
        var messages = Map.of(
            sender.get("id").toString(), sendDirectMessage(sender),
            sender2.get("id").toString(), sendDirectMessage(sender2),
            sender3.get("id").toString(), sendDirectMessage(sender3)
        );

        sendDirectMessage(sender);
        sendDirectMessage(sender2);
        sendDirectMessage(sender3);

        // Send another message from the first sender
        sendDirectMessage(sender);

        var unreadNotificationsCount = notificationTestService.getUnreadNotificationCount();
        assertThat(unreadNotificationsCount).isEqualTo(7);

        var unreadNotifications = notificationTestService.getUnreadNotificationsGroupedBySender();
        assertThat(unreadNotifications).hasSize(3);
        assertThat(unreadNotifications.get(senderId)).hasSize(3);
        assertThat(unreadNotifications.get(sender2.get("id").toString())).hasSize(2);
        assertThat(unreadNotifications.get(sender3.get("id").toString())).hasSize(2);

        // Assert notification contents for each sender
        for (var sender : senders) {
            var senderId = sender.get("id").toString();
            var notifications = unreadNotifications.get(senderId);
            var expected = new HashMap<>(expectedNotification);
            expected.put("chatId", messages.get(senderId).get("chatId"));
            expected.put("senderUserId", senderId);
            expected.put("senderUsername", sender.get("username"));


            assertThat(notifications).allSatisfy(notification -> {
                assertThat(notification).containsAllEntriesOf(expected);
                assertThat(notification).containsKeys("id", "timestamp", "messageId");
            });
        }

        // Read one notification from the first sender and one from the third sender
        var notificationId1 = unreadNotifications.get(senderId).getFirst().get("id").toString();
        var notificationId2 = unreadNotifications.get(sender3.get("id").toString()).getFirst().get("id").toString();

        notificationTestService.markNotificationAsRead(notificationId1);
        notificationTestService.markNotificationAsRead(notificationId2);

        // Assert that the unread count is updated
        var unreadCountAfter = notificationTestService.getUnreadNotificationCount();
        assertThat(unreadCountAfter).isEqualTo(unreadNotificationsCount - 2);

        // Assert that the notifications are marked as read
        var unreadNotificationsAfterMarking2 = notificationTestService.getUnreadNotifications();
        assertThat(unreadNotificationsAfterMarking2).hasSize(unreadNotificationsCount - 2);

        // Assert that the notifications are removed from grouped notifications
        var unreadNotificationsAfterMarking = notificationTestService.getUnreadNotificationsGroupedBySender();
        assertThat(unreadNotificationsAfterMarking.get(senderId)).hasSize(2);
        assertThat(unreadNotificationsAfterMarking.get(sender3.get("id").toString())).hasSize(1);
        assertThat(unreadNotificationsAfterMarking.get(sender2.get("id").toString())).hasSize(2);

        // Assert that the notifications are not removed from the original list
        var allNotificationAfterMarking2 = notificationTestService.getAllNotifications();
        assertThat(allNotificationAfterMarking2).hasSize(7);

        var readNotificationsIds = List.of(notificationId1, notificationId2);

        // Assert that notification has read equal to true
        for (var notification : allNotificationAfterMarking2) {
            if (readNotificationsIds.contains(notification.get("id").toString())) {
                assertThat(notification).containsEntry("read", true);
            } else {
                assertThat(notification).containsEntry("read", false);
            }
        }

        // Mark all notifications as read
        notificationTestService.markAllNotificationsAsRead();

        // Assert that all notifications are marked as read
        var unreadNotificationsAfterMarkingAll = notificationTestService.getUnreadNotifications();
        assertThat(unreadNotificationsAfterMarkingAll).isEmpty();

        var unreadNotificationCountAfterMarkingAll = notificationTestService.getUnreadNotificationCount();
        assertThat(unreadNotificationCountAfterMarkingAll).isEqualTo(0);

        // Assert that all notifications are marked as read
        var allNotificationsAfterMarkingAll = notificationTestService.getAllNotifications();
        assertThat(allNotificationsAfterMarkingAll).hasSize(7);
        for (var notification : allNotificationsAfterMarkingAll) {
            assertThat(notification).containsEntry("read", true);
        }

        // Assert that the notifications are removed from grouped notifications
        var unreadGroupedNotificationsAfterMarkingAll = notificationTestService.getUnreadNotificationsGroupedBySender();
        assertThat(unreadGroupedNotificationsAfterMarkingAll).isEmpty();
    }

    @Test
    @DisplayName("When a user sends a direct message and then we update the notification it, it should be reflected in the notification")
    public void shouldReflectUpdatedMessageInNotification() {
        sendDirectMessage();

        // Notification should be received
        var unreadNotification = getNotificationFromUnread();
        assertThat(unreadNotification).isNotNull();

        // Update notification
        var notificationId = unreadNotification.get("id").toString();
        var newNotification = new HashMap<>(Map.copyOf(unreadNotification));
        newNotification.put("messageText", "Updated message content");
        newNotification.put("recipientEmail", "updated_" + recipientEmail);

        notificationTestService.updateNotification(notificationId, newNotification);

        // Assert that the notification is updated
        var updatedNotification = getNotificationFromUnread();
        assertThat(updatedNotification).isNotNull();
        assertThat(updatedNotification).containsAllEntriesOf(newNotification);
    }

    @Test
    @DisplayName("When a user sends a direct message, and the recipient deletes it from their inbox, it should be removed from the inbox")
    public void shouldRemoveNotificationFromInboxWhenDeleted() {
        sendDirectMessage();

        // Notification should be received
        var unreadNotification = getNotificationFromUnread();
        assertThat(unreadNotification).isNotNull();

        // Delete notification
        var notificationId = unreadNotification.get("id").toString();
        notificationTestService.deleteNotification(notificationId);

        // Assert that the notification is deleted
        var deletedNotification = getNotificationFromUnread();
        assertThat(deletedNotification).isNull();
    }

    private Map<String, Object> sendDirectMessage() {
        return sendDirectMessage(sender);
    }


    private Map<String, Object> sendDirectMessage(Map<String, Object> sender) {
        Map<String, Object>[] messages = new Map[1];

        loggedAs(
            sender, () -> {
                messages[0] = messageTestService.sendDirectMessage(
                    recipientId,
                    messageContent
                );
            }
        );

        waitFor(500);

        return messages[0];
    }

    private void assertNotificationReceivedInInbox(Map<String, Object> user) {
        loggedAs(
            user, () -> {
                // Wait for notification to arrive
                var unreadNotification = getNotificationFromUnread();
                var notificationInAllNotifications = getNotificationFromAllNotifications();

                // Assert notification fields
                assertNotificationFields(unreadNotification);
                assertNotificationFields(notificationInAllNotifications);
            }
        );
    }

    private void assertNotificationReceivedInEmail(String email) {
        var foundMail = mailhogService.getMatchingMail(
            email,
            "New direct message from " + senderUsername,
            messageContent
        );

        assertThat(foundMail).isNotNull();
    }

    private void assertNotificationNotReceivedInInbox(Map<String, Object> user) {
        loggedAs(
            user, () -> {
                try {
                    // Wait for notification to arrive
                    var unreadNotification = getNotificationFromUnread();
                    var notificationInAllNotifications =
                        getNotificationFromAllNotifications();

                    assertThat(unreadNotification).isNull();
                    assertThat(notificationInAllNotifications).isNull();
                } catch (Exception e) {
                    // Ignore exception, as we expect no notification to be received
                }
            }
        );
    }

    private void assertNotificationNotReceivedInEmail(String email) {
        var foundMail = mailhogService.getMatchingMail(
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

    private Map<String, Object> getNotificationFromAllNotifications() {
        var notifications = notificationTestService.getAllNotifications();
        if (notifications.isEmpty()) {
            return null;
        }

        if (notifications.size() > 1) {
            throw new RuntimeException("Multiple notifications found");
        }

        return notifications.getFirst();

    }

    private Map<String, Object> getNotificationFromUnread() {
        var notifications =
            notificationTestService.getUnreadNotifications();

        if (notifications.isEmpty()) {
            return null;
        }

        if (notifications.size() > 1) {
            throw new RuntimeException("Multiple notifications found");
        }

        return notifications.getFirst();
    }
}
