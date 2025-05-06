package org.example.notificationservice.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.springframework.data.annotation.TypeAlias;

import java.time.LocalDateTime;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public abstract class MessageNotification extends Notification {
    private String senderUserId;
    private String senderUsername;
    private String messageId;
    private String messageText;
    private LocalDateTime messageTimestamp;
}
