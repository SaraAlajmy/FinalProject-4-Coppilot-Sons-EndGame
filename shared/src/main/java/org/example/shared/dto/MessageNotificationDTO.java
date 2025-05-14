package org.example.shared.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public abstract class MessageNotificationDTO extends NotificationDTO {
    private String senderUserId;
    private String senderUsername;
    private String messageId;
    private String messageText;
    private LocalDateTime messageTimestamp;
}
