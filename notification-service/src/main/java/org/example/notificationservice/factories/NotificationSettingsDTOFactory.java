package org.example.notificationservice.factories;

import com.github.javafaker.Faker;
import org.example.notificationservice.models.NotificationSettingsDTO;
import org.springframework.stereotype.Component;

@Component
public class NotificationSettingsDTOFactory {
    private final Faker faker = new Faker();

    public NotificationSettingsDTO createNotificationSettingsDTO() {
        return new NotificationSettingsDTO(
                faker.bool().bool(), // muteNotifications
                faker.bool().bool(), // directMessageEmail
                faker.bool().bool(), // directMessageInbox
                faker.bool().bool(), // groupMessageEmail
                faker.bool().bool(), // groupMessageInbox
                faker.bool().bool(), // groupMentionEmail
                faker.bool().bool()  // groupMentionInbox
        );
    }
}
