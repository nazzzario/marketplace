package com.teamchallenge.marketplace.password.dto.request;

import com.teamchallenge.marketplace.common.util.Constants;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;

public record PasswordResetTokenRequestDto(
        @Email(regexp = Constants.EMAIL_REGEXP,
                flags = Pattern.Flag.CASE_INSENSITIVE, message = "Invalid email")
        String email) {
}
