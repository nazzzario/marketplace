package com.teamchallenge.marketplace.common.security.preauthorize;

import com.teamchallenge.marketplace.common.security.bean.UserAccount;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.Objects;
import java.util.UUID;

@Component
public class UserSecurity {


    public boolean checkReference(UUID userReference) {
        UUID principalReference = ((UserAccount) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getReference();
        return Objects.equals(userReference, principalReference);
    }
}
