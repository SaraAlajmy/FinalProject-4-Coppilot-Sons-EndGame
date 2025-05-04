package com.example.chat_service.services;


import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class MessageConfig {

    private final RealMessageService realMessageService;
    private final List<Observer> notificationObserver;

    @Autowired
    MessageConfig(RealMessageService realMessageService, List<Observer> observers) {
        this.realMessageService = realMessageService;
        this.notificationObserver = observers;
    }


    @PostConstruct
    public void subscribeObserver() {
        notificationObserver.forEach(realMessageService::addObserver);
    }


}
