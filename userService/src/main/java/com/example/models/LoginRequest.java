package com.example.models;

public class LoginRequest {

    private String identifier;  // This can be a username or phone number
    private String password;    // User password
    private String type;        // Type of login: "username" or "phone"



    public LoginRequest() {}

    public LoginRequest(String identifier, String password, String type) {
        this.identifier = identifier;
        this.password = password;
        this.type = type;
    }

    public String getIdentifier() {
        return identifier;
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
