package com.example.ksis_3.exception;

public class ChatMessageBinderException extends RuntimeException {

    public ChatMessageBinderException() {
        super();
    }

    public ChatMessageBinderException(String message) {
        super(message);
    }

    public ChatMessageBinderException(String message, Throwable cause) {
        super(message, cause);
    }
}
