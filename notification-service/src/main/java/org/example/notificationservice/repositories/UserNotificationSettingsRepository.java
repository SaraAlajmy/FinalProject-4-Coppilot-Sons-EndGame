package org.example.notificationservice.repositories;

import org.example.notificationservice.models.UserNotificationSettings;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserNotificationSettingsRepository extends MongoRepository<UserNotificationSettings, String> {
    Optional<UserNotificationSettings> findByUserId(String userId);
}
