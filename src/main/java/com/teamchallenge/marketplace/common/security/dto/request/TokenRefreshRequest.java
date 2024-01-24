package com.teamchallenge.marketplace.common.security.dto.request;

import jakarta.validation.constraints.NotBlank;

public record TokenRefreshRequest(
        @NotBlank(message = "Token cannot be null")
        String refreshToken) {
}
