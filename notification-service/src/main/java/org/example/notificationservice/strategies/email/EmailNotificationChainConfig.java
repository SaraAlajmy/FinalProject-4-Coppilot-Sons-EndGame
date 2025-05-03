package org.example.notificationservice.strategies.email;

import org.example.notificationservice.strategies.email.stages.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

@Configuration
public class EmailNotificationChainConfig {
    @Bean
    @Primary
    public EmailNotificationChain getEmailNotificationChain(
        LoadConfigStage loadConfigStage,
        LoadEmailSubjectTemplateStage loadEmailSubjectTemplateStage,
        LoadEmailBodyTemplateStage loadEmailBodyTemplateStage,
        ReplaceEmailSubjectPlaceholdersStage replaceEmailSubjectPlaceholdersStage,
        ReplaceEmailBodyPlaceholdersStage replaceEmailBodyPlaceholdersStage,
        SendEmailStage sendEmailStage
    ) {
        return EmailNotificationChain.createChain(
            loadConfigStage,
            loadEmailSubjectTemplateStage,
            loadEmailBodyTemplateStage,
            replaceEmailSubjectPlaceholdersStage,
            replaceEmailBodyPlaceholdersStage,
            sendEmailStage
        );
    }
}
