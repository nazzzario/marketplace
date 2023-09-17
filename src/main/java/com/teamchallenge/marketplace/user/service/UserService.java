package com.teamchallenge.marketplace.user.service;

import com.teamchallenge.marketplace.user.dto.request.UserRequestDto;
import com.teamchallenge.marketplace.user.dto.response.UserResponseDto;

import java.util.UUID;

public interface UserService {
    UserResponseDto createUser(UserRequestDto requestDto);

    UserResponseDto getUserByReference(UUID reference);

    UserResponseDto getUserByPhoneNumber(String phoneNumber);
}
