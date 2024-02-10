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
            UserAccount account = (UserAccount) authentication.getPrincipal();
            return account.getRole().equals(RoleEnum.ROOT) ||
                    account.getRole().equals(RoleEnum.ADMIN) ||
                    Objects.equals(userReference, account.getReference());
        }
        return false;
    }

    public boolean checkAdminRights(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (Objects.nonNull(authentication) && authentication.isAuthenticated()) {
            UserAccount account = (UserAccount) authentication.getPrincipal();
            return account.getRole().equals(RoleEnum.ROOT) ||
                    account.getRole().equals(RoleEnum.ADMIN);
        }
        return false;
    }

    public boolean checkRootRights(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (Objects.nonNull(authentication) && authentication.isAuthenticated()) {
            UserAccount account = (UserAccount) authentication.getPrincipal();
            return account.getRole().equals(RoleEnum.ROOT);
        }
        return false;
    }
}
