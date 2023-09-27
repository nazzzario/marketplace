package com.teamchallenge.marketplace.product.service;

import com.teamchallenge.marketplace.product.dto.request.ProductRequestDto;
import com.teamchallenge.marketplace.product.dto.response.ProductResponseDto;
import org.springframework.data.domain.Slice;

import java.util.List;
import java.util.UUID;

public interface ProductService {

    ProductResponseDto getProductByReference(UUID reference);

    ProductResponseDto createProduct(ProductRequestDto requestDto, UUID userReference);

    void deleteProduct(UUID productReference);

    ProductResponseDto putProduct(ProductRequestDto requestDto);

    ProductResponseDto patchProduct(ProductRequestDto requestDto);

    List<ProductResponseDto> getAllProducts();

    List<ProductResponseDto> getProductsByProductTitle(String productTitle);

    Slice<ProductResponseDto> getNewestProducts(Integer page, Integer size);
}
