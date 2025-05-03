package org.example.notificationservice.strategies.email.stages;

import lombok.Data;
import org.example.notificationservice.models.Notification;

@Data
public abstract class EmailNotificationChain {
    protected EmailNotificationChain nextStage;

    public void execute(Notification notification, Context context) {
        boolean shouldMoveToNextStage = executeStage(notification, context);

        if (!shouldMoveToNextStage) {
            return;
        }

        if (nextStage != null) {
            nextStage.execute(notification, context);
        }
    }

    public abstract boolean executeStage(Notification notification, Context context);

    public static EmailNotificationChain createChain(EmailNotificationChain... stages) {
        for (int i = 0; i < stages.length - 1; i++) {
            stages[i].setNextStage(stages[i + 1]);
        }
        return stages[0];
    }

    public static class Context {
    }
}
