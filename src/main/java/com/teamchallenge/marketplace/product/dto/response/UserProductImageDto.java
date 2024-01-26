package com.teamchallenge.marketplace.product.dto.response;

import java.util.UUID;

public record UserProductImageDto(String imageUrl, UUID reference, Long id) {
}
