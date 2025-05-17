package org.example.shared.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ResetPasswordNotificationDTO extends NotificationDTO {
    private String resetPasswordLink;
    private String recipientName;

    public ResetPasswordNotificationDTO(
        String recipientUserId,
        String resetPasswordLink,
        String recipientName
    ) {
        super(recipientUserId, NotificationType.RESET_PASSWORD);
        this.resetPasswordLink = resetPasswordLink;
        this.recipientName = recipientName;
    }
}

