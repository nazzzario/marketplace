package com.teamchallenge.marketplace.user.service.impl;

import com.teamchallenge.marketplace.common.email.service.EmailService;
import com.teamchallenge.marketplace.common.exception.ClientBackendException;
import com.teamchallenge.marketplace.common.exception.ErrorCode;
import com.teamchallenge.marketplace.common.security.service.SecurityAttempts;
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
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private static final String EXCEPTION_PREFIX = "Exception_";
    private static final String LIMIT_VERIFICATION_PREFIX = "LimitVerification_";
    public static final String VERIFICATION_CODE = "VerificationCode_";

    @Value("${user.max.value}")
    private int maxValue;
    @Value("${user.limitation}")
    private int limitation;
    @Value("${user.timeout}")
    private long timeout;
    @Value("${mail.template.path.user}")
    private String userTemplatePath;

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final SecurityAttempts attempts;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;

    @Override
    @Transactional
    public UserResponseDto userRegistration(UserRequestDto requestDto) {
        if (attempts.isAttemptExhausted(LIMIT_VERIFICATION_PREFIX, requestDto.email())){
            throw new ClientBackendException(ErrorCode.ATTEMPTS_IS_EXHAUSTED);
        }

        if (userRepository.existsByEmail(requestDto.email())) {
            throw new ClientBackendException(ErrorCode.EMAIL_ALREADY_EXISTS);
        }

        if (attempts.isNotVerificationCode(VERIFICATION_CODE + requestDto.email(),
                requestDto.verificationCode())){
            attempts.incrementCounterAttempt(LIMIT_VERIFICATION_PREFIX, EXCEPTION_PREFIX,
                    requestDto.email(), limitation, timeout);
            throw new ClientBackendException(ErrorCode.IS_NOT_VERIFICATION);
        }

        attempts.deleteVerificationCode(VERIFICATION_CODE + requestDto.email());

        UserEntity userEntity = userMapper.toEntity(requestDto);
        userEntity.setPassword(passwordEncoder.encode(requestDto.password()));
        userEntity.setRole(RoleEnum.USER);
        UserEntity savedUser = userRepository.save(userEntity);

        attempts.delete(LIMIT_VERIFICATION_PREFIX, requestDto.email());
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
    public void sendVerificationCode(String email, String ip) {
        if (attempts.isAttemptExhausted(LIMIT_VERIFICATION_PREFIX, ip) ||
                attempts.isUsedSingleAttempt(LIMIT_VERIFICATION_PREFIX, email, timeout)) {
            throw new ClientBackendException(ErrorCode.ATTEMPTS_IS_EXHAUSTED);
        }

        attempts.incrementCounterAttempt(LIMIT_VERIFICATION_PREFIX, EXCEPTION_PREFIX, ip,
                limitation, timeout);

        String code = getVerificationCode();
        attempts.setVerificationCode(VERIFICATION_CODE + email, code, timeout);
        emailService.sendEmail(email, emailService.buildMsgForUser(userTemplatePath,
                getMessageAboutVerificationCode(code)),
                "Веріфікаційний код для підтвердження пошти.");
    }

    private String getMessageAboutVerificationCode(String code) {
        return new StringBuilder("<h2>Ми надіслали код для підтвердження пошти.</h2>")
                .append("<h3>Код: ").append(code).append("</h3>")
                .append("<h3>Введіть цей код в форму</h3>").toString();
    }

    private String getVerificationCode() {
        int randomNumber = ThreadLocalRandom.current().nextInt(maxValue);
        String format = "%0" + String.valueOf(maxValue).length() + "d";
        return String.format(format, randomNumber);
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
}
