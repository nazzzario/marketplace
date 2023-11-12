package com.teamchallenge.marketplace.password.dto.request;

import jakarta.validation.constraints.Size;

public record PasswordResetRequestDto(
        @Size(min = 4, message = "Password min length is 4")
        String newPassword) {
}
