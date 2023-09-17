package com.teamchallenge.marketplace.product.service.impl;

import com.teamchallenge.marketplace.product.dto.request.ProductRequestDto;
import com.teamchallenge.marketplace.product.dto.response.ProductResponseDto;
import com.teamchallenge.marketplace.product.mapper.ProductMapper;
import com.teamchallenge.marketplace.product.persisit.entity.ProductEntity;
import com.teamchallenge.marketplace.product.persisit.repository.ProductRepository;
import com.teamchallenge.marketplace.product.service.ProductService;
import com.teamchallenge.marketplace.user.persisit.entity.UserEntity;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;

    private final ProductMapper productMapper;

    @Override
    public ProductResponseDto getProductByReference(UUID reference) {
        ProductEntity productEntity = productRepository.findByReference(reference).orElseThrow(IllegalArgumentException::new);

        return productMapper.toResponseDto(productEntity, new UserEntity());
    }

    @Override
    public ProductResponseDto createProduct(ProductRequestDto requestDto) {
        ProductEntity entity = productMapper.toEntity(requestDto);
        ProductEntity savedEntity = productRepository.save(entity);
        return productMapper.toResponseDto(savedEntity, new UserEntity());
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
    public List<ProductResponseDto> getAllProducts() {
        return null;
    }
}
