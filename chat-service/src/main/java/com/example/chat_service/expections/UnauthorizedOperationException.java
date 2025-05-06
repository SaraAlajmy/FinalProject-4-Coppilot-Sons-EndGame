package com.example.chat_service.expections;

public class UnauthorizedOperationException extends RuntimeException {

    public UnauthorizedOperationException(String message) {
        super(message);
    }

}
