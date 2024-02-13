package com.teamchallenge.marketplace.admin.service;

import com.teamchallenge.marketplace.user.persisit.entity.enums.RoleEnum;

import java.util.UUID;

public interface AdminService {
    void blockUser(UUID userReference);

    void deleteUser(UUID userReference);

    void changeUserRole(UUID userReference, RoleEnum role);
}
