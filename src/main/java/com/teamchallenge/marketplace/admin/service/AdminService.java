package com.teamchallenge.marketplace.admin.service;

import com.teamchallenge.marketplace.admin.dto.AdminReprotDto;
import com.teamchallenge.marketplace.user.persisit.entity.enums.RoleEnum;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface AdminService {
    void blockUser(UUID userReference);

    void deleteUser(UUID userReference);

    void changeUserRole(UUID userReference, RoleEnum role);

    Page<AdminReprotDto> getReport(Pageable pageable);
}
