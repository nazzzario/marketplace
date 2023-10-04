package com.teamchallenge.marketplace.product.dto.response;

import com.teamchallenge.marketplace.product.persisit.entity.enums.ProductCategoriesEnum;
import com.teamchallenge.marketplace.product.persisit.entity.enums.ProductStateEnum;
import com.teamchallenge.marketplace.product.persisit.entity.enums.ProductStatusEnum;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;
import java.util.UUID;

@Schema(description = "The product response fields with images urls")
public record ProductResponseDto(UUID reference,
                                 ProductCategoriesEnum categoryName,
                                 String city,
                                 String productTitle,
                                 String productDescription,
                                 String ownerFirstName,
                                 String ownerPhoneNumber,
                                 ProductStateEnum state,
                                 ProductStatusEnum status,
                                 List<String> images) {
}

