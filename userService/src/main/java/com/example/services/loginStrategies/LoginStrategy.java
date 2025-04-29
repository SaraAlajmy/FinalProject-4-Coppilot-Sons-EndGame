package com.example.services.loginStrategies;

import com.example.models.User;

public interface LoginStrategy {

    User loadUser(String username);
}

