package com.teamchallenge.marketplace.admin.service.impl;

import com.teamchallenge.marketplace.admin.service.AdminService;
import com.teamchallenge.marketplace.common.exception.ClientBackendException;
import com.teamchallenge.marketplace.common.exception.ErrorCode;
import com.teamchallenge.marketplace.common.security.bean.UserAccount;
import com.teamchallenge.marketplace.product.service.UserProductService;
import com.teamchallenge.marketplace.user.persisit.entity.UserEntity;
import com.teamchallenge.marketplace.user.persisit.entity.enums.RoleEnum;
import com.teamchallenge.marketplace.user.persisit.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AdminServiceImpl implements AdminService {

    private static final long ONE_USER = 1L;

    private final UserRepository userRepository;
    private final UserProductService productService;

    @Override
    public void blockUser(UUID userReference) {
        var role = ((UserAccount) SecurityContextHolder.getContext().getAuthentication()
                .getPrincipal()).getRole();
        var user = userRepository.findByReference(userReference).orElseThrow(() ->
                new ClientBackendException(ErrorCode.USER_NOT_FOUND));

        if (role.equals(RoleEnum.ROOT) || user.getRole().equals(RoleEnum.USER)) {
            user.setNonLocked(!user.isNonLocked());
            userRepository.save(user);
        }
    }

    @Override
    public void deleteUser(UUID userReference) {
        var role = ((UserAccount) SecurityContextHolder.getContext().getAuthentication()
                .getPrincipal()).getRole();
        var user = userRepository.findByReference(userReference).orElseThrow(() ->
                new ClientBackendException(ErrorCode.USER_NOT_FOUND));

        if ((role.equals(RoleEnum.ROOT) && isNotOneUserWithRoleRoot(user)) ||
                user.getRole().equals(RoleEnum.USER)) {
            var products = productService.getAllProductByUser(user);

            products.forEach(productService::processDeleteProduct);
            userRepository.delete(user);
        }
    }

    @Override
    public void changeUserRole(UUID userReference, RoleEnum role) {
        var user = userRepository.findByReference(userReference).orElseThrow(() ->
                new ClientBackendException(ErrorCode.USER_NOT_FOUND));

        if (isNotOneUserWithRoleRoot(user)) {
            user.setRole(role);
            userRepository.save(user);
        } else {
            throw new ClientBackendException(ErrorCode.NOT_ONE_ROOT);
        }
    }

    private boolean isNotOneUserWithRoleRoot(UserEntity user) {
        return !(user.getRole().equals(RoleEnum.ROOT) &&
                userRepository.countByRole(RoleEnum.ROOT) == ONE_USER);
    }
}
