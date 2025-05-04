package com.example.chat_service.services;

import com.example.chat_service.models.Message;

public interface MessageSubject {

    public void addObserver(Observer observer);

    public void removeObserver(Observer observer);

    public void notifyObservers(Message message);
}
