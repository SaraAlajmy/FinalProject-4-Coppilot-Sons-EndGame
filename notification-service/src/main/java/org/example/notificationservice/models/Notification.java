package org.example.notificationservice.models;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "notifications")
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "type", visible = true)
@JsonSubTypes({
    @JsonSubTypes.Type(value = GroupMessageNotification.class, name = NotificationType.GROUP_MESSAGE),
    @JsonSubTypes.Type(value = GroupMentionNotification.class, name = NotificationType.GROUP_MENTION),
    @JsonSubTypes.Type(value = DirectMessageNotification.class, name = NotificationType.DIRECT_MESSAGE)
})

public abstract class Notification {
    @Id
    private String id;
    private String recipientUserId;
    private String recipientEmail;
    private LocalDateTime timestamp;
    private boolean isRead;
    private String type;
}
