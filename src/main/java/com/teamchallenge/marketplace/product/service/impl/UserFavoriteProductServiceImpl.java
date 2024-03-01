package com.teamchallenge.marketplace.product.service.impl;

import com.teamchallenge.marketplace.common.exception.ClientBackendException;
import com.teamchallenge.marketplace.common.exception.ErrorCode;
import com.teamchallenge.marketplace.product.persisit.entity.ProductEntity;
import com.teamchallenge.marketplace.product.persisit.repository.ProductRepository;
import com.teamchallenge.marketplace.product.service.UserFavoriteProductService;
import com.teamchallenge.marketplace.user.persisit.entity.UserEntity;
import com.teamchallenge.marketplace.user.persisit.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserFavoriteProductServiceImpl implements UserFavoriteProductService {
    private final ProductRepository productRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional
    public void addProductToFavorites(UUID productReference) {
        var authentication = SecurityContextHolder.getContext().getAuthentication();

        if (Objects.nonNull(authentication) && authentication.isAuthenticated()) {
            UserEntity userEntity = userRepository.findByEmail(authentication.getName())
                    .orElseThrow(() -> new ClientBackendException(ErrorCode.USER_NOT_FOUND));

            ProductEntity productEntity = productRepository.findByReference(productReference)
                    .orElseThrow(() -> new ClientBackendException(ErrorCode.PRODUCT_NOT_FOUND));

            userEntity.getFavoriteProducts().add(productEntity);
        } else {
            throw new ClientBackendException(ErrorCode.FORBIDDEN);
        }
    }

    @Override
    @Transactional
    public void removeProductFromFavorites(UUID productReference) {
        var authentication = SecurityContextHolder.getContext().getAuthentication();

        if (Objects.nonNull(authentication) && authentication.isAuthenticated()) {
            UserEntity userEntity = userRepository.findByEmail(authentication.getName())
                    .orElseThrow(() -> new ClientBackendException(ErrorCode.USER_NOT_FOUND));

            ProductEntity productEntity = productRepository.findByReference(productReference)
                    .orElseThrow(() -> new ClientBackendException(ErrorCode.PRODUCT_NOT_FOUND));

            userEntity.getFavoriteProducts().remove(productEntity);
        } else {
            throw new ClientBackendException(ErrorCode.FORBIDDEN);
        }
    }

}
