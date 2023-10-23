package com.teamchallenge.marketplace.common.exception;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.util.LinkedCaseInsensitiveMap;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.Map;

@Slf4j
@RestControllerAdvice
@RequiredArgsConstructor
public class GlobalExceptionHandler {

    private final HttpServletRequest httpServletRequest;

    @ExceptionHandler(ClientBackendException.class)
    public ResponseEntity<Map<String, Object>> handleClientException(ClientBackendException ex, HttpServletRequest request) {
        Map<String, Object> response = new LinkedCaseInsensitiveMap<>();
        ErrorCode.ErrorData errorData = ex.getErrorCode().getErrorData();
        response.put("time", LocalDateTime.now());
        response.put("code", errorData.getCode());
        response.put("description", errorData.getDescription());
        response.put("httpResponseCode", errorData.getHttpResponseCode());
        response.put("path", request.getRequestURI());

        return new ResponseEntity<>(response, HttpStatusCode.valueOf(errorData.getHttpResponseCode()));

    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<Map<String, Object>> handleAccessException(AccessDeniedException ex, HttpServletRequest request){
        System.out.println("Access exception");

        Map<String, Object> response = new LinkedCaseInsensitiveMap<>();
        String errorData = ex.getMessage();
        response.put("time", LocalDateTime.now());
        response.put("code", 403);
        response.put("description", errorData);
        response.put("path", request.getRequestURI());

        return new ResponseEntity<>(response, HttpStatusCode.valueOf(403));
    }

    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<Map<String, Object>> handleAuthException(AuthenticationException ex, HttpServletRequest request){
        System.out.println("Authentication exception");
        Map<String, Object> response = new LinkedCaseInsensitiveMap<>();
        String errorData = ex.getMessage();
        response.put("time", LocalDateTime.now());
        response.put("code", 401);
        response.put("description", errorData);
        response.put("path", request.getRequestURI());

        return new ResponseEntity<>(response, HttpStatusCode.valueOf(401));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleExceptions(Exception ex, HttpServletRequest request) {
        Map<String, Object> response = new LinkedCaseInsensitiveMap<>();
        response.put("time", LocalDateTime.now());
        response.put("code", 500);
        response.put("description", "Unhandled exception");
        response.put("httpResponseCode", 500);
        response.put("path", request.getRequestURI());

        return new ResponseEntity<>(response, HttpStatusCode.valueOf(500));
    }
}
