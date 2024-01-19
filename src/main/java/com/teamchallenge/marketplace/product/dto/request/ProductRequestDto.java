package com.teamchallenge.marketplace.product.dto.request;

import com.teamchallenge.marketplace.product.persisit.entity.enums.ProductCategoriesEnum;
import com.teamchallenge.marketplace.product.persisit.entity.enums.ProductStateEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

@Schema(description = "The product fields to be entered")
public record ProductRequestDto(
        @NotNull(message = "Product category cannot be null")
        ProductCategoriesEnum categoryName,
        @NotNull(message = "Product city cannot be null")
        String city,
        @NotBlank(message = "Product title cannot be empty")
        @Size(min = 4, max = 30, message = "Product length between 4 and 30")
        String productTitle,
        String productDescription,
        @NotNull(message = "Product state cannot be null")
        ProductStateEnum state,
        @NotNull(message = "Product publish date cannot be null")
        LocalDate publishDate) {
}
