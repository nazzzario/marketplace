package com.teamchallenge.marketplace.common.exception;

import com.teamchallenge.marketplace.common.exception.dto.ExceptionResponseDto;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.cloudinary.json.JSONException;
import org.cloudinary.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.io.BufferedReader;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Slf4j
@RestControllerAdvice
@RequiredArgsConstructor
public class GlobalExceptionHandler {

    private static final String EXCEPTION_CAUSED_BY_CLASS = "Exception caused by class: {}";
    public static final String AUTHENTICATION_EXCEPTION_PREFIX = "AuthenticationException_";
    private static final String VALUE_ZERO = "0";
    public static final String LIMIT_EMAIL_PREFIX = "LimitEmail_";

    @Value("${user.limitation}")
    private int limitation;
    @Value("${user.timeout}")
    private long timeout;

    private final HttpServletRequest httpServletRequest;
    private final RedisTemplate<String, String> redisTemplate;

    @ExceptionHandler(ClientBackendException.class)
    public ResponseEntity<ExceptionResponseDto> handleClientException(ClientBackendException ex, HttpServletRequest request) {
        log.error(EXCEPTION_CAUSED_BY_CLASS, ex.getClass().getName(), ex);
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
        log.error(EXCEPTION_CAUSED_BY_CLASS, ex.getClass().getName(), ex);
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
        log.error(EXCEPTION_CAUSED_BY_CLASS, ex.getClass().getName(), ex);
        redisTemplate.opsForHash().increment(AUTHENTICATION_EXCEPTION_PREFIX, getEmailOrPhone(request), 1);

        if (Integer.parseInt(Optional.ofNullable((String) redisTemplate.opsForHash().get(AUTHENTICATION_EXCEPTION_PREFIX,
                getEmailOrPhone(request))).orElse(VALUE_ZERO)) >= limitation) {
            redisTemplate.opsForValue().set(LIMIT_EMAIL_PREFIX, Boolean.TRUE.toString(), timeout, TimeUnit.MINUTES);
            redisTemplate.opsForHash().delete(AUTHENTICATION_EXCEPTION_PREFIX, getEmailOrPhone(request));
        }

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

    private String getEmailOrPhone(HttpServletRequest request) {
        StringBuilder jb = new StringBuilder();
        String line;
        try {
            BufferedReader reader = request.getReader();
            while ((line = reader.readLine()) != null)
                jb.append(line);
        } catch (Exception e) { throw new ClientBackendException(ErrorCode.FORBIDDEN); }

        try {
            JSONObject jsonObject = new JSONObject(jb.toString());
            return Optional.ofNullable(jsonObject.getString("email"))
                    .orElse(jsonObject.getString("phone"));
        } catch (JSONException e) {
            throw new ClientBackendException(ErrorCode.FORBIDDEN);
        }
    }

    // TODO: 11/1/23 add more specific exception handling 
    @ExceptionHandler({IllegalArgumentException.class, HttpMessageNotReadableException.class})
    public ResponseEntity<ExceptionResponseDto> handleIllegalArgumentException(RuntimeException ex, HttpServletRequest request){
        log.error(EXCEPTION_CAUSED_BY_CLASS, ex.getClass().getName(), ex);
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
        log.error(EXCEPTION_CAUSED_BY_CLASS, ex.getClass().getName(), ex);
        ExceptionResponseDto errorResponse = ExceptionResponseDto.builder()
                .time(LocalDateTime.now().toString())
                .errorCode(null)
                .title(ex.getClass().getName())
                .message("Unhandled exception: " + ex.getMessage())
                .httpResponseCode(500)
                .path(request.getRequestURI())
                .build();

        return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ExceptionResponseDto> handleValidationExceptions(MethodArgumentNotValidException ex, HttpServletRequest request) {
        log.error(EXCEPTION_CAUSED_BY_CLASS, ex.getClass().getName(), ex);
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
