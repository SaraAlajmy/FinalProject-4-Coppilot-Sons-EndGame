package org.example.shared.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
public class GroupMessageNotificationDTO extends GroupNotificationDTO {
    public GroupMessageNotificationDTO(
        String recipientUserId,
        String senderUserId,
        String senderUsername,
        String messageId,
        String messageText,
        LocalDateTime messageTimestamp,
        String groupId,
        String groupName,
        String groupIcon
    ) {
        super(
            recipientUserId,
            NotificationType.GROUP_MESSAGE,
            senderUserId,
            senderUsername,
            messageId,
            messageText,
            messageTimestamp,
            groupId,
            groupName,
            groupIcon
        );
    }
}
