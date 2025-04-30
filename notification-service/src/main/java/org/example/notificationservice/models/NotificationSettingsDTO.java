package org.example.notificationservice.models;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;


public class NotificationSettingsDTO {
    private Boolean muteNotifications ;
    private Boolean directMessageEmail;
    private Boolean directMessageInbox;
    private Boolean groupMessageEmail;
    private Boolean groupMessageInbox;
    private Boolean groupMentionEmail;
    private Boolean groupMentionInbox;

    // Constructor
    public NotificationSettingsDTO(){}
    public NotificationSettingsDTO(Boolean muteNotifications, Boolean directMessageEmail, Boolean directMessageInbox,
                                    Boolean groupMessageEmail, Boolean groupMessageInbox, Boolean groupMentionEmail,
                                    Boolean groupMentionInbox) {
        this.muteNotifications = muteNotifications;
        this.directMessageEmail = directMessageEmail;
        this.directMessageInbox = directMessageInbox;
        this.groupMessageEmail = groupMessageEmail;
        this.groupMessageInbox = groupMessageInbox;
        this.groupMentionEmail = groupMentionEmail;
        this.groupMentionInbox = groupMentionInbox;
    }

    // Getters
    public Boolean getMuteNotifications() {
        return muteNotifications;
    }
    public Boolean getDirectMessageEmail() {
        return directMessageEmail;
    }
    public Boolean getDirectMessageInbox() {
        return directMessageInbox;
    }
    public Boolean getGroupMessageEmail() {
        return groupMessageEmail;
    }
    public Boolean getGroupMessageInbox() {
        return groupMessageInbox;
    }
    public Boolean getGroupMentionEmail() {
        return groupMentionEmail;
    }
    public Boolean getGroupMentionInbox() {
        return groupMentionInbox;
    }

    @Override
    public String toString() {
        return "NotificationSettingsDTO{" +
                "muteNotifications=" + muteNotifications +
                ", directMessageEmail=" + directMessageEmail +
                ", directMessageInbox=" + directMessageInbox +
                ", groupMessageEmail=" + groupMessageEmail +
                ", groupMessageInbox=" + groupMessageInbox +
                ", groupMentionEmail=" + groupMentionEmail +
                ", groupMentionInbox=" + groupMentionInbox +
                '}';
    }
}
