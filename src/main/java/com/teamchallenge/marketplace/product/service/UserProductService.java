package com.teamchallenge.marketplace.product.service;

import com.teamchallenge.marketplace.product.dto.response.UserProductResponseDto;
import com.teamchallenge.marketplace.product.persisit.entity.enums.ProductStatusEnum;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface UserProductService {
    /**
     * Get all products with status by user within pageable.
     *
     * @param pageable  page, size and sort .
     */

    Page<UserProductResponseDto> getProductsWithStatusByUser(ProductStatusEnum status, Pageable pageable);

    /**
     * Get favorite products by user within pageable.
     *
     * @param pageable  page, size and sort .
     */
    Page<UserProductResponseDto> getFavoriteProductsByUser(Pageable pageable);

}
