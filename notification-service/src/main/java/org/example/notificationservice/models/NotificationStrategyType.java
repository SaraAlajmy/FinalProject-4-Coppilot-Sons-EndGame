package org.example.notificationservice.models;

import java.util.List;
import java.util.Arrays;

public class NotificationStrategyType {
    public static final String INBOX = "inbox"; // Adds the notification to the database
    public static final String EMAIL = "email";
    
    /**
     * Returns a list of all notification strategy types
     */
    public static List<String> getAllTypes() {
        return Arrays.asList(
            INBOX,
            EMAIL
        );
    }
}
