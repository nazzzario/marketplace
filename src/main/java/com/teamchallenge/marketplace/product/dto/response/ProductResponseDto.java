package com.teamchallenge.marketplace.product.dto.response;

import com.teamchallenge.marketplace.product.persisit.entity.enums.ProductCategoriesEnum;
import com.teamchallenge.marketplace.product.persisit.entity.enums.ProductStateEnum;
import com.teamchallenge.marketplace.product.persisit.entity.enums.ProductStatusEnum;

import java.util.UUID;

public record ProductResponseDto(UUID reference,
                                 ProductCategoriesEnum categoryName,
                                 String city,
                                 String productTitle,
                                 String productDescription,
                                 String ownerFirstName,
                                 String ownerPhoneNumber,
                                 ProductStateEnum state,
                                 ProductStatusEnum status) {
}

