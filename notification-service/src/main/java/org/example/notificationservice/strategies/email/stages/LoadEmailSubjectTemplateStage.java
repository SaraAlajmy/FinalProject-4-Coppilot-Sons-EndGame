package org.example.notificationservice.strategies.email.stages;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.notificationservice.config.EmailSubjectTemplatesConfig;
import org.example.notificationservice.models.Notification;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class LoadEmailSubjectTemplateStage extends EmailNotificationChain {
    private final EmailSubjectTemplatesConfig templatesConfig;

    @Override
    public boolean executeStage(Notification notification, Context context) {
        log.info("Load email subject template...");
        System.out.println(notification.getType());
        String template =
            templatesConfig.getSubjectTemplateForNotificationType(notification.getType());

        if (template == null) {
            log.error(
                "No subject template found for notification type: {}",
                notification.getType()
            );
            return false;
        }

        context.setSubjectTemplate(template);

        return true;
    }
}
