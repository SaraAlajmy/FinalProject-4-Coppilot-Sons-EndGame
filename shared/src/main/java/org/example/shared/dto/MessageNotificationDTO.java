package org.example.shared.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
public abstract class MessageNotificationDTO extends NotificationDTO {
    private String senderUserId;
    private String senderUsername;
    private String messageId;
    private String messageText;
    private LocalDateTime messageTimestamp;


    public MessageNotificationDTO(
        String recipientUserId,
        String type,
        String senderUserId,
        String senderUsername,
        String messageId,
        String messageText,
        LocalDateTime messageTimestamp
    ) {
        super(recipientUserId, type);
        this.senderUserId = senderUserId;
        this.senderUsername = senderUsername;
        this.messageId = messageId;
        this.messageText = messageText;
        this.messageTimestamp = messageTimestamp;
    }
}
