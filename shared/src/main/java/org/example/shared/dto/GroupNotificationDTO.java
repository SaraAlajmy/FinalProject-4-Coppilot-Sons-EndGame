package org.example.shared.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
public abstract class GroupNotificationDTO extends MessageNotificationDTO {
    private String groupId;
    private String groupName;
    private String groupIcon;

    public GroupNotificationDTO(
        String recipientUserId,
        String type,
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
            type,
            senderUserId,
            senderUsername,
            messageId,
            messageText,
            messageTimestamp
        );
        this.groupId = groupId;
        this.groupName = groupName;
        this.groupIcon = groupIcon;
    }
}
