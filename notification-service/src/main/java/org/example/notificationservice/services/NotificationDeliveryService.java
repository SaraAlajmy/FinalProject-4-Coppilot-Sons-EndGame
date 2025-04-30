package org.example.notificationservice.services;

import jakarta.annotation.PostConstruct;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.notificationservice.models.Notification;
import org.example.notificationservice.strategies.NotificationStrategy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationDeliveryService {
    private final List<NotificationStrategy> notificationStrategies;
    private Map<String, NotificationStrategy> strategyMap;

    @PostConstruct
    public void initStrategiesMap() {
        this.strategyMap = new HashMap<>();
        for (NotificationStrategy strategy : notificationStrategies) {
            strategyMap.put(strategy.getStrategyType(), strategy);
        }
    }

    public boolean deliverNotificationUsingStrategy(
        Notification notification,
        String strategyType
    ) {
        return strategyMap.get(strategyType).deliver(notification);
    }
}
