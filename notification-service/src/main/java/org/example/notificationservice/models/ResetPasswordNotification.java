package org.example.notificationservice.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.springframework.data.annotation.TypeAlias;
import java.time.LocalDateTime;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@TypeAlias(NotificationType.RESET_PASSWORD)
public class ResetPasswordNotification extends Notification {
    private String resetPasswordLink;
    private String recipientName;
}

