package com.teamchallenge.marketplace.common.security.preauthorize;

import com.teamchallenge.marketplace.common.security.bean.UserAccount;
import com.teamchallenge.marketplace.user.persisit.entity.enums.RoleEnum;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.Objects;
import java.util.UUID;

@Component
public class UserSecurity {


    public boolean checkReference(UUID userReference) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (Objects.nonNull(authentication) && authentication.isAuthenticated()) {
            UUID principalReference = ((UserAccount) authentication.getPrincipal()).getReference();
            return authentication.getAuthorities().stream().anyMatch(role -> role.getAuthority()
                    .equals(RoleEnum.ADMIN.name())) ||
                    Objects.equals(userReference, principalReference);
        }
        return false;
    }
}
