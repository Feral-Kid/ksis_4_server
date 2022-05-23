package com.example.ksis_3.exception;

public class RoomIsNotPresentException extends RuntimeException {

    public RoomIsNotPresentException() {
    }

    public RoomIsNotPresentException(String message) {
        super(message);
    }

    public RoomIsNotPresentException(String message, Throwable cause) {
        super(message, cause);
    }
}
