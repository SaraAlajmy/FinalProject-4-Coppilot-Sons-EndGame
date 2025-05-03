package org.example.notificationservice.models;

import java.util.List;
import java.util.Arrays;

public class NotificationType {
    public static final String GROUP_MESSAGE = "group_message";
    public static final String GROUP_MENTION = "group_mention";
    public static final String DIRECT_MESSAGE = "direct_message";
    
    /**
     * Returns a list of all notification types
     */
    public static List<String> getAllTypes() {
        return Arrays.asList(
            GROUP_MESSAGE,
            GROUP_MENTION,
            DIRECT_MESSAGE
        );
    }
}
