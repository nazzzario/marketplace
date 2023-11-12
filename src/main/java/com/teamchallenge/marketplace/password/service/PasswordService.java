package com.teamchallenge.marketplace.password.service;

import com.teamchallenge.marketplace.password.dto.request.PasswordResetRequestDto;
import com.teamchallenge.marketplace.password.dto.request.PasswordResetTokenRequestDto;

public interface PasswordService {

    void changeForgottenPassword(String resetToken, PasswordResetRequestDto requestDto);

    void sendResetPasswordToken(PasswordResetTokenRequestDto resetRequestDto);
}
