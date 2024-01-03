package com.teamchallenge.marketplace.product.mapper;

import com.teamchallenge.marketplace.product.dto.response.UserProductImageDto;
import com.teamchallenge.marketplace.product.dto.response.UserProductResponseDto;
import com.teamchallenge.marketplace.product.persisit.entity.ProductEntity;
import com.teamchallenge.marketplace.product.persisit.entity.ProductImageEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface UserProductMapper {
    @Mapping(target = "reference", source = "productEntity.reference")
    @Mapping(target = "images",
            expression = "java(toListOfImageDto(productEntity.getImages()))")
    UserProductResponseDto toResponseDto(ProductEntity productEntity);

    UserProductImageDto toImageDto(ProductImageEntity imageEntity);

    List<UserProductImageDto> toListOfImageDto(List<ProductImageEntity> imageEntities);
}