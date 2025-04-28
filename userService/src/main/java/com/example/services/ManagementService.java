package com.example.services;

import com.example.models.NotificationSettings;
import com.example.models.User;
import com.example.repositories.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashSet;

@Service
public class ManagementService {

    private final UserRepository userRepository;
    private final UserService userService;
    private static final Logger logger = LoggerFactory.getLogger(ManagementService.class);

    @Autowired
    public ManagementService(UserRepository userRepository, UserService userService) {
        this.userRepository = userRepository;
        this.userService = userService;
    }

    public void blockUser(Long userBlockingId, Long userToBlockId) {
        try {
            User userBlocking = userService.getUserById(userBlockingId);
            User userToBlock = userService.getUserById(userToBlockId);
            if (userBlocking.getBlockedUsers() == null) {
                userBlocking.setBlockedUsers(new HashSet<>());
            }
            userBlocking.getBlockedUsers().add(userToBlock);
            userRepository.save(userBlocking);
        } catch (Exception e) {
            logger.error("Error blocking user: {}", e.getMessage());
            throw e;
        }
    }

    public void unBlockUser(Long userBlockingId, Long userToUnBlockId) {
        try {
            User userBlocking = userService.getUserById(userBlockingId);
            User userToUnBlock = userService.getUserById(userToUnBlockId);
            if (userBlocking.getBlockedUsers() != null) {
                userBlocking.getBlockedUsers().remove(userToUnBlock);
            }
            userRepository.save(userBlocking);
        } catch (Exception e) {
            logger.error("Error unblocking user: {}", e.getMessage());
            throw e;
        }
    }

    public void muteNotifications(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        user.getNotificationSettings().setMuteNotifications(true);
        userRepository.save(user);
    }

    public void unmuteNotifications(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        user.getNotificationSettings().setMuteNotifications(false);
        userRepository.save(user);
    }

    public void updateNotificationSettings(Long userId, NotificationSettings updateDTO) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        NotificationSettings settings = user.getNotificationSettings();

        if (updateDTO.getMuteNotifications() != null) {
            settings.setMuteNotifications(updateDTO.getMuteNotifications());
        }
        if (updateDTO.getDirectMessageEmail() != null) {
            settings.setDirectMessageEmail(updateDTO.getDirectMessageEmail());
        }
        if (updateDTO.getDirectMessageInbox() != null) {
            settings.setDirectMessageInbox(updateDTO.getDirectMessageInbox());
        }
        if (updateDTO.getGroupMessageEmail() != null) {
            settings.setGroupMessageEmail(updateDTO.getGroupMessageEmail());
        }
        if (updateDTO.getGroupMessageInbox() != null) {
            settings.setGroupMessageInbox(updateDTO.getGroupMessageInbox());
        }
        if (updateDTO.getGroupMentionEmail() != null) {
            settings.setGroupMentionEmail(updateDTO.getGroupMentionEmail());
        }
        if (updateDTO.getGroupMentionInbox() != null) {
            settings.setGroupMentionInbox(updateDTO.getGroupMentionInbox());
        }

        userRepository.save(user);
    }
}



