package com.teamchallenge.marketplace.user.dto.response;

import java.util.UUID;

public record UserResponseDto(UUID reference,
                              String username,
                              String phoneNumber,
                              String email) {
}
