package com.teamchallenge.marketplace.password.service.impl;

import com.teamchallenge.marketplace.common.email.service.EmailService;
import com.teamchallenge.marketplace.common.exception.ClientBackendException;
import com.teamchallenge.marketplace.common.exception.ErrorCode;
import com.teamchallenge.marketplace.password.dto.request.PasswordResetRequestDto;
import com.teamchallenge.marketplace.password.dto.request.PasswordResetTokenRequestDto;
import com.teamchallenge.marketplace.password.service.PasswordService;
import com.teamchallenge.marketplace.user.persisit.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PasswordServiceImpl implements PasswordService {

    private final EmailService emailService;
    private final UserRepository userRepository;

    @Override
    @Transactional
    public void changeForgottenPassword(String resetToken, PasswordResetRequestDto requestDto) {

    }

    @Override
    public void sendResetPasswordToken(PasswordResetTokenRequestDto resetRequestDto) {
        String userEmail = resetRequestDto.email();
        if(!userRepository.existsByEmail(userEmail)){
            throw new ClientBackendException(ErrorCode.EMAIL_NOT_EXISTS);
        }

        // generating reset token with JWT
        // sawing reset token in Redis

        emailService.sendPasswordResetToken(userEmail, "<ADD PASSWORD RESET TOKEN>");
    }
}
