package com.example.ksis_3.exceptionhandler;

import org.springframework.http.HttpStatus;

public class ExceptionInfo {

    private String message;

    private HttpStatus status;

    public ExceptionInfo(String message, HttpStatus status) {
        this.message = message;
        this.status = status;
    }

    public HttpStatus getStatus() {
        return status;
    }

    public void setStatus(HttpStatus status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
