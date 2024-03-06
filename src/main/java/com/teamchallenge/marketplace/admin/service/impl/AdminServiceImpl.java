package com.teamchallenge.marketplace.admin.service.impl;

import com.teamchallenge.marketplace.admin.dto.AdminReprotDto;
import com.teamchallenge.marketplace.admin.dto.ComplaintCounterDto;
import com.teamchallenge.marketplace.admin.dto.CounterProductDto;
import com.teamchallenge.marketplace.admin.service.AdminService;
import com.teamchallenge.marketplace.common.exception.ClientBackendException;
import com.teamchallenge.marketplace.common.exception.ErrorCode;
import com.teamchallenge.marketplace.common.security.bean.UserAccount;
import com.teamchallenge.marketplace.product.persisit.entity.enums.ProductStatusEnum;
import com.teamchallenge.marketplace.product.persisit.repository.ProductRepository;
import com.teamchallenge.marketplace.product.service.UserProductService;
import com.teamchallenge.marketplace.user.persisit.entity.UserEntity;
import com.teamchallenge.marketplace.user.persisit.entity.enums.RoleEnum;
import com.teamchallenge.marketplace.user.persisit.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@RequiredArgsConstructor
public class AdminServiceImpl implements AdminService {

    private static final long ONE_USER = 1L;

    private final UserRepository userRepository;
    private final ProductRepository productRepository;
    private final UserProductService productService;
    private final RedisTemplate<String, String> redisTemplate;

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

    @Override
    public Page<AdminReprotDto> getReport(Pageable pageable) {
        var users = userRepository.findAll(pageable);

        List<CounterProductDto> products;
        if (users.isEmpty()) {
            products = List.of();
        } else {
            products = productRepository.getSumByUsers(users.toList());
        }

        var complaints = Optional.ofNullable(
                        redisTemplate.keys(UserProductService.COMPLAINT_PREFIX + "*"))
                .orElse(Set.of())
                .stream().map(e -> UUID.fromString(
                        e.replace(UserProductService.COMPLAINT_PREFIX, "")))
                .toList();
        List<ComplaintCounterDto> groupComplaints;
        if (complaints.isEmpty()) {
            groupComplaints = List.of();
        } else {
            groupComplaints = productRepository.getComplaintGroupByOwner(complaints);
        }

        return users.map(user -> {
            var localActiveProduct = products.stream().filter(e -> e.ownerId() == user.getId() &&
                            e.status().equals(ProductStatusEnum.ACTIVE)).findAny()
                    .orElse(new CounterProductDto(user.getId().intValue(), ProductStatusEnum.ACTIVE,
                            0, 0, 0, 0));

            return new AdminReprotDto(
                    user.getEmail(),
                    user.getRole(),
                    localActiveProduct.count(),
                    localActiveProduct.view(),
                    localActiveProduct.raise(),
                    groupComplaints.stream().filter(e -> e.userId() == user.getId())
                            .mapToLong(ComplaintCounterDto::count).sum(),
                    products.stream().filter(e1 -> e1.ownerId() == user.getId() &&
                                    e1.status().equals(ProductStatusEnum.DISABLED))
                            .mapToLong(CounterProductDto::count).sum(),
                    products.stream().anyMatch(e2 -> e2.ownerId() == user.getId() &&
                            e2.status().equals(ProductStatusEnum.NEW)),
                    localActiveProduct.favoritism(),
                    user.isNonLocked()
            );
        });
    }

    private boolean isNotOneUserWithRoleRoot(UserEntity user) {
        return !(user.getRole().equals(RoleEnum.ROOT) &&
                userRepository.countByRole(RoleEnum.ROOT) == ONE_USER);
    }
}