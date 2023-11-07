package com.teamchallenge.marketplace.user.service.impl;

import com.teamchallenge.marketplace.common.exception.ClientBackendException;
import com.teamchallenge.marketplace.common.exception.ErrorCode;
import com.teamchallenge.marketplace.user.dto.request.UserPatchRequestDto;
import com.teamchallenge.marketplace.user.dto.request.UserRequestDto;
import com.teamchallenge.marketplace.user.dto.response.UserResponseDto;
import com.teamchallenge.marketplace.user.mapper.UserMapper;
import com.teamchallenge.marketplace.user.persisit.entity.UserEntity;
import com.teamchallenge.marketplace.user.persisit.entity.enums.RoleEnum;
import com.teamchallenge.marketplace.user.persisit.repository.UserRepository;
import com.teamchallenge.marketplace.user.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

    private final UserMapper userMapper;

    @Override
    @Transactional
    public UserResponseDto userRegistration(UserRequestDto requestDto) {
        if(existsByEmail(requestDto.email())){
            throw new ClientBackendException(ErrorCode.EMAIL_ALREADY_EXISTS);
        }
        UserEntity userEntity = userMapper.toEntity(requestDto);
        userEntity.setRole(RoleEnum.USER);
        UserEntity savedUser = userRepository.save(userEntity);

        return userMapper.toResponseDto(savedUser);
    }

    @Override
    public UserResponseDto getUserByReference(UUID reference) {
        UserEntity userByReference = userRepository.findByReference(reference)
                .orElseThrow(() -> new ClientBackendException(ErrorCode.USER_NOT_FOUND));

        return userMapper.toResponseDto(userByReference);
    }

    @Override
    public UserResponseDto getUserByPhoneNumber(String phoneNumber) {
        UserEntity userByPhoneNumber = userRepository.findByPhoneNumber(phoneNumber)
                .orElseThrow(() -> new ClientBackendException(ErrorCode.USER_NOT_FOUND));

        return userMapper.toResponseDto(userByPhoneNumber);
    }

    @Override
    @Transactional
    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    @Override
    @Transactional
    public UserResponseDto patchUser(UUID userReference, @Valid UserPatchRequestDto requestDto) {
        UserEntity userByReference = userRepository.findByReference(userReference)
                .orElseThrow(() -> new ClientBackendException(ErrorCode.USER_NOT_FOUND));
        userMapper.patchMerge(requestDto, userByReference);

        return userMapper.toResponseDto(userByReference);
    }
}
