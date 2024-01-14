package com.teamchallenge.marketplace.product.service.impl;

import com.teamchallenge.marketplace.common.exception.ClientBackendException;
import com.teamchallenge.marketplace.common.exception.ErrorCode;
import com.teamchallenge.marketplace.product.dto.response.UserProductResponseDto;
import com.teamchallenge.marketplace.product.mapper.UserProductMapper;
import com.teamchallenge.marketplace.product.persisit.entity.enums.ProductStatusEnum;
import com.teamchallenge.marketplace.product.persisit.repository.ProductRepository;
import com.teamchallenge.marketplace.product.service.ShowUserProductService;
import com.teamchallenge.marketplace.user.persisit.entity.UserEntity;
import com.teamchallenge.marketplace.user.persisit.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
@RequiredArgsConstructor
public class ShowUserProductServiceImpl implements ShowUserProductService {
    private final ProductRepository productRepository;
    private final UserRepository userRepository;
    private final UserProductMapper productMapper;

    @Override
    public Page<UserProductResponseDto> getProductsWithStatusByUser(ProductStatusEnum status, Pageable pageable) {
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        if (Objects.nonNull(authentication) && authentication.isAuthenticated()){
            String email = authentication.getName();
            UserEntity userEntity = userRepository.findByEmail(email)
                    .orElseThrow(() -> new ClientBackendException(ErrorCode.USER_NOT_FOUND));
            return productRepository.findByOwnerAndStatus(userEntity, status, pageable)
                    .map(productMapper::toResponseDto);
        } else {
            throw new ClientBackendException(ErrorCode.UNKNOWN_SERVER_ERROR);
        }

    }

    @Override
    public Page<UserProductResponseDto> getFavoriteProductsByUser(Pageable pageable) {
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        if (Objects.nonNull(authentication) && authentication.isAuthenticated()){
            String email = authentication.getName();
            UserEntity userEntity = userRepository.findByEmail(email)
                    .orElseThrow(() -> new ClientBackendException(ErrorCode.USER_NOT_FOUND));
            return productRepository.findByFavoritismId(userEntity.getId(), pageable)
                    .map(productMapper::toResponseDto);
        } else {
            throw new ClientBackendException(ErrorCode.UNKNOWN_SERVER_ERROR);
        }
    }
}
