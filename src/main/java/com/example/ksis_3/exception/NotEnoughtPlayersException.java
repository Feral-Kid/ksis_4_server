package com.example.ksis_3.exception;

public class NotEnoughtPlayersException extends RuntimeException {
    public NotEnoughtPlayersException() {
        super();
    }

    public NotEnoughtPlayersException(String message) {
        super(message);
    }

    public NotEnoughtPlayersException(String message, Throwable cause) {
        super(message, cause);
    }
}
