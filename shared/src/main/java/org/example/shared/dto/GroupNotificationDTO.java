package org.example.shared.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public abstract class GroupNotificationDTO extends MessageNotificationDTO {
    private String groupId;
    private String groupName;
    private String groupIcon;
}
