package com.teamchallenge.marketplace.admin.service.impl;

import com.teamchallenge.marketplace.admin.service.AdminProductService;
import com.teamchallenge.marketplace.common.exception.ClientBackendException;
import com.teamchallenge.marketplace.common.exception.ErrorCode;
import com.teamchallenge.marketplace.product.dto.response.UserProductResponseDto;
import com.teamchallenge.marketplace.product.mapper.UserProductMapper;
import com.teamchallenge.marketplace.product.persisit.entity.enums.ProductStatusEnum;
import com.teamchallenge.marketplace.product.persisit.repository.ProductRepository;
import com.teamchallenge.marketplace.product.service.UserProductService;
import com.teamchallenge.marketplace.user.persisit.entity.UserEntity;
import com.teamchallenge.marketplace.user.persisit.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AdminProductServiceImpl implements AdminProductService {
    private final UserRepository userRepository;
    private final ProductRepository productRepository;
    private final UserProductMapper productMapper;
    private final RedisTemplate<String, String> redisTemplate;

    @Override
    public Page<UserProductResponseDto> getProductsWithStatusByUser(UUID userReference, ProductStatusEnum status, Pageable pageable) {
        UserEntity userEntity = userRepository.findByReference(userReference)
                .orElseThrow(() -> new ClientBackendException(ErrorCode.USER_NOT_FOUND));
        return productRepository.findByOwnerAndStatus(userEntity, status, pageable)
                .map(product ->  productMapper.toResponseDto(product,
                        redisTemplate.opsForHash().hasKey(UserProductService.RAISE_AD_PREFIX, product.getReference().toString())));
    }

    @Override
    public Page<UserProductResponseDto> getFavoriteProductsByUser(UUID userReference, Pageable pageable) {
        UserEntity userEntity = userRepository.findByReference(userReference)
                .orElseThrow(() -> new ClientBackendException(ErrorCode.USER_NOT_FOUND));
        return productRepository.findByFavoritismId(userEntity.getId(), pageable)
                .map(product ->  productMapper.toResponseDto(product,
                        redisTemplate.opsForHash().hasKey(UserProductService.RAISE_AD_PREFIX,
                                product.getReference().toString())));
    }
}
