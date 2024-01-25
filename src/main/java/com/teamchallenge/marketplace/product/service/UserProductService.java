package com.teamchallenge.marketplace.product.service;

import com.teamchallenge.marketplace.product.dto.request.ProductRequestDto;
import com.teamchallenge.marketplace.product.dto.response.UserProductResponseDto;
import com.teamchallenge.marketplace.product.persisit.entity.ProductEntity;
import com.teamchallenge.marketplace.product.persisit.entity.enums.ProductStatusEnum;

import java.util.UUID;

public interface UserProductService {
    /**
     * Get product with status NEW. If there is no product create new product.
     * Values of product is values of first product with status ACTIVE.
     * If there is no any product with status ACTIVE fill values default values
     * */
    UserProductResponseDto createOrGetNewProduct();

    UserProductResponseDto patchProduct(ProductRequestDto requestDto, UUID productReference);

    void deleteProduct(UUID productReference);

    UserProductResponseDto changeStatusProduct(UUID productReference, ProductStatusEnum status, int period);

    void processDeleteProduct(ProductEntity productEntity);

    ProductEntity getProductAndChangeStatus(ProductEntity product, ProductStatusEnum status);
}
