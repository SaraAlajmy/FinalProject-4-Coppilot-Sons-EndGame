package com.example.models;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false, unique = true)
    private String username;
    private String password;
    @Column(nullable = false, unique = true)
    private String phoneNumber;

    @Embedded
    private NotificationSettings notificationSettings = new NotificationSettings();

    @ManyToMany
    private Set<User> blockedUsers = new HashSet<>();

    private String refreshToken;


    public User() {
    }

    private User(String username, String phoneNumber, String password) {
        this.username = username;
        this.phoneNumber = phoneNumber;
        this.password = password;
    }

    public User(Long id, String username, String password, NotificationSettings notificationSettings, Set<User> blockedUsers) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.notificationSettings = notificationSettings;
        this.blockedUsers = blockedUsers;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String passwordHash) {
        this.password= passwordHash;
    }

    public NotificationSettings getNotificationSettings() {
        return notificationSettings;
    }

    public void setNotificationSettings(NotificationSettings notificationSettings) {
        this.notificationSettings = notificationSettings;
    }

    public Set<User> getBlockedUsers() {
        return blockedUsers;
    }

    public void setBlockedUsers(Set<User> blockedUsers) {
        this.blockedUsers = blockedUsers;
    }
    public String getPhoneNumber() {
        return phoneNumber;
    }
    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }
    public String getRefreshToken() {
        return refreshToken;
    }
    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }

}
