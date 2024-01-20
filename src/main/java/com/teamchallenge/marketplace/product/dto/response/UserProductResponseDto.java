package com.teamchallenge.marketplace.product.dto.response;

import com.teamchallenge.marketplace.product.persisit.entity.enums.ProductCategoriesEnum;
import com.teamchallenge.marketplace.product.persisit.entity.enums.ProductStateEnum;
import com.teamchallenge.marketplace.product.persisit.entity.enums.ProductStatusEnum;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Schema(description = "The product response fields with images url and reference")
public record UserProductResponseDto(UUID reference,
                                     ProductCategoriesEnum categoryName,
                                     String city,
                                     String productTitle,
                                     String productDescription,
                                     ProductStateEnum state,
                                     ProductStatusEnum status,
                                     long viewCount,
                                     LocalDate publishDate,
                                     List<UserProductImageDto> images) {
}
