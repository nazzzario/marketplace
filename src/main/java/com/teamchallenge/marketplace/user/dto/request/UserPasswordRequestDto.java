package com.teamchallenge.marketplace.user.dto.request;

import jakarta.validation.constraints.Size;

public record UserPasswordRequestDto(
        @Size(min = 8, max = 15, message = "Password min length is 8 and max length is 15")
        String oldPassword,
        @Size(min = 8, max = 15, message = "Password min length is 8 and max length is 15")
        String newPassword) {
}
