package com.teamchallenge.marketplace.user.dto.request;

import com.teamchallenge.marketplace.common.util.Constants;
import com.teamchallenge.marketplace.common.validation.phone.ValidUkrainianPhoneNumber;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record UserRequestDto(
        @NotBlank(message = "Username cannot be empty")
        @Size(min = 4, max = 30, message = "Username must be between 4 and 30")
        String username,
        @NotBlank(message = "Email cannot be empty")
        @Email(regexp = Constants.EMAIL_REGEXP,
                flags = Pattern.Flag.CASE_INSENSITIVE, message = "Invalid email")
        String email,
        @ValidUkrainianPhoneNumber
        String phoneNumber,
        @Size(min = 8, message = "Password min length is 8")
        String password) {
}
