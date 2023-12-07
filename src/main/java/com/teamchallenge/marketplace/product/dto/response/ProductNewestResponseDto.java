package com.teamchallenge.marketplace.product.dto.response;

import com.teamchallenge.marketplace.product.persisit.entity.enums.ProductCategoriesEnum;
import com.teamchallenge.marketplace.product.persisit.entity.enums.ProductStateEnum;
import com.teamchallenge.marketplace.product.persisit.entity.enums.ProductStatusEnum;

import java.time.LocalDate;
import java.util.UUID;

public record ProductNewestResponseDto(UUID reference,
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
                                       String titleImage) {
}
