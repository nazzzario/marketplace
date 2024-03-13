package com.teamchallenge.marketplace.admin.dto;

import java.util.UUID;

public record ComplaintCounterDto(

        long userId,
        UUID productReference
) {
}
