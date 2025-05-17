package com.example.e2e.notification;

import com.example.e2e.base.BaseApiTest;
import com.example.e2e.service.NotificationTestService;
import com.example.e2e.service.UserTestService;
import io.restassured.specification.RequestSpecification;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * End-to-end tests for Notification Settings API.
 */
public class NotificationSettingsApiTests extends BaseApiTest {
    // All notification types
    private static final String DIRECT_MESSAGE = "direct_message";
    private static final String GROUP_MESSAGE = "group_message";
    private static final String GROUP_MENTION = "group_mention";
    private static final List<String> NOTIFICATION_TYPES = Arrays.asList(
            DIRECT_MESSAGE, GROUP_MESSAGE, GROUP_MENTION);

    // All strategy types
    private static final String INBOX = "inbox";
    private static final String EMAIL = "email";
    private static final List<String> STRATEGY_TYPES = Arrays.asList(INBOX, EMAIL);

    private RequestSpecification notificationServiceSpec;
    private RequestSpecification userServiceSpec;
    private NotificationTestService notificationTestService;
    private UserTestService userTestService;
    private String testUserId;

    @Override
    protected void setupServiceSpecificConfig() {
        // Configure the notification service specific request specification
        notificationServiceSpec = getSpecForService(NOTIFICATION_SERVICE_PORT);
        userServiceSpec = getSpecForService(USER_SERVICE_PORT);
        
        notificationTestService = new NotificationTestService(notificationServiceSpec);
        userTestService = new UserTestService(userServiceSpec);
    }

    @BeforeEach
    public void setUp() {
        // Create a test user to use in notification tests
        Map<String, Object> createdUser = userTestService.registerUser();
        testUserId = createdUser.get("id").toString();
    }

    @Test
    @DisplayName("Should get user notification settings structure")
    public void shouldGetUserNotificationSettings() {
        // Get user's notification settings
        Map<String, Object> settings = notificationTestService.getUserNotificationSettings(testUserId);
        
        // Verify the response contains the expected user ID and notification strategy map
        assertThat(settings).containsKey("userId");
        assertThat(settings).containsKey("notificationStrategy");
        assertThat(settings.get("userId")).isEqualTo(testUserId);
    }

    @Test
    @DisplayName("Should have all notifications enabled by default")
    public void shouldHaveAllNotificationsEnabledByDefault() {
        // Verify all combinations of notification types and strategies are enabled by default
        for (String notificationType : NOTIFICATION_TYPES) {
            for (String strategyType : STRATEGY_TYPES) {
                boolean status = notificationTestService.getNotificationStatus(testUserId, notificationType, strategyType);
                assertThat(status)
                    .as("Notification %s with strategy %s should be enabled by default", 
                        notificationType, strategyType)
                    .isTrue();
            }
        }
    }

    @Test
    @DisplayName("Should disable all notification combinations")
    public void shouldDisableAllNotificationCombinations() {
        // Disable each combination one by one and verify
        for (String notificationType : NOTIFICATION_TYPES) {
            for (String strategyType : STRATEGY_TYPES) {
                // Verify notification is enabled by default
                boolean initialStatus = notificationTestService.getNotificationStatus(
                    testUserId, notificationType, strategyType);
                assertThat(initialStatus)
                    .as("Notification %s with strategy %s should be enabled by default", 
                        notificationType, strategyType)
                    .isTrue();
                
                // Disable the notification
                notificationTestService.disableNotification(testUserId, notificationType, strategyType);
                
                // Verify notification is now disabled
                boolean updatedStatus = notificationTestService.getNotificationStatus(
                    testUserId, notificationType, strategyType);
                assertThat(updatedStatus)
                    .as("Notification %s with strategy %s should be disabled after disabling", 
                        notificationType, strategyType)
                    .isFalse();
            }
        }
    }

    @Test
    @DisplayName("Should enable all notification combinations")
    public void shouldEnableAllNotificationCombinations() {
        // First disable all notifications
        notificationTestService.muteAllNotifications(testUserId);
        
        // Enable each combination one by one and verify
        for (String notificationType : NOTIFICATION_TYPES) {
            for (String strategyType : STRATEGY_TYPES) {
                // Verify notification is disabled
                boolean initialStatus = notificationTestService.getNotificationStatus(
                    testUserId, notificationType, strategyType);
                assertThat(initialStatus)
                    .as("Notification %s with strategy %s should be disabled after mute-all", 
                        notificationType, strategyType)
                    .isFalse();
                
                // Enable the notification
                notificationTestService.enableNotification(testUserId, notificationType, strategyType);
                
                // Verify notification is now enabled
                boolean updatedStatus = notificationTestService.getNotificationStatus(
                    testUserId, notificationType, strategyType);
                assertThat(updatedStatus)
                    .as("Notification %s with strategy %s should be enabled after enabling", 
                        notificationType, strategyType)
                    .isTrue();
            }
        }
    }

    @Test
    @DisplayName("Should mute all notifications")
    public void shouldMuteAllNotifications() {
        // Mute all notifications
        notificationTestService.muteAllNotifications(testUserId);
        
        // Verify all notification types and strategies are disabled
        for (String notificationType : NOTIFICATION_TYPES) {
            for (String strategyType : STRATEGY_TYPES) {
                boolean status = notificationTestService.getNotificationStatus(
                    testUserId, notificationType, strategyType);
                assertThat(status)
                    .as("Notification %s with strategy %s should be disabled after mute-all", 
                        notificationType, strategyType)
                    .isFalse();
            }
        }
    }

    @Test
    @DisplayName("Should unmute all notifications")
    public void shouldUnmuteAllNotifications() {
        // First mute all notifications
        notificationTestService.muteAllNotifications(testUserId);
        
        // Verify notifications are muted
        for (String notificationType : NOTIFICATION_TYPES) {
            for (String strategyType : STRATEGY_TYPES) {
                boolean status = notificationTestService.getNotificationStatus(
                    testUserId, notificationType, strategyType);
                assertThat(status)
                    .as("Notification %s with strategy %s should be disabled after mute-all", 
                        notificationType, strategyType)
                    .isFalse();
            }
        }
        
        // Unmute all notifications
        notificationTestService.unmuteAllNotifications(testUserId);
        
        // Verify all notifications are unmuted
        for (String notificationType : NOTIFICATION_TYPES) {
            for (String strategyType : STRATEGY_TYPES) {
                boolean status = notificationTestService.getNotificationStatus(
                    testUserId, notificationType, strategyType);
                assertThat(status)
                    .as("Notification %s with strategy %s should be enabled after unmute-all", 
                        notificationType, strategyType)
                    .isTrue();
            }
        }
    }
    
    @Test
    @DisplayName("Should keep other notifications unchanged when modifying specific one")
    public void shouldKeepOtherNotificationsUnchanged() {
        // Disable one specific notification setting
        notificationTestService.disableNotification(testUserId, DIRECT_MESSAGE, EMAIL);
        
        // Verify that specific setting is disabled
        boolean disabledStatus = notificationTestService.getNotificationStatus(testUserId, DIRECT_MESSAGE, EMAIL);
        assertThat(disabledStatus).isFalse();
        
        // Verify all other settings remain enabled by default
        for (String notificationType : NOTIFICATION_TYPES) {
            for (String strategyType : STRATEGY_TYPES) {
                if (!(notificationType.equals(DIRECT_MESSAGE) && strategyType.equals(EMAIL))) {
                    boolean status = notificationTestService.getNotificationStatus(
                        testUserId, notificationType, strategyType);
                    assertThat(status)
                        .as("Notification %s with strategy %s should remain enabled", 
                            notificationType, strategyType)
                        .isTrue();
                }
            }
        }
    }
}
