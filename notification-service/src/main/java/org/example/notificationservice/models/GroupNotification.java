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
public abstract class GroupNotification extends MessageNotification {
    private String groupId;
    private String groupName;
    private String groupIcon;
}
