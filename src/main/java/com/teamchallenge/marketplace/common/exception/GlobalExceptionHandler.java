package com.teamchallenge.marketplace.common.exception;

import com.teamchallenge.marketplace.common.exception.dto.ExceptionResponseDto;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.stream.Collectors;

@Slf4j
@RestControllerAdvice
@RequiredArgsConstructor
public class GlobalExceptionHandler {

    private final HttpServletRequest httpServletRequest;

    @ExceptionHandler(ClientBackendException.class)
    public ResponseEntity<ExceptionResponseDto> handleClientException(ClientBackendException ex, HttpServletRequest request) {
        ErrorCode.ErrorData errorData = ex.getErrorCode().getErrorData();
        ExceptionResponseDto errorResponse = ExceptionResponseDto.builder()
                .time(LocalDateTime.now().toString())
                .errorCode(errorData.getCode())
                .title(ex.getClass().getName())
                .message(errorData.getDescription())
                .httpResponseCode(errorData.getHttpResponseCode())
                .path(request.getRequestURI())
                .build();

        return new ResponseEntity<>(errorResponse, HttpStatusCode.valueOf(errorData.getHttpResponseCode()));

    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ExceptionResponseDto> handleAccessException(AccessDeniedException ex, HttpServletRequest request){
        ExceptionResponseDto errorResponse = ExceptionResponseDto.builder()
                .time(LocalDateTime.now().toString())
                .errorCode(null)
                .title(ex.getClass().getName())
                .message(ex.getMessage())
                .httpResponseCode(403)
                .path(request.getRequestURI())
                .build();

        return new ResponseEntity<>(errorResponse, HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<ExceptionResponseDto> handleAuthException(AuthenticationException ex, HttpServletRequest request){
        ExceptionResponseDto errorResponse = ExceptionResponseDto.builder()
                .time(LocalDateTime.now().toString())
                .errorCode(null)
                .title(ex.getClass().getName())
                .message(ex.getMessage())
                .httpResponseCode(401)
                .path(request.getRequestURI())
                .build();

        return new ResponseEntity<>(errorResponse, HttpStatus.UNAUTHORIZED);
    }

    // TODO: 11/1/23 add more specific exception handling 
    @ExceptionHandler({IllegalArgumentException.class, HttpMessageNotReadableException.class})
    public ResponseEntity<ExceptionResponseDto> handleIllegalArgumentException(RuntimeException ex, HttpServletRequest request){
        ExceptionResponseDto errorResponse = ExceptionResponseDto.builder()
                .time(LocalDateTime.now().toString())
                .errorCode(null)
                .title(ex.getClass().getName())
                .message(ex.getMessage())
                .httpResponseCode(400)
                .path(request.getRequestURI())
                .build();

        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }


    @ExceptionHandler(Exception.class)
    public ResponseEntity<ExceptionResponseDto> handleExceptions(Exception ex, HttpServletRequest request) {
        ExceptionResponseDto errorResponse = ExceptionResponseDto.builder()
                .time(LocalDateTime.now().toString())
                .errorCode(null)
                .title(ex.getClass().getName())
                .message("Unhandled exception")
                .httpResponseCode(500)
                .path(request.getRequestURI())
                .build();

        return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ExceptionResponseDto> handleValidationExceptions(MethodArgumentNotValidException ex, HttpServletRequest request) {
        String errorMessage = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(DefaultMessageSourceResolvable::getDefaultMessage)
                .collect(Collectors.joining("\n"));

        ExceptionResponseDto errorResponse = ExceptionResponseDto.builder()
                .time(LocalDateTime.now().toString())
                .errorCode(null)
                .title("Validation exception")
                .message(errorMessage)
                .httpResponseCode(400)
                .path(request.getRequestURI())
                .build();

        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }
}
