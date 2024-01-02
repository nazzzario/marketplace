package com.teamchallenge.marketplace.product.service.impl;

import com.teamchallenge.marketplace.common.exception.ClientBackendException;
import com.teamchallenge.marketplace.common.exception.ErrorCode;
import com.teamchallenge.marketplace.product.dto.response.ProductResponseDto;
import com.teamchallenge.marketplace.product.mapper.ProductMapper;
import com.teamchallenge.marketplace.product.persisit.entity.enums.ProductStatusEnum;
import com.teamchallenge.marketplace.product.persisit.repository.ProductRepository;
import com.teamchallenge.marketplace.product.service.ActiveProductService;
import com.teamchallenge.marketplace.user.persisit.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ActiveProductServiceImpl implements ActiveProductService {

    private final UserRepository userRepository;
    private final ProductRepository productRepository;
    private final ProductMapper productMapper;

    @Override
    public Page<ProductResponseDto> getProductByReferenceUser(ProductStatusEnum status,
                                                              UUID referenceUser,
                                                              Pageable pageable) {
        var user = userRepository.findByReference(referenceUser).orElseThrow(() ->
                new ClientBackendException(ErrorCode.USER_NOT_FOUND));

        return productRepository.findByOwnerAndStatus(user, status, pageable)
                .map(productEntity -> productMapper.toResponseDto(productEntity,user));

    }
}
