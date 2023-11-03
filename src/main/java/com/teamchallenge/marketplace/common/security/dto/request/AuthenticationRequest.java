package com.teamchallenge.marketplace.common.security.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record AuthenticationRequest(
        @Email(message = "Invalid email")
        String email,
        @NotBlank(message = "Password cannot be null")
        String password) {
}
