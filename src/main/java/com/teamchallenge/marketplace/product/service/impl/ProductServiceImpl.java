package com.teamchallenge.marketplace.product.service.impl;

import com.teamchallenge.marketplace.product.dto.request.ProductRequestDto;
import com.teamchallenge.marketplace.product.dto.response.ProductResponseDto;
import com.teamchallenge.marketplace.product.mapper.ProductMapper;
import com.teamchallenge.marketplace.product.persisit.entity.ProductEntity;
import com.teamchallenge.marketplace.product.persisit.repository.ProductRepository;
import com.teamchallenge.marketplace.product.service.ProductService;
import com.teamchallenge.marketplace.user.persisit.entity.UserEntity;
import com.teamchallenge.marketplace.user.persisit.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    private final UserRepository userRepository;

    private final ProductMapper productMapper;

    @Override
    public ProductResponseDto getProductByReference(UUID reference) {
        ProductEntity productEntity = productRepository.findByReference(reference).orElseThrow(IllegalArgumentException::new);

        return productMapper.toResponseDto(productEntity, productEntity.getOwner());
    }

    @Override
    public ProductResponseDto createProduct(ProductRequestDto requestDto, UUID userReference) {
        UserEntity userEntity = userRepository.findByReference(userReference).orElseThrow(IllegalArgumentException::new);
        ProductEntity entity = productMapper.toEntity(requestDto);

        entity.setOwner(userEntity);

        ProductEntity savedEntity = productRepository.save(entity);
        return productMapper.toResponseDto(savedEntity, userEntity);
    }

    @Override
    public void deleteProduct(UUID productReference) {

    }

    @Override
    public ProductResponseDto putProduct(ProductRequestDto requestDto) {
        return null;
    }

    @Override
    public ProductResponseDto patchProduct(ProductRequestDto requestDto) {
        return null;
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProductResponseDto> getAllProducts() {
        return productRepository.findAll().stream()
                .map(p -> productMapper.toResponseDto(p, p.getOwner()))
                .toList();
    }

    @Override
    public List<ProductResponseDto> getProductsByProductTitle(String productTitle) {
        if (Objects.isNull(productTitle)) {
            throw new IllegalArgumentException();
        }
        return productRepository.findByProductTitleLikeIgnoreCase("%" + productTitle + "%")
                .stream().map(p -> productMapper.toResponseDto(p, p.getOwner()))
                .toList();
    }
}
