package com.teamchallenge.marketplace.user.service.impl;

import com.teamchallenge.marketplace.common.exception.ClientBackendException;
import com.teamchallenge.marketplace.common.exception.ErrorCode;
import com.teamchallenge.marketplace.common.security.dto.request.AuthenticationRequest;
import com.teamchallenge.marketplace.product.persisit.entity.enums.ProductStatusEnum;
import com.teamchallenge.marketplace.user.dto.request.UserPasswordRequestDto;
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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public UserResponseDto userRegistration(UserRequestDto requestDto) {
        if (userRepository.existsByEmail(requestDto.email())) {
            throw new ClientBackendException(ErrorCode.EMAIL_ALREADY_EXISTS);
        }
        UserEntity userEntity = userMapper.toEntity(requestDto);
        userEntity.setPassword(passwordEncoder.encode(requestDto.password()));
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

    @Override
    public Page<UserResponseDto> getUserByStatusProduct(ProductStatusEnum status, Pageable pageable) {
        return userRepository.findDistinctByProductsStatus(status, pageable)
                .map(userMapper::toResponseDto);
    }

    @Override
    @Transactional
    public void patchPassword(UserPasswordRequestDto requestDto) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (Objects.nonNull(authentication) && authentication.isAuthenticated()) {
            if (requestDto.oldPassword().equals(requestDto.newPassword())) {
                throw new ClientBackendException(ErrorCode.NEW_PASSWORD_SAME_AS_OLD_PASSWORD);
            }
            var user = userRepository.findByEmail(authentication.getName()).orElseThrow(() ->
                    new ClientBackendException(ErrorCode.USER_NOT_FOUND));

            if (passwordEncoder.matches(requestDto.oldPassword(), user.getPassword())) {
                user.setPassword(passwordEncoder.encode(requestDto.newPassword()));
            } else {
                throw new ClientBackendException(ErrorCode.PASSWORD_NOT_EXISTS);
            }
        } else {
            throw new ClientBackendException(ErrorCode.UNKNOWN_SERVER_ERROR);
        }
    }

    @Override
    public List<AuthenticationRequest> hashPasswordAllUsers() {
        List<UserEntity> users = userRepository.findAll();

        users.stream().filter(u -> u.getPassword().length() > 30)
                .forEach(user -> {
                    user.setPassword(passwordEncoder.encode(user.getPassword()));
                    userRepository.save(user);
                });

        return users.stream().map(u -> new AuthenticationRequest(u.getEmail(),
                u.getPassword())).toList();
    }
}
