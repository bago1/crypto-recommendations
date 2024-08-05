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

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.TimeZone;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<Map<String, String>> handleNotFoundException(NotFoundException e, WebRequest request) {
        return createErrorResponse(e.getMessage(), HttpStatus.NOT_FOUND, request);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, String>> handleIllegalArgumentException(IllegalArgumentException e, WebRequest request) {
        return createErrorResponse(e.getMessage(), HttpStatus.BAD_REQUEST, request);
    }

    private ResponseEntity<Map<String, String>> createErrorResponse(String message, HttpStatus status, WebRequest request) {
        String path = request.getDescription(false).substring(4);

        Map<String, String> body = Map.of(
                "message", message,
                "status", String.valueOf(status.value()),
                "error", status.getReasonPhrase(),
                "timestamp", new Date().toString(),
                "path", path
        );
        return new ResponseEntity<>(body, status);
    }
}
