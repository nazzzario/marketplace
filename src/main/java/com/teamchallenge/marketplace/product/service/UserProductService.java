package com.teamchallenge.marketplace.product.service;

import com.teamchallenge.marketplace.product.dto.request.ProductRequestDto;
import com.teamchallenge.marketplace.product.dto.response.UserProductResponseDto;
import com.teamchallenge.marketplace.product.persisit.entity.enums.ProductStatusEnum;

import java.util.UUID;

public interface UserProductService {
    UserProductResponseDto createOrGetNewProduct();

    UserProductResponseDto patchProduct(ProductRequestDto requestDto, UUID productReference);

    void deleteProduct(UUID productReference);

    UserProductResponseDto changeStatusProduct(UUID productReference, ProductStatusEnum status);
}
