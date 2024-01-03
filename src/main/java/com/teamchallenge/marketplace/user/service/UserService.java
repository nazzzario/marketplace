package com.teamchallenge.marketplace.user.service;

import com.teamchallenge.marketplace.product.persisit.entity.enums.ProductStatusEnum;
import com.teamchallenge.marketplace.user.dto.request.UserPatchRequestDto;
import com.teamchallenge.marketplace.user.dto.request.UserRequestDto;
import com.teamchallenge.marketplace.user.dto.response.UserResponseDto;

import java.util.List;
import java.util.UUID;

public interface UserService {
    UserResponseDto userRegistration(UserRequestDto requestDto);

    UserResponseDto getUserByReference(UUID reference);

    UserResponseDto getUserByPhoneNumber(String phoneNumber);

    boolean existsByEmail(String email);

    UserResponseDto patchUser(UUID userReference, UserPatchRequestDto requestDto);

    List<UserResponseDto> getUsersByProductStatus(ProductStatusEnum status);
}
