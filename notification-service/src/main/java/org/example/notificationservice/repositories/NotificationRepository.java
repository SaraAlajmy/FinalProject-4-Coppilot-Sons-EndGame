package org.example.notificationservice.repositories;

import org.example.notificationservice.models.Notification;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface NotificationRepository extends MongoRepository<Notification, String> {
    List<Notification> findByRecipientUserIdAndIsRead(String userId, boolean isRead);
    List<Notification> findByRecipientUserId(String recipientUserId);



}
