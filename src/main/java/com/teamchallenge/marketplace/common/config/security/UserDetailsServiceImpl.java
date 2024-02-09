package com.teamchallenge.marketplace.common.config.security;

import com.teamchallenge.marketplace.common.exception.ClientBackendException;
import com.teamchallenge.marketplace.common.exception.ErrorCode;
import com.teamchallenge.marketplace.common.security.bean.UserAccount;
import com.teamchallenge.marketplace.common.util.PhoneNumberValidator;
import com.teamchallenge.marketplace.user.persisit.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.apache.commons.validator.routines.EmailValidator;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {
    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String emailOrPhone) throws UsernameNotFoundException {
        return getUserByEmailOrPhone(emailOrPhone);
    }

    public UserDetails getUserByEmailOrPhone(String emailOrPhone) {
        String s = Optional.ofNullable(emailOrPhone).orElseThrow(() -> new ClientBackendException(ErrorCode.UNKNOWN_SERVER_ERROR));
        if (EmailValidator.getInstance().isValid(emailOrPhone)) {
            return userRepository.findByEmail(s)
                    .map(UserAccount::fromUserEntityToCustomUserDetails)
                    .orElseThrow(() -> new ClientBackendException(ErrorCode.USER_NOT_FOUND));
        }
        if (PhoneNumberValidator.isValidPhoneNumber(s)) {
            return userRepository.findByPhoneNumber(s)
                    .map(UserAccount::fromUserEntityToCustomUserDetails)
                    .orElseThrow(() -> new ClientBackendException(ErrorCode.USER_NOT_FOUND));
        }

        throw new ClientBackendException(ErrorCode.UNKNOWN_SERVER_ERROR);
    }
}
