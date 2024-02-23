package com.teamchallenge.marketplace.admin.dto;

import java.util.UUID;

public record ComplaintDto(
        UUID userReference,
        String message) {
}
