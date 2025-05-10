package com.example.chat_service.exceptions;

public class FavouriteMessageException extends RuntimeException {
    public FavouriteMessageException(String message) {
        super(message);
    }

    public FavouriteMessageException(String message, Throwable cause) {
        super(message, cause);
    }
} 