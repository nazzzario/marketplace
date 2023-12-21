package com.teamchallenge.marketplace.product.mapper;

import com.teamchallenge.marketplace.product.dto.response.UserProductResponseDto;
import com.teamchallenge.marketplace.product.persisit.entity.ProductEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface UserProductMapper {
    @Mapping(target = "reference", source = "productEntity.reference")
    UserProductResponseDto toResponseDto(ProductEntity productEntity);
}
