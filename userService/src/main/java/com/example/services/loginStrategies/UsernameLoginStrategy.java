package com.example.services.loginStrategies;

import com.example.models.User;
import com.example.repositories.UserRepository;
import org.springframework.stereotype.Component;

@Component("usernameStrategy")
public class UsernameLoginStrategy implements LoginStrategy {

    private final UserRepository userRepository;

    public UsernameLoginStrategy(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public User loadUser(String username) {
        return userRepository.findByUsername(username);

    }
}