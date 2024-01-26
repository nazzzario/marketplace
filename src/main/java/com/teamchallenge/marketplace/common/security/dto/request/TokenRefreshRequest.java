package com.teamchallenge.marketplace.common.security.dto.request;

import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record TokenRefreshRequest(
        @NotNull
        UUID refreshToken) {
}
