package org.example.notificationservice.strategies.email.stages;

import lombok.extern.slf4j.Slf4j;
import org.example.notificationservice.models.Notification;
import org.springframework.stereotype.Component;
import org.springframework.util.ResourceUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;

@Component
@Slf4j
public class LoadEmailBodyTemplateStage extends EmailNotificationChain {
    @Override
    public boolean executeStage(Notification notification, Context context) {
        log.info("Load email body template...");

        String templatePath =
            "classpath:templates/email-bodies/" + notification.getType() + ".html";

        try {
            File file = ResourceUtils.getFile(templatePath);
            String template = Files.readString(file.toPath(), StandardCharsets.UTF_8);
            context.setBodyTemplate(template);
            return true;
        } catch (FileNotFoundException e) {
            log.error(
                "Email body template not found for notification type: {}",
                notification.getType()
            );
            return false;
        } catch (IOException e) {
            log.error(
                "Error reading email body template for notification type: {}",
                notification.getType(),
                e
            );
            return false;
        }
    }
}
