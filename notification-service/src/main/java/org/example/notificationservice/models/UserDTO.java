package org.example.notificationservice.models;

public class UserDTO {
    private String userId;
    private String email; // Receiver's email

    // Constructor
    public UserDTO(String userId, String email) {
        this.userId = userId;
        this.email = email;
    }

    // Getters
    public String getUserId() {
        return userId;
    }

    public String getEmail() {
        return email;
    }
}
