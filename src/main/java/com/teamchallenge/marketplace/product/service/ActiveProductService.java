package com.teamchallenge.marketplace.product.service;

import com.teamchallenge.marketplace.product.dto.response.ProductResponseDto;
import com.teamchallenge.marketplace.product.persisit.entity.enums.ProductStatusEnum;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface ActiveProductService {
    /**
     * Get all product with status by reference of user
     *
     * @param status Status of product
     * @param referenceUser Reference of user
     */
    Page<ProductResponseDto> getProductByReferenceUser(ProductStatusEnum status,
                                                       UUID referenceUser,
                                                       Pageable pageable);
}
