package org.example.notificationservice.services;

import lombok.RequiredArgsConstructor;
import org.example.notificationservice.models.NotificationStrategyType;
import org.example.notificationservice.models.NotificationType;
import org.example.notificationservice.models.UserNotificationSettings;
import org.example.notificationservice.repositories.UserNotificationSettingsRepository;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class NotificationSettingsService {
    private final UserNotificationSettingsRepository userNotificationSettingsRepository;
    
    public UserNotificationSettings getUserNotificationSettings(String userId) {
        return userNotificationSettingsRepository.findByUserId(userId)
                .orElseGet(() -> createDefaultSettings(userId));
    }
    
    public boolean isNotificationEnabled(String userId, String notificationType, String strategyType) {
        UserNotificationSettings settings = getUserNotificationSettings(userId);
        return settings.isNotificationEnabled(notificationType, strategyType);
    }
    
    public void enableNotification(String userId, String notificationType, String strategyType) {
        UserNotificationSettings settings = getUserNotificationSettings(userId);
        settings.setNotificationEnabled(notificationType, strategyType, true);
        userNotificationSettingsRepository.save(settings);
    }
    
    public void disableNotification(String userId, String notificationType, String strategyType) {
        UserNotificationSettings settings = getUserNotificationSettings(userId);
        settings.setNotificationEnabled(notificationType, strategyType, false);
        userNotificationSettingsRepository.save(settings);
    }
    
    public void muteAllNotifications(String userId) {
        UserNotificationSettings settings = getUserNotificationSettings(userId);
        settings.muteAllNotifications();
        userNotificationSettingsRepository.save(settings);
    }
    
    public void unmuteAllNotifications(String userId) {
        UserNotificationSettings settings = getUserNotificationSettings(userId);
        settings.unmuteAllNotifications();
        userNotificationSettingsRepository.save(settings);
    }
    
    private UserNotificationSettings createDefaultSettings(String userId) {
        UserNotificationSettings settings = UserNotificationSettings.builder()
                .userId(userId)
                .build();
                
        // Initialize all notification types and strategies to true by default
        Map<String, Map<String, Boolean>> defaultStrategies = new HashMap<>();
        
        // Get all notification types and strategy types from their respective classes
        List<String> notificationTypes = NotificationType.getAllTypes();
        List<String> strategyTypes = NotificationStrategyType.getAllTypes();
        
        // Initialize all combinations to true
        for (String notificationType : notificationTypes) {
            Map<String, Boolean> strategies = new HashMap<>();
            for (String strategyType : strategyTypes) {
                strategies.put(strategyType, true);
            }
            defaultStrategies.put(notificationType, strategies);
        }
        
        settings.setNotificationStrategy(defaultStrategies);
        return userNotificationSettingsRepository.save(settings);
    }
}
