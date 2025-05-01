package org.example.notificationservice.models;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.springframework.data.annotation.TypeAlias;

@Data
@SuperBuilder
@NoArgsConstructor
@TypeAlias(NotificationType.DIRECT_MESSAGE)
public class DirectMessageNotification extends MessageNotification {
}
