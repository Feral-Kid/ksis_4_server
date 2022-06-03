package com.example.ksis_3.exceptionhandler;

import com.example.ksis_3.exception.NotValidUUIDException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@Slf4j
public class GlobalControllerAdvice extends ResponseEntityExceptionHandler {

    @ExceptionHandler(NotValidUUIDException.class)
    public ResponseEntity<ExceptionInfo> handleNoSuchRecordException(NotValidUUIDException exception) {
        log.info(String.format("Handled exception - %s", exception), exception);
        ExceptionInfo info = new ExceptionInfo(exception.getMessage(), HttpStatus.NOT_FOUND);
        return new ResponseEntity<>(info, HttpStatus.NOT_FOUND);
    }
}
