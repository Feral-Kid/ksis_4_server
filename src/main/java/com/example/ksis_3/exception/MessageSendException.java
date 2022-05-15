package com.example.ksis_3.exception;

public class MessageSendException extends RuntimeException {

    public MessageSendException() {
    }

    public MessageSendException(String message) {
        super(message);
    }

    public MessageSendException(String message, Throwable cause) {
        super(message, cause);
    }
}
