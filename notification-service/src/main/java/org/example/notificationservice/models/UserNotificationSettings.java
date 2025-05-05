package org.example.notificationservice.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.HashMap;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "user_notification_settings")
public class UserNotificationSettings {
    @Id
    private String userId;
    
    @Builder.Default
    private Map<String, Map<String, Boolean>> notificationStrategy = new HashMap<>();
    
    public boolean isNotificationEnabled(String notificationType, String strategyType) {
        Map<String, Boolean> typeStrategies = notificationStrategy.get(notificationType);
        if (typeStrategies == null) {
            return true; // Default to enabled if not specified
        }
        
        Boolean enabled = typeStrategies.get(strategyType);
        return enabled == null || enabled; // Default to enabled if not specified
    }
    
    public void setNotificationEnabled(String notificationType, String strategyType, boolean enabled) {
        notificationStrategy.computeIfAbsent(notificationType, k -> new HashMap<>())
                .put(strategyType, enabled);
    }
    
    public void muteAllNotifications() {
        setAllNotificationsState(false);
    }
    
    public void unmuteAllNotifications() {
        setAllNotificationsState(true);
    }
    
    private void setAllNotificationsState(boolean enabled) {
        // Loop through all possible notification types
        for (String notificationType : NotificationType.getAllTypes()) {
            // Get or create the strategies map for this type
            Map<String, Boolean> typeStrategies = notificationStrategy.computeIfAbsent(
                    notificationType, k -> new HashMap<>());
            
            // Loop through all possible strategy types
            for (String strategyType : NotificationStrategyType.getAllTypes()) {
                typeStrategies.put(strategyType, enabled);
            }
        }
    }
}
