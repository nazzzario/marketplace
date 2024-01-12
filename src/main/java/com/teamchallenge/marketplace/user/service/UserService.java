package com.teamchallenge.marketplace.user.service;

import com.teamchallenge.marketplace.user.dto.request.UserPatchRequestDto;
import com.teamchallenge.marketplace.user.dto.request.UserRequestDto;
import com.teamchallenge.marketplace.user.dto.response.UserResponseDto;
import com.teamchallenge.marketplace.user.persisit.entity.UserEntity;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.UUID;

public interface UserService {
    UserResponseDto userRegistration(UserRequestDto requestDto);

    UserResponseDto getUserByReference(UUID reference);

    UserResponseDto getUserByPhoneNumber(String phoneNumber);

    boolean existsByEmail(String email);

    UserResponseDto patchUser(UUID userReference, UserPatchRequestDto requestDto);

    UserEntity oauthUserRegistration(OAuth2User oAuth2User);
}
