package com.example.services.loginStrategies;

import com.example.models.User;
import com.example.repositories.UserRepository;
import org.springframework.stereotype.Component;

@Component("phoneStrategy")
public class PhoneLoginStrategy implements LoginStrategy {

    private final UserRepository userRepository;

    public PhoneLoginStrategy(UserRepository userRepository) {
        this.userRepository = userRepository;
    }


    @Override
    public User loadUser(String phoneNumber) {
        User user = userRepository.findByPhoneNumber(phoneNumber);
        return user;
    }
}
