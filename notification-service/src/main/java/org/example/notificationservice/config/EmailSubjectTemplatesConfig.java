package org.example.notificationservice.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

import java.util.Map;

@Configuration
@PropertySource("classpath:templates/email-subjects.properties")
@ConfigurationProperties("email")
@Data
public class EmailSubjectTemplatesConfig {
    private Map<String, String> subjectTemplates;

    public String getSubjectTemplateForNotificationType(String type) {
        return subjectTemplates.get(type);
    }
}