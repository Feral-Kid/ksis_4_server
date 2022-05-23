package com.example.ksis_3.exception;

public class NotValidUUIDException extends RuntimeException {

    public NotValidUUIDException() {
        super();
    }

    public NotValidUUIDException(String message) {
        super(message);
    }

    public NotValidUUIDException(String message, Throwable cause) {
        super(message, cause);
    }
}
