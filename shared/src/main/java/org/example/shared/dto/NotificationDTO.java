package org.example.shared.dto;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "type", visible = true)
@JsonSubTypes({
    @JsonSubTypes.Type(value = GroupMessageNotificationDTO.class, name = NotificationType.GROUP_MESSAGE),
    @JsonSubTypes.Type(value = GroupMentionNotificationDTO.class, name = NotificationType.GROUP_MENTION),
    @JsonSubTypes.Type(value = DirectMessageNotificationDTO.class, name = NotificationType.DIRECT_MESSAGE),
    @JsonSubTypes.Type(value = ResetPasswordNotificationDTO.class, name = NotificationType.RESET_PASSWORD)

})

public abstract class NotificationDTO {
    private String recipientUserId;
    private String type;
}
