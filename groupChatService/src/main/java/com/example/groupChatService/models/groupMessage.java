package com.example.groupChatService.models;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;
import java.time.LocalDateTime;

@Document(collection = "groupMessage")
public class groupMessage {
    @Id
    private String id;
    private String groupId;
    private String senderId;
    private String message;
    @CreatedDate
    private Instant createdAt;   // <-- set automatically when inserting

    @LastModifiedDate
    private Instant updatedAt;   // <-- set automatically when updating
    private boolean archived;

    public groupMessage(String groupId, String senderId, String message) {
        this.groupId = groupId;
        this.senderId = senderId;
        this.message = message;
        this.archived = false;
    }
}
