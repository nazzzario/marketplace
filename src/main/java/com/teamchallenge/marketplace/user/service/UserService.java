package com.teamchallenge.marketplace.user.service;

import com.teamchallenge.marketplace.product.persisit.entity.enums.ProductStatusEnum;
import com.teamchallenge.marketplace.user.dto.request.UserPasswordRequestDto;
import com.teamchallenge.marketplace.user.dto.request.UserPatchRequestDto;
import com.teamchallenge.marketplace.user.dto.request.UserRequestDto;
import com.teamchallenge.marketplace.user.dto.response.UserResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface UserService {
    UserResponseDto userRegistration(UserRequestDto requestDto);

    UserResponseDto getUserByReference(UUID reference);

    UserResponseDto getUserByPhoneNumber(String phoneNumber);

    boolean existsByEmail(String email);

    UserResponseDto patchUser(UUID userReference, UserPatchRequestDto requestDto);

    Page<UserResponseDto> getUserByStatusProduct(ProductStatusEnum status, Pageable pageable);

    void patchPassword(UserPasswordRequestDto requestDto);

    void sendVerificationCode(String email, String ip);

    void changeUserToFake();
}
