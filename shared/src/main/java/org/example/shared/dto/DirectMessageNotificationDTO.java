package org.example.shared.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
public class DirectMessageNotificationDTO extends MessageNotificationDTO {
    private String chatId;

    public DirectMessageNotificationDTO(
        String recipientUserId,
        String senderUserId,
        String senderUsername,
        String messageId,
        String messageText,
        LocalDateTime messageTimestamp,
        String chatId
    ) {
        super(
            recipientUserId,
            NotificationType.DIRECT_MESSAGE,
            senderUserId,
            senderUsername,
            messageId,
            messageText,
            messageTimestamp
        );
        this.chatId = chatId;
    }
}
