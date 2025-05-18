package com.example.services;

import com.example.models.User;
import com.example.repositories.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.UUID;

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

    public void blockUser(UUID userBlockingId, UUID userToBlockId) {
        try {
            User userBlocking = userService.getUserById(userBlockingId);
            User userToBlock = userService.getUserById(userToBlockId);

            if (userToBlock == null) {
                throw new NoSuchElementException("User to block does not exist.");
            }

            if (userBlocking == null) {
                throw new NoSuchElementException("Blocking user does not exist.");
            }

            // Avoid blocking the same user multiple times
            if (userBlocking.getBlockedUsers() == null) {
                userBlocking.setBlockedUsers(new HashSet<>());
            }

            if (userBlocking.getBlockedUsers().contains(userToBlock)) {
                logger.warn("User {} already blocked user {}", userBlockingId, userToBlockId);
                return;
            }

            userBlocking.getBlockedUsers().add(userToBlock);
            userRepository.save(userBlocking);

            logger.info("User {} successfully blocked user {}", userBlockingId, userToBlockId);

        } catch (Exception e) {
            logger.error("Error blocking user {} by {}: {}", userToBlockId, userBlockingId, e.getMessage());
            throw e;
        }

    }


    public void unBlockUser(UUID userBlockingId, UUID userToUnBlockId) {
        try {
            User userBlocking = userService.getUserById(userBlockingId);
            User userToUnBlock = userService.getUserById(userToUnBlockId);

            if (userBlocking == null) {
                throw new NoSuchElementException("Blocking user does not exist.");
            }

            if (userToUnBlock == null) {
                throw new NoSuchElementException("User to unblock does not exist.");
            }

            Set<User> blockedUsers = userBlocking.getBlockedUsers();
            if (blockedUsers != null && blockedUsers.contains(userToUnBlock)) {
                blockedUsers.remove(userToUnBlock);
                userRepository.save(userBlocking);
                logger.info("User {} unblocked user {}", userBlockingId, userToUnBlockId);
            } else {
                logger.warn("User {} had not blocked user {}", userBlockingId, userToUnBlockId);
            }

        } catch (Exception e) {
            logger.error("Error unblocking user {} by {}: {}", userToUnBlockId, userBlockingId, e.getMessage());
            throw e;
        }
    }


}