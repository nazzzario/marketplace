package com.teamchallenge.marketplace.admin.dto;

import com.teamchallenge.marketplace.user.persisit.entity.enums.RoleEnum;

public record AdminReportDto(
        String email,
        RoleEnum role,
        long countActiveAd,
        long countViewActiveAd,
        long raiseActiveAd,
        int countComplaintAd,
        long countDisabledAd,
        boolean isExistNewAd,
        long countFavoriteAd,
        boolean isUnblockedUser
) {
}
