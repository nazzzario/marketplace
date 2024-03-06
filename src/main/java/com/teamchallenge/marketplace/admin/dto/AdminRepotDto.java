package com.teamchallenge.marketplace.admin.dto;

import com.teamchallenge.marketplace.user.persisit.entity.enums.RoleEnum;

public record AdminRepotDto(
        String email,
        RoleEnum role,
        long countActiveAd,
        long countViewActiveAd,
        long raiseActiveAd,
        long countComplaintAd,
        long countDisabledAd,
        boolean isExistNewAd,
        long countFavoriteAd,
        boolean isUnblockedUser
) {
}