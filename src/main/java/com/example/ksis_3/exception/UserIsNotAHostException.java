package com.example.ksis_3.exception;

public class UserIsNotAHostException extends RuntimeException {

    public UserIsNotAHostException() {
        super();
    }

    public UserIsNotAHostException(String message) {
        super(message);
    }

    public UserIsNotAHostException(String message, Throwable cause) {
        super(message, cause);
    }
}
