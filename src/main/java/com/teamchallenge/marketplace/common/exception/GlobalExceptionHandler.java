package com.teamchallenge.marketplace.common.exception;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
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
    public ResponseEntity<Map<String, Object>> handleClientException(ClientBackendException ex, HttpServletRequest request){
        Map<String, Object> response = new LinkedCaseInsensitiveMap<>();
        ErrorCode.ErrorData errorData = ex.getErrorCode().getErrorData();
        response.put("time", LocalDateTime.now());
        response.put("code", errorData.getCode());
        response.put("description", errorData.getDescription());
        response.put("httpResponseCode", errorData.getHttpResponseCode());
        response.put("path", request.getRequestURI());

        return new ResponseEntity<>(response, HttpStatusCode.valueOf(errorData.getHttpResponseCode()));

    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleExceptions(Exception ex, HttpServletRequest request){
        Map<String, Object> response = new LinkedCaseInsensitiveMap<>();
        response.put("time", LocalDateTime.now());
        response.put("code", 500);
        response.put("description", "Unhandled exception");
        response.put("httpResponseCode", 500);
        response.put("path", request.getRequestURI());

        return new ResponseEntity<>(response, HttpStatusCode.valueOf(500));
    }
}
