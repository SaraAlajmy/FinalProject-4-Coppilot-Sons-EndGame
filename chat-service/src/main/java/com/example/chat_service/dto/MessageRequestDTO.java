package com.example.chat_service.dto;


import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MessageRequestDTO {
    private String content;
    private String receiverId;
}
