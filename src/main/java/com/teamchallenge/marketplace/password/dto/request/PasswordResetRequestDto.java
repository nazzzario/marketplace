package com.teamchallenge.marketplace.password.dto.request;

import jakarta.validation.constraints.Size;

public record PasswordResetRequestDto(
        @Size(min = 8, message = "Password min length is 8")
        String newPassword) {
}
