package com.teamchallenge.marketplace.common.security.dto.request;

import com.teamchallenge.marketplace.common.util.Constants;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record AuthenticationRequest(
        @Email(regexp = Constants.EMAIL_REGEXP,
                flags = Pattern.Flag.CASE_INSENSITIVE, message = "Invalid email")
        String email,
        @NotBlank(message = "Password cannot be null")
        String password) {
}
