package com.example.groupChatService.dto;

import java.time.LocalDateTime;

public class NotificationPayload {
    private String recipientUserId;
    private String type;
    private String senderUserId;
    private String senderName;
    private String messageId;
    private String messageText;
    private LocalDateTime messageTimestamp;

    private String groupId;
    private String groupName;
    private String groupIcon;

    public String getRecipientUserId() { return recipientUserId; }
    public void setRecipientUserId(String recipientUserId) { this.recipientUserId = recipientUserId; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public String getSenderUserId() { return senderUserId; }
    public void setSenderUserId(String senderUserId) { this.senderUserId = senderUserId; }

    public String getSenderName() { return senderName; }
    public void setSenderName(String senderName) { this.senderName = senderName; }

    public String getMessageId() { return messageId; }
    public void setMessageId(String messageId) { this.messageId = messageId; }

    public String getMessageText() { return messageText; }
    public void setMessageText(String messageText) { this.messageText = messageText; }

    public LocalDateTime getMessageTimestamp() { return messageTimestamp; }
    public void setMessageTimestamp(LocalDateTime messageTimestamp) { this.messageTimestamp = messageTimestamp; }

    public String getGroupId() { return groupId; }
    public void setGroupId(String groupId) { this.groupId = groupId; }

    public String getGroupName() { return groupName; }
    public void setGroupName(String groupName) { this.groupName = groupName; }

    public String getGroupIcon() { return groupIcon; }
    public void setGroupIcon(String groupIcon) { this.groupIcon = groupIcon; }
}
