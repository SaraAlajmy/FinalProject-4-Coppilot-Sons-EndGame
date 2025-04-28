package com.example.models;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
public class User {
    @Id
    private Long id;
    private String username;
    private String passwordHash;

    @Embedded
    private NotificationSettings notificationSettings = new NotificationSettings();

    @ElementCollection
    private List<String> inboxMessages = new ArrayList<>();

    @ManyToMany
    private Set<User> blockedUsers = new HashSet<>();

    public User() {
    }

    public User(String username, String passwordHash) {
        this.username = username;
        this.passwordHash = passwordHash;
    }

    public User(Long id, String username, String passwordHash, NotificationSettings notificationSettings, List<String> inboxMessages, Set<User> blockedUsers) {
        this.id = id;
        this.username = username;
        this.passwordHash = passwordHash;
        this.notificationSettings = notificationSettings;
        this.inboxMessages = inboxMessages;
        this.blockedUsers = blockedUsers;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public NotificationSettings getNotificationSettings() {
        return notificationSettings;
    }

    public void setNotificationSettings(NotificationSettings notificationSettings) {
        this.notificationSettings = notificationSettings;
    }

    public List<String> getInboxMessages() {
        return inboxMessages;
    }

    public void setInboxMessages(List<String> inboxMessages) {
        this.inboxMessages = inboxMessages;
    }

    public Set<User> getBlockedUsers() {
        return blockedUsers;
    }

    public void setBlockedUsers(Set<User> blockedUsers) {
        this.blockedUsers = blockedUsers;
    }

}
