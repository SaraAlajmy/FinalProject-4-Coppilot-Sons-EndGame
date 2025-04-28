package com.example.models;

import jakarta.persistence.Embeddable;

@Embeddable
public class NotificationSettings {

    private Boolean muteNotifications = false;
    private Boolean directMessageEmail;
    private Boolean directMessageInbox;
    private Boolean groupMessageEmail;
    private Boolean groupMessageInbox;
    private Boolean groupMentionEmail;
    private Boolean groupMentionInbox;

    public Boolean getMuteNotifications() {
        return muteNotifications;
    }

    public void setMuteNotifications(Boolean muteNotifications) {
        this.muteNotifications = muteNotifications;
    }

    public Boolean getDirectMessageEmail() {
        return directMessageEmail;
    }

    public void setDirectMessageEmail(Boolean directMessageEmail) {
        this.directMessageEmail = directMessageEmail;
    }

    public Boolean getDirectMessageInbox() {
        return directMessageInbox;
    }

    public void setDirectMessageInbox(Boolean directMessageInbox) {
        this.directMessageInbox = directMessageInbox;
    }

    public Boolean getGroupMessageEmail() {
        return groupMessageEmail;
    }

    public void setGroupMessageEmail(Boolean groupMessageEmail) {
        this.groupMessageEmail = groupMessageEmail;
    }

    public Boolean getGroupMessageInbox() {
        return groupMessageInbox;
    }

    public void setGroupMessageInbox(Boolean groupMessageInbox) {
        this.groupMessageInbox = groupMessageInbox;
    }

    public Boolean getGroupMentionEmail() {
        return groupMentionEmail;
    }

    public void setGroupMentionEmail(Boolean groupMentionEmail) {
        this.groupMentionEmail = groupMentionEmail;
    }

    public Boolean getGroupMentionInbox() {
        return groupMentionInbox;
    }

    public void setGroupMentionInbox(Boolean groupMentionInbox) {
        this.groupMentionInbox = groupMentionInbox;
    }
}
