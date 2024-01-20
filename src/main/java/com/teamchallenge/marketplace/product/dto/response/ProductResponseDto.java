package com.teamchallenge.marketplace.product.dto.response;

import com.teamchallenge.marketplace.product.persisit.entity.enums.ProductCategoriesEnum;
import com.teamchallenge.marketplace.product.persisit.entity.enums.ProductStateEnum;
import com.teamchallenge.marketplace.product.persisit.entity.enums.ProductStatusEnum;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Schema(description = "The product response fields with images urls")
public record ProductResponseDto(UUID reference,
                                 ProductCategoriesEnum categoryName,
                                 String city,
                                 String productTitle,
                                 String productDescription,
                                 String ownerUsername,
                                 String ownerPhoneNumber,
                                 ProductStateEnum state,
                                 ProductStatusEnum status,
                                 long viewCount,
                                 LocalDate publishDate,
                                 List<String> images) {
}

