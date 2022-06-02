package com.example.ksis_3.exception;

public class RoomIsFullException extends RuntimeException {
    public RoomIsFullException() {
        super();
    }

    public RoomIsFullException(String message) {
        super(message);
    }

    public RoomIsFullException(String message, Throwable cause) {
        super(message, cause);
    }
}
