package com.example.services;

import com.example.models.User;
import com.example.repositories.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {

    private final UserRepository UserRepository;
    private static final Logger logger = LoggerFactory.getLogger(UserService.class);

    @Autowired
    public UserService(UserRepository userRepository) {
        UserRepository = userRepository;
    }

    public User addUser(User user) {
        try {
            User addedUser = UserRepository.save(user);
            logger.info("User added successfully: {}", addedUser);
            return addedUser;
        } catch (Exception e) {
            logger.error("Error adding user: {}", e.getMessage());
            throw e;
        }
    }

    public List<User> getAllUsers() {
        try {
            List<User> users = UserRepository.findAll();
            logger.info("Retrieved all users successfully");
            return users;
        } catch (Exception e) {
            logger.error("Error retrieving all users: {}", e.getMessage());
            throw e;
        }
    }

    public User getUserById(Long id) {
        try {
            User user =  UserRepository.findById(id).orElse(null);
            if (user == null) {
                logger.warn("User with ID {} not found", id);
                return null;
            }
            logger.info("Retrieved user by ID {} successfully", id);
            return user;
        } catch (Exception e) {
            logger.error("Error retrieving user by ID: {}", e.getMessage());
            throw e;
        }
    }

    public User updateUser(Long id, User user) {
        User existingUser = UserRepository.findById(id).orElseThrow(() -> {
            logger.error("User with ID {} not found", id);
            return new RuntimeException("User not found");
        });

        existingUser.setUsername(user.getUsername() != null ? user.getUsername() : existingUser.getUsername());
        existingUser.setPasswordHash(user.getPasswordHash() != null ? user.getPasswordHash() : existingUser.getPasswordHash());
        existingUser.setNotificationSettings(user.getNotificationSettings() != null ? user.getNotificationSettings() : existingUser.getNotificationSettings());

        User updated = UserRepository.save(existingUser);
        logger.info("User with ID {} updated successfully", id);
        return updated;
    }

    public void deleteUser(Long id) {
        try {
            UserRepository.deleteById(id);
            logger.info("User with ID {} deleted successfully", id);
        } catch (Exception e) {
            logger.error("Error deleting trip: {}", e.getMessage());
            throw e;
        }
    }
}
