package com.teamchallenge.marketplace.user.dto.request;

import jakarta.validation.constraints.Size;

public record UserPasswordRequestDto(
        @Size(min = 8, message = "Password min length is 8")
        String oldPassword,
        @Size(min = 8, message = "Password min length is 8")
        String newPassword) {
}
