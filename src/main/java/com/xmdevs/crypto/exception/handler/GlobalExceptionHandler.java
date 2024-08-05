package com.xmdevs.crypto.exception.handler;

import com.xmdevs.crypto.exception.NotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

import java.sql.Time;
import java.util.Date;
import java.util.Map;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<Map<String, String>> handleNotFoundException(NotFoundException e, WebRequest request) {
        String path = request.getDescription(false).substring(4);

        Map<String, String> body = Map.of(
                "message", e.getMessage(),
                "status", "404",
                "error", "Not Found",
                "timestamp", new Date().toString(),
                "path", path
        );
        return new ResponseEntity<>(body, HttpStatus.NOT_FOUND);
    }
}

