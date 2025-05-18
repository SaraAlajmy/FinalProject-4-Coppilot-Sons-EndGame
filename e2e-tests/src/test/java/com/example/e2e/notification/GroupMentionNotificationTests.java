package com.example.e2e.notification;


import com.example.e2e.base.BaseApiTest;
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
        MAILDROP_MAILBOX1,
        MAILDROP_MAILBOX2,
        MAILDROP_MAILBOX3,
        MAILDROP_MAILBOX4,
        MAILDROP_MAILBOX5
    };

    private User creator;
    private List<User> members;
    private Map<String, Object> group;

    private User sender;
    private List<User> recipients;
    private String groupId;
    private String messageContent;
    private List<User> mentionedMembers;

    @BeforeEach
    public void setUp() {
        // Create chat
        var result = groupChatTestService.createRandomGroupChatWithUsers(
            List.of(EMAIL2, EMAIL3, EMAIL4, EMAIL5)
        );

        creator = User.builder()
                      .id(loggedInUser.get("id").toString())
                      .email(loggedInUser.get("email").toString())
                      .username(loggedInUser.get("username").toString())
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
        messageContent =
            "This mentions @" + members.get(0).username() + " and @" + members.get(1).username() +
            " and @" + creator.username();
        mentionedMembers = List.of(
            members.get(0),
            members.get(1),
            creator
        );

        // By default mute email notifications
        notificationTestService.disableNotification("group_message", "email");
        notificationTestService.disableNotification("group_mention", "email");

        for (var member : allMembers()) {
            loggedAs(
                member, () -> {
                    notificationTestService.disableNotification("group_message", "email");
                    notificationTestService.disableNotification("group_mention", "email");
                }
            );
        }
    }

    @AfterEach
    public void cleanup() {
//        userTestService.cleanup();
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
        assertNotificationNotReceivedInInbox(sender);
        assertNotificationNotReceivedInEmail(sender.email());

        for (var recipient : recipients) {
            assertNotificationReceivedInInbox(recipient);
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
        assertNotificationNotReceivedInInbox(sender);
        assertNotificationNotReceivedInEmail(sender.email());

        for (var recipient : recipients) {
            assertNotificationReceivedInInbox(recipient);
            assertNotificationReceivedInEmail(recipient.email());
        }
    }

    @Test
    @DisplayName("When a member sends a message, and only inbox is enabled for all users, all users should receive inbox notifications except the sender")
    public void shouldReceiveOnlyInboxNotificationWhenEmailDisabled() {
        // Enable inbox but disable email notifications
        for (var member : allMembers()) {
            loggedAs(
                member, () -> {
                    notificationTestService.enableNotification("group_message", "inbox");
                    notificationTestService.disableNotification("group_message", "email");
                    notificationTestService.enableNotification("group_mention", "inbox");
                    notificationTestService.disableNotification("group_mention", "email");
                }
            );
        }

        sender = members.getFirst();
        recipients = membersExcept(sender.id());

        sendMessage();

        // Assert that the sender does not receive notifications
        assertNotificationNotReceivedInInbox(sender);
        assertNotificationNotReceivedInEmail(sender.email());

        for (var recipient : recipients) {
            assertNotificationReceivedInInbox(recipient);
            assertNotificationNotReceivedInEmail(recipient.email());
        }
    }

    @Test
    @DisplayName("When a member sends a message, and only email is enabled for all users, all users should receive email notifications except the sender")
    public void shouldReceiveOnlyEmailNotificationWhenInboxDisabled() {
        // Enable email but disable inbox notifications
        for (var member : allMembers()) {
            loggedAs(
                member, () -> {
                    notificationTestService.disableNotification("group_message", "inbox");
                    notificationTestService.enableNotification("group_message", "email");
                    notificationTestService.disableNotification("group_mention", "inbox");
                    notificationTestService.enableNotification("group_mention", "email");
                }
            );
        }

        sender = members.getFirst();
        recipients = membersExcept(sender.id());

        sendMessage();

        // Assert that the sender does not receive notifications
        assertNotificationNotReceivedInInbox(sender);
        assertNotificationNotReceivedInEmail(sender.email());

        for (var recipient : recipients) {
            assertNotificationNotReceivedInInbox(recipient);
            assertNotificationReceivedInEmail(recipient.email());
        }
    }

    @Test
    @DisplayName("When a member sends a message, and both inbox and email are disabled for all users, no users should receive notifications")
    public void shouldReceiveNoNotificationsWhenBothDisabled() {
        // Disable both inbox and email notifications
        for (var member : allMembers()) {
            loggedAs(
                member, () -> {
                    notificationTestService.disableNotification("group_message", "inbox");
                    notificationTestService.disableNotification("group_message", "email");
                    notificationTestService.disableNotification("group_mention", "inbox");
                    notificationTestService.disableNotification("group_mention", "email");
                }
            );
        }

        sender = members.getFirst();
        recipients = membersExcept(sender.id());

        sendMessage();

        // Assert that the sender does not receive notifications
        assertNotificationNotReceivedInInbox(sender);
        assertNotificationNotReceivedInEmail(sender.email());

        for (var recipient : recipients) {
            assertNotificationNotReceivedInInbox(recipient);
            assertNotificationNotReceivedInEmail(recipient.email());
        }
    }

    @Test
    @DisplayName("When a member sends a message, and all notifications are muted, no users should receive notifications")
    public void shouldReceiveNoNotificationsWhenMuted() {
        // Enable notifications but then mute all
        for (var member : allMembers()) {
            loggedAs(
                member, () -> {
                    notificationTestService.enableNotification("group_message", "inbox");
                    notificationTestService.enableNotification("group_message", "email");
                    notificationTestService.enableNotification("group_mention", "inbox");
                    notificationTestService.enableNotification("group_mention", "email");
                    notificationTestService.muteAllNotifications();
                }
            );
        }

        sender = members.getFirst();
        recipients = membersExcept(sender.id());

        sendMessage();

        // Assert that the sender does not receive notifications
        assertNotificationNotReceivedInInbox(sender);
        assertNotificationNotReceivedInEmail(sender.email());

        for (var recipient : recipients) {
            assertNotificationNotReceivedInInbox(recipient);
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
        groupChatTestService.addMember(groupId, newMember.id());

        sender = members.getFirst();
        recipients = membersExcept(sender.id());
        recipients.add(newMember);

        for (var member : allMembers()) {
            loggedAs(
                member, () -> {
                    notificationTestService.enableNotification("group_message", "inbox");
                    notificationTestService.enableNotification("group_message", "email");
                    notificationTestService.enableNotification("group_mention", "inbox");
                    notificationTestService.enableNotification("group_mention", "email");
                }
            );
        }

        sendMessage();

        // Assert that the sender does not receive notifications
        assertNotificationNotReceivedInInbox(sender);
        assertNotificationNotReceivedInEmail(sender.email());

        // Assert that the new member receives notifications
        assertNotificationReceivedInInbox(newMember);
        assertNotificationReceivedInEmail(newMember.email());

        //  Assert that other members receive notifications
        for (var recipient : recipients) {
            if (!recipient.id().equals(newMember.id())) {
                assertNotificationReceivedInInbox(recipient);
                assertNotificationReceivedInEmail(recipient.email());
            }
        }
    }

    @Test
    @DisplayName("When a member sends a message, and users have mix of inbox and email enabled, they should receive notifications accordingly")
    public void shouldReceiveNotificationsAccordingToSettings() {
        loggedAs(
            creator, () -> {
                notificationTestService.enableNotification("group_message", "inbox");
                notificationTestService.enableNotification("group_message", "email");
                notificationTestService.enableNotification("group_mention", "inbox");
                notificationTestService.enableNotification("group_mention", "email");
            }
        );

        loggedAs(
            members.get(1), () -> {
                notificationTestService.enableNotification("group_message", "inbox");
                notificationTestService.disableNotification("group_message", "email");
                notificationTestService.enableNotification("group_mention", "inbox");
                notificationTestService.disableNotification("group_mention", "email");
            }
        );

        loggedAs(
            members.get(2), () -> {

                notificationTestService.enableNotification("group_message", "email");
                notificationTestService.disableNotification("group_message", "inbox");
                notificationTestService.enableNotification("group_mention", "email");
                notificationTestService.disableNotification("group_mention", "inbox");
            }
        );

        loggedAs(
            members.get(3), () -> {
                notificationTestService.disableNotification("group_message", "inbox");
                notificationTestService.disableNotification("group_message", "email");
                notificationTestService.disableNotification("group_mention", "inbox");
                notificationTestService.disableNotification("group_mention", "email");
            }
        );

        sender = members.getFirst();
        recipients = membersExcept(sender.id());

        sendMessage();

        // Assert that the sender does not receive notifications
        assertNotificationNotReceivedInInbox(sender);
        assertNotificationNotReceivedInEmail(sender.email());

        assertNotificationReceivedInInbox(creator);
        assertNotificationReceivedInEmail(creator.email());

        assertNotificationReceivedInInbox(members.get(1));
        assertNotificationNotReceivedInEmail(members.get(1).email());

        assertNotificationNotReceivedInInbox(members.get(2));
        assertNotificationReceivedInEmail(members.get(2).email());

        assertNotificationNotReceivedInInbox(members.get(3));
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

    private void sendMessage() {
        loggedAs(
            sender, () -> {
                var message = groupMessageTestService.sendGroupMessage(
                    groupId,
                    messageContent
                );

                waitFor(500);
            }
        );
    }

    private void assertNotificationReceivedInInbox(User recipient) {
        loggedAs(
            recipient, () -> {
                // Wait for notification to arrive
                var unreadNotification = getNotificationFromUnread();
                var notificationInAllNotifications = getNotificationFromAllNotifications();

                // Assert notification fields
                assertNotificationFields(unreadNotification, recipient);
                assertNotificationFields(notificationInAllNotifications, recipient);
            }
        );
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

    private void assertNotificationNotReceivedInInbox(User recipient) {
        try {
            loggedAs(
                recipient, () -> {
                    // Wait for notification to arrive
                    var unreadNotification = getNotificationFromUnread();
                    var notificationInAllNotifications = getNotificationFromAllNotifications();

                    assertThat(unreadNotification).isNull();
                    assertThat(notificationInAllNotifications).isNull();
                }
            );
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

    private void enableNotificationsForAllMembers() {
        for (var member : allMembers()) {
            loggedAs(
                member, () -> {
                    notificationTestService.enableNotification("group_message", "inbox");
                    notificationTestService.enableNotification("group_message", "email");
                    notificationTestService.enableNotification("group_mention", "inbox");
                    notificationTestService.enableNotification("group_mention", "email");
                }
            );
        }
    }
}
