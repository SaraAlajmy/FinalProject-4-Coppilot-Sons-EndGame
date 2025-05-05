package org.example.notificationservice.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.springframework.data.annotation.TypeAlias;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@TypeAlias(NotificationType.DIRECT_MESSAGE)
public class DirectMessageNotification extends MessageNotification {
    private String chatId;
}
