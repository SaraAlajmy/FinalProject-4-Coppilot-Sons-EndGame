package com.example.chat_service.dto;


import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MessageRequestDTO {
    private String content;
    private String senderId;
    private String receiverId;
    private String chatId;
    private boolean isFavorite;
}
