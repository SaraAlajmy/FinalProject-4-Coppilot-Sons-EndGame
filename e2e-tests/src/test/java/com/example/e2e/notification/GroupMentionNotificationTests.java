package com.example.e2e.notification;


import com.example.e2e.base.BaseApiTest;
import com.example.e2e.service.*;
import io.restassured.specification.RequestSpecification;
import lombok.Builder;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@Slf4j
public class GroupMentionNotificationTests extends BaseApiTest {
    private UserTestService userTestService;
    private NotificationTestService notificationTestService;
    private MailhogService mailhogService;
    private GroupChatTestService groupChatTestService;
    private GroupMessageTestService groupMessageTestService;

    private static final String MAILDROP_DOMAIN = "@maildrop.cc";
    private static final String MAILDROP_MAILBOX1 = "mathew1";
    private static final String MAILDROP_MAILBOX2 = "mathew2";
    private static final String MAILDROP_MAILBOX3 = "mathew3";
    private static final String MAILDROP_MAILBOX4 = "mathew4";
    private static final String MAILDROP_MAILBOX5 = "mathew5";

    private static final String EMAIL1 = MAILDROP_MAILBOX1 + MAILDROP_DOMAIN;
    private static final String EMAIL2 = MAILDROP_MAILBOX2 + MAILDROP_DOMAIN;
    private static final String EMAIL3 = MAILDROP_MAILBOX3 + MAILDROP_DOMAIN;
    private static final String EMAIL4 = MAILDROP_MAILBOX4 + MAILDROP_DOMAIN;
    private static final String EMAIL5 = MAILDROP_MAILBOX5 + MAILDROP_DOMAIN;

    private static final String[] MAILDROP_MAILBOXES = {
        MAILDROP_MAILBOX1, MAILDROP_MAILBOX2, MAILDROP_MAILBOX3, MAILDROP_MAILBOX4, MAILDROP_MAILBOX5
    };

    private User creator;
    private List<User> members;
    private Map<String, Object> group;

    private User sender;
    private List<User> recipients;
    private String groupId;
    private String messageContent;
    private List<User> mentionedMembers;


    @Override
    protected void setupServiceSpecificConfig() {
        RequestSpecification userServiceSpec = getSpecForService(USER_SERVICE_PORT);
        RequestSpecification notificationServiceSpec = getSpecForService(NOTIFICATION_SERVICE_PORT);
        RequestSpecification groupChatServiceSpec = getSpecForService(GROUP_CHAT_SERVICE_PORT);

        userTestService = new UserTestService(userServiceSpec);
        notificationTestService = new NotificationTestService(notificationServiceSpec);
        groupChatTestService = new GroupChatTestService(groupChatServiceSpec, userTestService);
        groupMessageTestService = new GroupMessageTestService(groupChatServiceSpec);
        mailhogService = new MailhogService();
    }

    @BeforeEach
    public void setUp() {
        // Create chat
        var result = groupChatTestService.createRandomGroupChatWithUsers(
            EMAIL1,
            List.of(EMAIL2, EMAIL3, EMAIL4, EMAIL5)
        );

        creator = User.builder()
                      .id(result.creator().get("id").toString())
                      .email(result.creator().get("email").toString())
                      .username(result.creator().get("username").toString())
                      .build();

        members = result.members().stream()
                        .map(member -> User.builder()
                                           .id(member.get("id").toString())
                                           .email(member.get("email").toString())
                                           .username(member.get("username").toString())
                                           .build())
                        .toList();

        group = result.groupChat();

        groupId = group.get("id").toString();
        messageContent = "This mentions @" + members.get(0).username() + " and @" + members.get(1).username() +
                        " and @" + creator.username();
        mentionedMembers = List.of(
            members.get(0),
            members.get(1),
            creator
        );

        // By default mute email notifications
        notificationTestService.disableNotification(creator.id(), "group_message", "email");
        notificationTestService.disableNotification(creator.id(), "group_mention", "email");

        for (var member : allMembers()) {
            var memberId = member.id();
            notificationTestService.disableNotification(memberId, "group_message", "email");
            notificationTestService.disableNotification(memberId, "group_mention", "email");
        }
    }

    @AfterEach
    public void cleanup() {
        userTestService.cleanup();
        notificationTestService.cleanup();
        mailhogService.emptyMailbox();
    }

    @Test
    @DisplayName("When a member sends a message, and both inbox and email are enabled for all users, all users should receive notifications except the sender")
    public void shouldReceiveNotificationWhenAllEnabled() {
        enableNotificationsForAllMembers();

        sender = members.getFirst();
        recipients = membersExcept(sender.id());

        sendMessage();

        // Assert that the sender does not receive notifications
        assertNotificationNotReceivedInInbox(sender.id());
        assertNotificationNotReceivedInEmail(sender.email());

        for (var recipient : recipients) {
            assertNotificationReceivedInInbox(recipient.id(), recipient);
            assertNotificationReceivedInEmail(recipient.email());
        }
    }

    @Test
    @DisplayName("When the creator sends a message, and both inbox and email are enabled for all users, all users should receive notifications except the sender")
    public void shouldReceiveNotificationWhenCreatorSendsMessage() {
        enableNotificationsForAllMembers();

        sender = creator;
        recipients = members;

        sendMessage();

        // Assert that the sender does not receive notifications
        assertNotificationNotReceivedInInbox(sender.id());
        assertNotificationNotReceivedInEmail(sender.email());

        for (var recipient : recipients) {
            assertNotificationReceivedInInbox(recipient.id(), recipient);
            assertNotificationReceivedInEmail(recipient.email());
        }
    }

    @Test
    @DisplayName("When a member sends a message, and only inbox is enabled for all users, all users should receive inbox notifications except the sender")
    public void shouldReceiveOnlyInboxNotificationWhenEmailDisabled() {
        // Enable inbox but disable email notifications
        for (var member : allMembers()) {
            var memberId = member.id();
            notificationTestService.enableNotification(memberId, "group_message", "inbox");
            notificationTestService.disableNotification(memberId, "group_message", "email");
            notificationTestService.enableNotification(memberId, "group_mention", "inbox");
            notificationTestService.disableNotification(memberId, "group_mention", "email");
        }

        sender = members.getFirst();
        recipients = membersExcept(sender.id());

        sendMessage();

        // Assert that the sender does not receive notifications
        assertNotificationNotReceivedInInbox(sender.id());
        assertNotificationNotReceivedInEmail(sender.email());

        for (var recipient : recipients) {
            assertNotificationReceivedInInbox(recipient.id(), recipient);
            assertNotificationNotReceivedInEmail(recipient.email());
        }
    }

    @Test
    @DisplayName("When a member sends a message, and only email is enabled for all users, all users should receive email notifications except the sender")
    public void shouldReceiveOnlyEmailNotificationWhenInboxDisabled() {
        // Enable email but disable inbox notifications
        for (var member : allMembers()) {
            var memberId = member.id();
            notificationTestService.disableNotification(memberId, "group_message", "inbox");
            notificationTestService.enableNotification(memberId, "group_message", "email");
            notificationTestService.disableNotification(memberId, "group_mention", "inbox");
            notificationTestService.enableNotification(memberId, "group_mention", "email");
        }

        sender = members.getFirst();
        recipients = membersExcept(sender.id());

        sendMessage();

        // Assert that the sender does not receive notifications
        assertNotificationNotReceivedInInbox(sender.id());
        assertNotificationNotReceivedInEmail(sender.email());

        for (var recipient : recipients) {
            assertNotificationNotReceivedInInbox(recipient.id());
            assertNotificationReceivedInEmail(recipient.email());
        }
    }

    @Test
    @DisplayName("When a member sends a message, and both inbox and email are disabled for all users, no users should receive notifications")
    public void shouldReceiveNoNotificationsWhenBothDisabled() {
        // Disable both inbox and email notifications
        for (var member : allMembers()) {
            var memberId = member.id();
            notificationTestService.disableNotification(memberId, "group_message", "inbox");
            notificationTestService.disableNotification(memberId, "group_message", "email");
            notificationTestService.disableNotification(memberId, "group_mention", "inbox");
            notificationTestService.disableNotification(memberId, "group_mention", "email");
        }

        sender = members.getFirst();
        recipients = membersExcept(sender.id());

        sendMessage();

        // Assert that the sender does not receive notifications
        assertNotificationNotReceivedInInbox(sender.id());
        assertNotificationNotReceivedInEmail(sender.email());

        for (var recipient : recipients) {
            assertNotificationNotReceivedInInbox(recipient.id());
            assertNotificationNotReceivedInEmail(recipient.email());
        }
    }

    @Test
    @DisplayName("When a member sends a message, and all notifications are muted, no users should receive notifications")
    public void shouldReceiveNoNotificationsWhenMuted() {
        // Enable notifications but then mute all
        for (var member : allMembers()) {
            var memberId = member.id();
            notificationTestService.enableNotification(memberId, "group_message", "inbox");
            notificationTestService.enableNotification(memberId, "group_message", "email");
            notificationTestService.enableNotification(memberId, "group_mention", "inbox");
            notificationTestService.enableNotification(memberId, "group_mention", "email");
            notificationTestService.muteAllNotifications(memberId);
        }

        sender = members.getFirst();
        recipients = membersExcept(sender.id());

        sendMessage();

        // Assert that the sender does not receive notifications
        assertNotificationNotReceivedInInbox(sender.id());
        assertNotificationNotReceivedInEmail(sender.email());

        for (var recipient : recipients) {
            assertNotificationNotReceivedInInbox(recipient.id());
            assertNotificationNotReceivedInEmail(recipient.email());
        }
    }



    @Test
    @DisplayName("When a new member is added, and all notifications are enabled, they should receive notifications")
    public void shouldReceiveNotificationsWhenNewMemberAdded() {
        // Add new member
        var newMemberData = userTestService.registerUserWithEmail("mathew.hanybb@gmail.com");
        var newMember = User.builder()
                            .id(newMemberData.get("id").toString())
                            .email(newMemberData.get("email").toString())
                            .username(newMemberData.get("username").toString())
                            .build();
        groupChatTestService.addMember(groupId, creator.id(), newMember.id());

        sender = members.getFirst();
        recipients = membersExcept(sender.id());
        recipients.add(newMember);

        for (var member : allMembers()) {
            var memberId = member.id();
            notificationTestService.enableNotification(memberId, "group_message", "inbox");
            notificationTestService.enableNotification(memberId, "group_message", "email");
            notificationTestService.enableNotification(memberId, "group_mention", "inbox");
            notificationTestService.enableNotification(memberId, "group_mention", "email");
        }

        sendMessage();

        // Assert that the sender does not receive notifications
        assertNotificationNotReceivedInInbox(sender.id());
        assertNotificationNotReceivedInEmail(sender.email());

        // Assert that the new member receives notifications
        assertNotificationReceivedInInbox(newMember.id(), newMember);
        assertNotificationReceivedInEmail(newMember.email());

        //  Assert that other members receive notifications
        for (var recipient : recipients) {
            if (!recipient.id().equals(newMember.id())) {
                assertNotificationReceivedInInbox(recipient.id(), recipient);
                assertNotificationReceivedInEmail(recipient.email());
            }
        }
    }

    @Test
    @DisplayName("When a member sends a message, and users have mix of inbox and email enabled, they should receive notifications accordingly")
    public void shouldReceiveNotificationsAccordingToSettings() {
        notificationTestService.enableNotification(creator.id(), "group_message", "inbox");
        notificationTestService.enableNotification(creator.id(), "group_message", "email");
        notificationTestService.enableNotification(creator.id(), "group_mention", "inbox");
        notificationTestService.enableNotification(creator.id(), "group_mention", "email");

        notificationTestService.enableNotification(members.get(1).id(), "group_message", "inbox");
        notificationTestService.disableNotification(members.get(1).id(), "group_message", "email");
        notificationTestService.enableNotification(members.get(1).id(), "group_mention", "inbox");
        notificationTestService.disableNotification(members.get(1).id(), "group_mention", "email");

        notificationTestService.enableNotification(members.get(2).id(), "group_message", "email");
        notificationTestService.disableNotification(members.get(2).id(), "group_message", "inbox");
        notificationTestService.enableNotification(members.get(2).id(), "group_mention", "email");
        notificationTestService.disableNotification(members.get(2).id(), "group_mention", "inbox");

        notificationTestService.disableNotification(members.get(3).id(), "group_message", "inbox");
        notificationTestService.disableNotification(members.get(3).id(), "group_message", "email");
        notificationTestService.disableNotification(members.get(3).id(), "group_mention", "inbox");
        notificationTestService.disableNotification(members.get(3).id(), "group_mention", "email");

        sender = members.getFirst();
        recipients = membersExcept(sender.id());

        sendMessage();

        // Assert that the sender does not receive notifications
        assertNotificationNotReceivedInInbox(sender.id());
        assertNotificationNotReceivedInEmail(sender.email());

        assertNotificationReceivedInInbox(creator.id(), creator);
        assertNotificationReceivedInEmail(creator.email());

        assertNotificationReceivedInInbox(members.get(1).id(), members.get(1));
        assertNotificationNotReceivedInEmail(members.get(1).email());

        assertNotificationNotReceivedInInbox(members.get(2).id());
        assertNotificationReceivedInEmail(members.get(2).email());

        assertNotificationNotReceivedInInbox(members.get(3).id());
        assertNotificationNotReceivedInEmail(members.get(3).email());
    }


    private List<User> allMembers() {
        var membersList = new ArrayList<>(members);
        membersList.add(creator);
        return membersList;
    }

    private List<User> membersExcept(String memberId) {
        var membersList = allMembers();
        membersList.removeIf(m -> m.id().equals(memberId));
        return membersList;
    }

    private Map<String, Object> sendMessage() {
        var message = groupMessageTestService.sendGroupMessage(
            sender.id(),
            sender.username(),
            groupId,
            messageContent
        );

        waitFor(500);

        return message;
    }

    private void assertNotificationReceivedInInbox(String userId, User recipient) {
        // Wait for notification to arrive
        var unreadNotification = getNotificationFromUnread(userId);
        var notificationInAllNotifications = getNotificationFromAllNotifications(userId);

        // Assert notification fields
        assertNotificationFields(unreadNotification, recipient);
        assertNotificationFields(notificationInAllNotifications, recipient);
    }

    private void assertNotificationReceivedInEmail(String email) {
        var subject = mentionedMembers.stream().anyMatch(m -> m.email().equals(email)) ?
            "You were mentioned in " + group.get("name").toString() :
            "New group message from " + sender.username();

        var foundMail = mailhogService.getMatchingMail(
            email,
            subject,
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
        var subject = mentionedMembers.stream().anyMatch(m -> m.email().equals(email)) ?
            "You were mentioned in " + group.get("name").toString() :
            "New group message from " + sender.username();

        var foundMail = mailhogService.getMatchingMail(
            email,
            subject,
            messageContent
        );

        assertThat(foundMail).isNull();
    }

    private void assertNotificationFields(
        Map<String, Object> actualNotification,
        User recipient
    ) {
        assertThat(actualNotification).containsKeys("id", "timestamp", "messageId");
        assertThat(actualNotification).containsAllEntriesOf(Map.of(
            "type", mentionedMembers.contains(recipient) ? "group_mention" : "group_message",
            "senderUserId", sender.id(),
            "senderUsername", sender.username(),
            "recipientUserId", recipient.id(),
            "recipientEmail", recipient.email(),
            "groupId", groupId,
            "groupName", group.get("name"),
            "messageText", messageContent,
            "read", false
        ));
        if (group.get("emoji") != null) {
            assertThat(actualNotification).containsEntry("groupEmoji", group.get("emoji"));
        }
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

    private void enableNotificationsForAllMembers() {
        for (var member : allMembers()) {
            var memberId = member.id();
            notificationTestService.enableNotification(memberId, "group_message", "inbox");
            notificationTestService.enableNotification(memberId, "group_message", "email");
            notificationTestService.enableNotification(memberId, "group_mention", "inbox");
            notificationTestService.enableNotification(memberId, "group_mention", "email");
        }
    }

    @Builder
    private record User(
        String id,
        String email,
        String username
    ) {
    }
}
