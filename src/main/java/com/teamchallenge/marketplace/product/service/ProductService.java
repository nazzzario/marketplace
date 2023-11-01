package com.teamchallenge.marketplace.product.service;

import com.teamchallenge.marketplace.product.dto.request.ProductRequestDto;
import com.teamchallenge.marketplace.product.dto.response.ProductResponseDto;
import com.teamchallenge.marketplace.product.persisit.entity.enums.CitiesEnum;
import com.teamchallenge.marketplace.product.persisit.entity.enums.ProductCategoriesEnum;
import com.teamchallenge.marketplace.product.persisit.entity.enums.ProductStateEnum;
import com.teamchallenge.marketplace.product.persisit.entity.enums.SortingFieldEnum;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Slice;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

public interface ProductService {

    ProductResponseDto getProductByReference(UUID reference);

    ProductResponseDto createProduct(ProductRequestDto requestDto);


    ProductResponseDto createProduct(ProductRequestDto requestDto, UUID userReference);

    void deleteProduct(UUID productReference);

    ProductResponseDto putProduct(ProductRequestDto requestDto);

    ProductResponseDto patchProduct(ProductRequestDto requestDto, UUID productReference);

    List<ProductResponseDto> getAllProducts();

    Page<ProductResponseDto> getProductsByProductTitle(String productTitle, CitiesEnum city, Integer page, Integer size);

    Slice<ProductResponseDto> getNewestProducts(Integer page, Integer size);

    ProductResponseDto uploadImagesToProduct(UUID productReference, List<MultipartFile> images);

    Page<ProductResponseDto> getAllProductsByCategory(ProductCategoriesEnum category,
                                                      CitiesEnum city,
                                                      List<ProductStateEnum> statusList,
                                                      Integer page,
                                                      Integer size,
                                                      SortingFieldEnum sortField);
}
