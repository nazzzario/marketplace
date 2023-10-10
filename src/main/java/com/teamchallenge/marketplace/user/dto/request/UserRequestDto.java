package com.teamchallenge.marketplace.user.dto.request;

public record UserRequestDto(String username,
                             String email,
                             String phoneNumber,
                             String password) {
}
