package com.example.chat_service.dto;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class NotificationDTO {

    private String recipientUserId;
    private String type = "direct_message";
    private String senderUserId;
    private String senderUserName;
    private String messageId;
    private String messageText;
    private LocalDateTime messageTimestamp;
    private String chatId;


}



