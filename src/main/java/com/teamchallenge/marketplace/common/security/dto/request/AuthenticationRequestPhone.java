package com.teamchallenge.marketplace.common.security.dto.request;

import com.teamchallenge.marketplace.common.validation.phone.ValidUkrainianPhoneNumber;
import jakarta.validation.constraints.NotBlank;

public record AuthenticationRequestPhone(
        @ValidUkrainianPhoneNumber
        String phone,
        @NotBlank(message = "Password cannot be null")
        String password) {
}
