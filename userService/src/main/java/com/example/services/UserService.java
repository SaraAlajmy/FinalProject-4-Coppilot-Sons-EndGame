package com.example.services;

import com.example.models.User;
import com.example.repositories.UserRepository;
import com.example.services.loginStrategies.LoginStrategy;
import com.example.services.loginStrategies.PhoneLoginStrategy;
import com.example.services.loginStrategies.UsernameLoginStrategy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class UserService {

    private final UserRepository userRepository;
    private static final Logger logger = LoggerFactory.getLogger(UserService.class);
    @Autowired
    private JWTService jwtService;


    @Autowired
    AuthenticationManager authManager;


    @Autowired
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User addUser(User user) {
        try {
            User addedUser = userRepository.save(user);
            logger.info("User added successfully: {}", addedUser);
            return addedUser;
        } catch (Exception e) {
            logger.error("Error adding user: {}", e.getMessage());
            throw e;
        }
    }

    public List<User> getAllUsers() {
        try {
            List<User> users = userRepository.findAll();
            logger.info("Retrieved all users successfully");
            return users;

        } catch (Exception e) {
            logger.error("Error retrieving all users: {}", e.getMessage());
            throw e;
        }
    }

    public User getUserById(Long id) {
        try {
            User user =  userRepository.findById(id).orElse(null);
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
        User existingUser = userRepository.findById(id).orElseThrow(() -> {
            logger.error("User with ID {} not found", id);
            return new RuntimeException("User not found");
        });

        existingUser.setUsername(user.getUsername() != null ? user.getUsername() : existingUser.getUsername());
        existingUser.setPassword(user.getPassword() != null ? user.getPassword() : existingUser.getPassword());

        User updated = userRepository.save(existingUser);
        logger.info("User with ID {} updated successfully", id);
        return updated;
    }

    public void deleteUser(Long id) {
        try {
            userRepository.deleteById(id);
            logger.info("User with ID {} deleted successfully", id);
        } catch (Exception e) {
            logger.error("Error deleting trip: {}", e.getMessage());
            throw e;
        }
    }

    public User getUserByUsername(String username) {
        try {
            return userRepository.findByUsername(username);
        } catch (Exception e) {
            logger.error("Error retrieving user by username: {}", e.getMessage());
            throw e;
        }
    }

    private BCryptPasswordEncoder encoder = new BCryptPasswordEncoder(12);

    public User register(User user) {
        user.setPassword(encoder.encode(user.getPassword()));
        userRepository.save(user);
        return user;
    }

    public Map<String,String> verify(String identifier, String password, String loginType) {
      LoginStrategy strategy= loginType.equals("phone")? new PhoneLoginStrategy(userRepository): new UsernameLoginStrategy(userRepository);
        User user = strategy.loadUser(identifier);

        if (user == null ) {
            logger.error("User not found or password mismatch");
            return null;
        }

        Authentication authentication = authManager.authenticate(new UsernamePasswordAuthenticationToken(user.getUsername(), password));
        if (authentication.isAuthenticated()) {
            String newAccessToken = jwtService.generateToken(user.getUsername(), user.getId());
            String newRefreshToken = jwtService.generateRefreshToken(user.getUsername());
            user.setRefreshToken(newRefreshToken);
            userRepository.save(user);
            return Map.of(
                    "accessToken", newAccessToken,
                    "refreshToken", newRefreshToken);
        } else {
            logger.error("User is not verified");
            return null;
        }

    }
    public String changePassword(User user,String newPassword) {
        user.setPassword(encoder.encode(newPassword));
        userRepository.save(user);
        return "Password changed successfully";
    }

    public Map<String, Object> validateToken(String token) {
        return jwtService.validateAndExtractClaims(token);
    }


    public Map<String, String> refreshToken(String incomingToken) {
        User user = userRepository.findByRefreshToken(incomingToken);
        if (user == null|| jwtService.isTokenExpired(incomingToken)) {
            logger.error("User not found or token expired");
            throw new RuntimeException("User not found or token expired");
        }

        String newAccessToken = jwtService.generateToken(user.getUsername(), user.getId());
        String newRefreshToken = jwtService.generateRefreshToken(user.getUsername());
        user.setRefreshToken(newRefreshToken);
        userRepository.save(user);
        logger.info("Tokens refreshed successfully");
        return Map.of(
                "accessToken", newAccessToken,
                "refreshToken", newRefreshToken
        );
    }

    public void logout(String userId) {
       User user = userRepository.findById(Long.valueOf(userId)).orElse(null);
        if (user != null) {
            user.setRefreshToken(null);
            userRepository.save(user);
            logger.info("User logged out successfully");
        } else {
            logger.error("User not found");
            throw new RuntimeException("User not found");
        }
    }
    //for sake of helping in testing
    public void deleteAllUsers() {
        try {
            userRepository.deleteAll();
            logger.info("All users deleted successfully");
        } catch (Exception e) {
            logger.error("Error deleting all users: {}", e.getMessage());
            throw e;
        }

    }

    public boolean isBlocked(Long userBlockingId, Long userToCheckId) {
        try {
            User userBlocking = getUserById(userBlockingId);
            User userToCheck = getUserById(userToCheckId);
            if (userBlocking.getBlockedUsers() != null) {
                return userBlocking.getBlockedUsers().contains(userToCheck);
            }
            return false;
        } catch (Exception e) {
            logger.error("Error checking block status: {}", e.getMessage());
            throw e;
        }
    }

    public String getUserEmail(Long userId){
        User user = userRepository.findById(userId).orElse(null);
        if (user != null) {
            return user.getEmail();
        } else {
            logger.error("User not found");
            throw new RuntimeException("User not found");
        }
    }

}
