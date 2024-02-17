package com.teamchallenge.marketplace.password.service.impl;

import com.teamchallenge.marketplace.common.email.service.EmailService;
import com.teamchallenge.marketplace.common.exception.ClientBackendException;
import com.teamchallenge.marketplace.common.exception.ErrorCode;
import com.teamchallenge.marketplace.common.security.service.SecurityAttempts;
import com.teamchallenge.marketplace.password.dto.request.PasswordResetRequestDto;
import com.teamchallenge.marketplace.password.dto.request.PasswordResetTokenRequestDto;
import com.teamchallenge.marketplace.password.service.PasswordService;
import com.teamchallenge.marketplace.user.persisit.entity.UserEntity;
import com.teamchallenge.marketplace.user.persisit.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PasswordServiceImpl implements PasswordService {

    private static final String PASSWORD_RESET_TOKEN_PREFIX = "passwordResetToken:";
    private static final String LIMIT_RESET_PREFIX = "LimitReset_";
    private static final String EXCEPTION_RESET_PREFIX = "ExceptionReset_";
    private static final String EXCEPTION_SEND_RESET_PREFIX = "ExceptionSendReset_";

    @Value("${user.limitation}")
    private int limitation;
    @Value("${user.timeout}")
    private long timeout;

    private final EmailService emailService;
    private final UserRepository userRepository;
    private final SecurityAttempts attempts;
    private final RedisTemplate<String, Object> redisTemplate;
    private final PasswordEncoder passwordEncoder;


    @Override
    @Transactional
    public void changeForgottenPassword(String resetToken, PasswordResetRequestDto requestDto,
                                        String ip) {
        if (attempts.isAttemptExhausted(LIMIT_RESET_PREFIX, ip)) {
            throw new ClientBackendException(ErrorCode.ATTEMPTS_IS_EXHAUSTED);
        }

        attempts.incrementCounterAttempt(LIMIT_RESET_PREFIX, EXCEPTION_RESET_PREFIX, ip,
                limitation, timeout);

        String userEmailFromCache = (String) Optional.ofNullable(redisTemplate.opsForValue()
                        .get(PASSWORD_RESET_TOKEN_PREFIX + resetToken)).orElseThrow(() ->
                new ClientBackendException(ErrorCode.PASSWORD_RESET_TOKEN_NOT_EXISTS));

        UserEntity userByReference = userRepository.findByEmail(userEmailFromCache)
                .orElseThrow(() -> new ClientBackendException(ErrorCode.USER_NOT_FOUND));

        if (passwordEncoder.matches(requestDto.newPassword(), userByReference.getPassword())) {
            throw new ClientBackendException(ErrorCode.NEW_PASSWORD_SAME_AS_OLD_PASSWORD);
        }

        redisTemplate.delete(PASSWORD_RESET_TOKEN_PREFIX + resetToken);

        userByReference.setPassword(passwordEncoder.encode(requestDto.newPassword()));

        attempts.delete(EXCEPTION_RESET_PREFIX, ip);
        attempts.delete(EXCEPTION_SEND_RESET_PREFIX, ip);
    }

    @Override
    public void sendResetPasswordToken(PasswordResetTokenRequestDto resetRequestDto, String ip) {
        if (attempts.isAttemptExhausted(LIMIT_RESET_PREFIX, ip)) {
            throw new ClientBackendException(ErrorCode.ATTEMPTS_IS_EXHAUSTED);
        }

        attempts.incrementCounterAttempt(LIMIT_RESET_PREFIX, EXCEPTION_SEND_RESET_PREFIX, ip,
                limitation, timeout);

        String userEmail = resetRequestDto.email();
        if (!userRepository.existsByEmail(userEmail)) {
            throw new ClientBackendException(ErrorCode.EMAIL_NOT_EXISTS);
        }

        String passwordResetToken = UUID.randomUUID().toString();
        redisTemplate.opsForValue().set(PASSWORD_RESET_TOKEN_PREFIX + passwordResetToken,
                userEmail, Duration.ofMinutes(5));

        emailService.sendPasswordResetToken(userEmail, passwordResetToken);
    }
}
