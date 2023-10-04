package com.teamchallenge.marketplace.product.dto.request;

import com.teamchallenge.marketplace.product.persisit.entity.enums.ProductCategoriesEnum;
import com.teamchallenge.marketplace.product.persisit.entity.enums.ProductStateEnum;
import com.teamchallenge.marketplace.product.persisit.entity.enums.ProductStatusEnum;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "The product fields to be entered")
public record ProductRequestDto(
        ProductCategoriesEnum categoryName,
        String city,
        String productTitle,
        String productDescription,
        ProductStateEnum state,
        ProductStatusEnum status) {
}
