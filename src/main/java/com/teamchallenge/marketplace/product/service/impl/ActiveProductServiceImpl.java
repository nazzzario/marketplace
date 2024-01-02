package com.teamchallenge.marketplace.product.service.impl;

import com.teamchallenge.marketplace.product.dto.response.ProductResponseDto;
import com.teamchallenge.marketplace.product.persisit.entity.enums.ProductStatusEnum;
import com.teamchallenge.marketplace.product.service.ActiveProductService;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class ActiveProductServiceImpl implements ActiveProductService {
    @Override
    public Page<ProductResponseDto> getProductByReferenceUser(ProductStatusEnum status, UUID referenceUser) {
        return null;
    }
}
