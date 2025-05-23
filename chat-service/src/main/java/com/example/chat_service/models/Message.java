package com.example.chat_service.models;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;


@Document(collection = "messages")
@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class Message {

    @Id
    private String id;

    private String chatId;

    private String senderId;

    private String senderUserName;

    private String receiverId;

    private String content;

    private boolean isDeleted = false;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;


    public Message(String chatId, String senderId, String senderUserName, String receiverId, String content) {
        this.chatId = chatId;
        this.senderId = senderId;
        this.senderUserName = senderUserName;
        this.receiverId = receiverId;
        this.content = content;
        this.isDeleted = false;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = this.createdAt;
    }


    public void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = this.createdAt;
    }

    public void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}
