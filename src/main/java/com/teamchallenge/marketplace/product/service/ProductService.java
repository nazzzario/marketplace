package com.teamchallenge.marketplace.product.service;

import com.teamchallenge.marketplace.product.dto.request.ProductRequestDto;
import com.teamchallenge.marketplace.product.dto.response.ProductNewestResponseDto;
import com.teamchallenge.marketplace.product.dto.response.ProductResponseDto;
import com.teamchallenge.marketplace.product.persisit.entity.enums.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;

import java.util.List;
import java.util.UUID;

public interface ProductService {

    ProductResponseDto getProductByReference(UUID reference);

    void createProduct(ProductRequestDto requestDto, UUID userReference);

    List<ProductResponseDto> getAllProducts();

    Page<ProductResponseDto> getProductsByProductTitle(String productTitle, CitiesEnum city,
                                                       Integer page, Integer size);

    Slice<ProductNewestResponseDto> getNewestProducts(Integer page, Integer size);

    Page<ProductResponseDto> getAllProductsByCategory(ProductCategoriesEnum category,
                                                      CitiesEnum city,
                                                      List<ProductStateEnum> statusList,
                                                      Integer page,
                                                      Integer size,
                                                      SortingFieldEnum sortField);

    Page<ProductResponseDto> getProductByReferenceUser(ProductStatusEnum status,
                                                       UUID referenceUser, Pageable pageable);
}
