package org.example.notificationservice.strategies.email.stages;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.notificationservice.models.Notification;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
@Slf4j
@RequiredArgsConstructor
public class ReplaceEmailBodyPlaceholdersStage extends EmailNotificationChain {
    private final ObjectMapper mapper;

    @Override
    public boolean executeStage(Notification notification, Context context) {
        log.info("Replace email body placeholders...");

        String body = context.getBodyTemplate();

        for (var entry : getPlaceholders(notification).entrySet()) {
            String placeholder = entry.getKey();
            String value = entry.getValue();
            body = body.replace(placeholder, value);
        }

        context.setBody(body);

        return true;
    }
    private Map<String, String> getPlaceholders(Notification notification) {
        Map<String, String> placeholders = new HashMap<>();

        try {
            // Convert the notification object to a Map
            Map<String, Object> map = mapper.convertValue(notification, Map.class);

            // Convert all values to strings for the placeholders map
            map.forEach((key, value) -> {
                if (value != null) {
                    placeholders.put("{{" + key + "}}", value.toString());
                }
            });
        } catch (Exception e) {
            log.error("Failed to convert notification to map", e);
        }

        return placeholders;
    }
}
