package com.example.services;

import com.example.clients.EmailClient;
import com.example.models.EmailRequest;
import com.example.models.User;
import com.example.repositories.UserRepository;
import com.example.services.loginStrategies.LoginStrategy;
import com.example.services.loginStrategies.PhoneLoginStrategy;
import com.example.services.loginStrategies.UsernameLoginStrategy;
import org.example.shared.Utils;
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
import java.util.UUID;

@Service
public class UserService {

    private final UserRepository userRepository;
    private static final Logger logger = LoggerFactory.getLogger(UserService.class);
    @Autowired
    private JWTService jwtService;


    @Autowired
    AuthenticationManager authManager;

    @Autowired
    private EmailClient emailClient;

    @Autowired
    private TokenBlacklistService tokenBlacklistService;


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

    public User getUserById(UUID id) {
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

    public User updateUser(UUID id, User user) {
        User existingUser = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if(user.getId() != null && !user.getId().equals(existingUser.getId())) {
            throw new RuntimeException("User ID mismatch");
        }

        Utils.copyPropertiesWithReflection(user, existingUser);

        return userRepository.save(existingUser);
    }

    public void deleteUser(UUID id, String token) {

        try {
            userRepository.deleteById(id);
            logger.info("User with ID {} deleted successfully", id);
        } catch (Exception e) {
            logger.error("Error deleting trip: {}", e.getMessage());
            throw e;
        }

        try{
            tokenBlacklistService.blacklist(token);
            logger.info("Token blacklisted successfully");
        } catch (Exception e) {
            logger.error("Error logging out user: {}", e.getMessage());
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

    public void logout(UUID userId, String token) {
       User user = userRepository.findById(userId).orElse(null);
        if (user != null) {
            user.setRefreshToken(null);
            userRepository.save(user);
            tokenBlacklistService.blacklist(token);
            logger.info("User logged out successfully");
        } else {
            logger.error("User not found");
            throw new RuntimeException("User not found");
        }
    }
    public void forgotPassword(String identifier, String loginType) {
        LoginStrategy strategy= loginType.equals("phone")? new PhoneLoginStrategy(userRepository): new UsernameLoginStrategy(userRepository);
        User user = strategy.loadUser(identifier);

        if (user == null) {
            logger.error("User not found");
            throw new RuntimeException("User not found");
        }

        String resetToken = jwtService.generateResetToken(user.getUsername());

        emailClient.sendEmail(resetToken, user.getEmail(), user.getUsername());

        logger.info("Password reset token generated successfully");
    }
    public String resetPassword(String token, String newPassword) {
        System.out.print(token);
        Map<String, Object> claims = jwtService.validateResetToken(token);
        String username = (String) claims.get("username");
        User user = userRepository.findByUsername(username);

        if (user == null) {
            logger.error("User not found");
            throw new RuntimeException("User not found");
        }

        user.setPassword(encoder.encode(newPassword));
        userRepository.save(user);
        logger.info("Password reset successfully");
        return "Password reset successfully";
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

    public boolean isBlocked(UUID userBlockingId, UUID userToCheckId) {
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

    public String getUserEmail(UUID userId){
        User user = userRepository.findById(userId).orElse(null);
        if (user != null) {
            return user.getEmail();
        } else {
            logger.error("User not found");
            throw new RuntimeException("User not found");
        }
    }

}
