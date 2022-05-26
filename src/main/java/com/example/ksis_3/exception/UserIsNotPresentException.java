package com.example.ksis_3.exception;

public class UserIsNotPresentException extends RuntimeException {
    public UserIsNotPresentException() {
        super();
    }

    public UserIsNotPresentException(String message) {
        super(message);
    }

    public UserIsNotPresentException(String message, Throwable cause) {
        super(message, cause);
    }
}
