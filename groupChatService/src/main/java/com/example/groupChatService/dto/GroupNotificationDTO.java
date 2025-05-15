package com.example.groupChatService.dto;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GroupNotificationDTO {
    private String recipientUserId;
    private String type; // "group_message" or "group_mention"
    private String senderUserId;
    private String senderUserName;
    private String messageId;
    private String messageText;
    private LocalDateTime messageTimestamp;
    private String groupId;
    private String groupName;
    private String groupIcon;
}



