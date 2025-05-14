package org.example.shared.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class ResetPasswordNotificationDTO extends NotificationDTO {
    private String resetPasswordLink;
    private String recipientName;
}

