package com.example.groupChatService.models;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;
import java.time.LocalDateTime;

@Document(collection = "groupMessage")
public class GroupMessage {
    @Id
    private String id;
    private String groupId;
    private String senderId;
    private String content;
    @CreatedDate
    private LocalDateTime createdAt;   // <-- set automatically when inserting

    @LastModifiedDate
    private LocalDateTime updatedAt;   // <-- set automatically when updating
    private boolean archived;

    public GroupMessage(String groupId, String senderId, String content) {
        this.groupId = groupId;
        this.senderId = senderId;
        this.content = content;
        this.archived = false;
    }

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public String getId() {
        return id;
    }

    public String getSenderId() {
        return senderId;
    }

    public void setSenderId(String senderId) {
        this.senderId = senderId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }


    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }


    public boolean isArchived() {
        return archived;
    }

    public void setArchived(boolean archived) {
        this.archived = archived;
    }
}
