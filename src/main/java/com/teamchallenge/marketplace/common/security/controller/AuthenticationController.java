package com.teamchallenge.marketplace.common.security.controller;

import com.teamchallenge.marketplace.common.exception.ClientBackendException;
import com.teamchallenge.marketplace.common.exception.ErrorCode;
import com.teamchallenge.marketplace.common.exception.GlobalExceptionHandler;
import com.teamchallenge.marketplace.common.exception.dto.ExceptionResponseDto;
import com.teamchallenge.marketplace.common.security.bean.UserAccount;
import com.teamchallenge.marketplace.common.security.dto.request.AuthenticationRequest;
import com.teamchallenge.marketplace.common.security.dto.request.AuthenticationRequestPhone;
import com.teamchallenge.marketplace.common.security.dto.request.TokenRefreshRequest;
import com.teamchallenge.marketplace.common.security.dto.response.AuthenticationResponse;
import com.teamchallenge.marketplace.common.security.service.JwtService;
import com.teamchallenge.marketplace.user.persisit.entity.UserEntity;
import com.teamchallenge.marketplace.user.persisit.repository.UserRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;
import java.util.concurrent.TimeUnit;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/public")
@Tag(name = "Authentication")
public class AuthenticationController {

    public static final String REFRESH_TOKEN_PREFIX = "RefreshToken";
    public static final String VALUE_ZERO = "0";
    public static final String X_FORWARDED_FOR = "X-FORWARDED-FOR";
    public static final String LIMIT_IP_PREFIX = "LimitIp_";

    @Value("${user.limitation}")
    private int limitation;
    @Value("${user.timeout}")
    private long timeout;

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final UserRepository userRepository;
    private final RedisTemplate<String, String> redisTemplate;

    @PostMapping("/auth")
    @Operation(summary = "Authenticate user", description = "Input user credentials to get JWT token and refresh token")

    @ApiResponse(responseCode = "200", description = "User authentication tokens returned",
            content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ExceptionResponseDto.class))})
    @ApiResponse(responseCode = "400", description = "Invalid input",
            content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ExceptionResponseDto.class))})
    @ApiResponse(responseCode = "401", description = "Invalid user credentials",
            content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ExceptionResponseDto.class))})
    @ApiResponse(responseCode = "403", description = "User already authenticated",
            content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ExceptionResponseDto.class))})
    @PreAuthorize("isAnonymous()")
    public ResponseEntity<AuthenticationResponse> authenticate(@Valid @RequestBody AuthenticationRequest request) {

        if (Boolean.parseBoolean(redisTemplate.opsForValue().get(GlobalExceptionHandler.LIMIT_EMAIL_PREFIX
                + request.email()))) {
            throw new ClientBackendException(ErrorCode.FORBIDDEN);
        }

        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.email(), request.password())
        );

        UserEntity userEntity = userRepository.findByEmail(request.email()).orElseThrow();
        String accessToken = jwtService.generateAccessToken(UserAccount.fromUserEntityToCustomUserDetails(userEntity));
        String refreshToken = jwtService.generateRefreshToken(userEntity.getEmail());

        return new ResponseEntity<>(new AuthenticationResponse(userEntity.getReference(), accessToken,
                refreshToken), HttpStatus.OK);
    }

    @PostMapping("/auth/phone")
    @Operation(summary = "Authenticate user", description = "Input user credentials to get JWT token and refresh token.")

    @ApiResponse(responseCode = "200", description = "User authentication token returned",
            content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ExceptionResponseDto.class))})
    @ApiResponse(responseCode = "400", description = "Invalid input",
            content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ExceptionResponseDto.class))})
    @ApiResponse(responseCode = "401", description = "Invalid user credentials",
            content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ExceptionResponseDto.class))})
    @ApiResponse(responseCode = "403", description = "User already authenticated",
            content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ExceptionResponseDto.class))})
    @PreAuthorize("isAnonymous()")
    public ResponseEntity<AuthenticationResponse> authenticate(@Valid @RequestBody AuthenticationRequestPhone request) {

        if (Boolean.parseBoolean(redisTemplate.opsForValue().get(GlobalExceptionHandler.LIMIT_EMAIL_PREFIX
                + request.phone()))) {
            throw new ClientBackendException(ErrorCode.FORBIDDEN);
        }

        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.phone(), request.password())
        );

        UserEntity userEntity = userRepository.findByPhoneNumber(request.phone()).orElseThrow();
        String accessToken = jwtService.generateAccessToken(UserAccount.fromUserEntityToCustomUserDetails(userEntity));
        String refreshToken = jwtService.generateRefreshToken(userEntity.getEmail());

        return new ResponseEntity<>(new AuthenticationResponse(userEntity.getReference(), accessToken, refreshToken), HttpStatus.OK);
    }

    @PostMapping("/refreshtoken")
    @Operation(summary = "Authenticate user", description = "Input user credentials to get JWT token and refresh token")

    @ApiResponse(responseCode = "200", description = "User authentication token returned",
            content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ExceptionResponseDto.class))})
    @ApiResponse(responseCode = "400", description = "Invalid input",
            content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ExceptionResponseDto.class))})
    @ApiResponse(responseCode = "401", description = "Invalid user credentials",
            content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ExceptionResponseDto.class))})
    @ApiResponse(responseCode = "403", description = "User already authenticated",
            content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ExceptionResponseDto.class))})
    public ResponseEntity<AuthenticationResponse> refreshtoken(@Valid @RequestBody TokenRefreshRequest token,
                                                               HttpServletRequest request) {

        String ip = Optional.ofNullable(request.getHeader(X_FORWARDED_FOR)).orElse(request.getRemoteAddr());

        if (Boolean.parseBoolean(redisTemplate.opsForValue().get(LIMIT_IP_PREFIX + ip))) {
            throw new ClientBackendException(ErrorCode.FORBIDDEN);
        }

        String email = jwtService.getEmailByRefreshToken(token.refreshToken().toString());

        if (email != null) {
            UserEntity userEntity = userRepository.findByEmail(email).orElseThrow(
                    () -> new ClientBackendException(ErrorCode.USER_NOT_FOUND));

            String accessToken = jwtService.generateAccessToken(UserAccount.fromUserEntityToCustomUserDetails(userEntity));
            String refreshToken = jwtService.generateRefreshToken(userEntity.getEmail());


            return new ResponseEntity<>(new AuthenticationResponse(userEntity.getReference(), accessToken, refreshToken),
                    HttpStatus.OK);
        } else {
            redisTemplate.opsForHash().increment(REFRESH_TOKEN_PREFIX, ip, 1);

            if (Integer.parseInt(Optional.ofNullable((String) redisTemplate.opsForHash().get(REFRESH_TOKEN_PREFIX, ip))
                    .orElse(VALUE_ZERO)) >= limitation) {
                redisTemplate.opsForValue().set(LIMIT_IP_PREFIX + ip, Boolean.TRUE.toString(), timeout, TimeUnit.MINUTES);
                redisTemplate.opsForHash().delete(REFRESH_TOKEN_PREFIX, ip);
            }

            throw new ClientBackendException(ErrorCode.INVALID_SEARCH_INPUT);
        }
    }
}
