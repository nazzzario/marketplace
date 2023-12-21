package com.teamchallenge.marketplace.product.mapper;

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
            expression = "java(getListImageToString(productEntity.getImages()))")
    UserProductResponseDto toResponseDto(ProductEntity productEntity);

    default List<String> getListImageToString(
            List<ProductImageEntity> productImageEntities){
        return productImageEntities.stream().map(ProductImageEntity::getImageUrl).toList();
    }

}