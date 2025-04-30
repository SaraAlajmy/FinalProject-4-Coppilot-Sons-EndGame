package org.example.notificationservice.models;

public class UserDTO {
    private String userId;
    private String name;  // Receiver's name
    private String email; // Receiver's email

    // Constructor
    public UserDTO(String userId, String name, String email) {
        this.userId = userId;
        this.name = name;
        this.email = email;
    }

    // Getters
    public String getUserId() {
        return userId;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }
}
