package org.example.notificationservice.strategies.email.stages;

import lombok.extern.slf4j.Slf4j;
import org.example.notificationservice.models.*;
import org.springframework.stereotype.Component;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

@Component
@Slf4j
public class ReplaceEmailSubjectPlaceholdersStage extends EmailNotificationChain {
    @Override
    public boolean executeStage(Notification notification, Context context) {
        log.info("Replace email subject placeholders...");

        String subject = context.getSubjectTemplate();
        Map<String, String> placeholders = getPlaceholders(notification);

        for (var entry : placeholders.entrySet()) {
            String placeholder = entry.getKey();
            String value = entry.getValue();
            subject = subject.replace("{{" + placeholder + "}}", value);
        }

        context.setSubject(subject);

        return true;
    }

    private Map<String, String> getPlaceholders(Notification notification) {
        Map<String, String> placeholders = new HashMap<>();

        if (notification instanceof MessageNotification messageNotification) {
            placeholders.put("senderUsername", messageNotification.getSenderUsername());
        }

        if (notification instanceof GroupNotification groupNotification) {
            placeholders.put("groupName", groupNotification.getGroupName());
        }

        return placeholders;
    }
}
