package com.teamchallenge.marketplace.product.mapper;

import com.teamchallenge.marketplace.product.dto.request.ProductRequestDto;
import com.teamchallenge.marketplace.product.dto.response.UserProductImageDto;
import com.teamchallenge.marketplace.product.dto.response.UserProductResponseDto;
import com.teamchallenge.marketplace.product.persisit.entity.ProductEntity;
import com.teamchallenge.marketplace.product.persisit.entity.ProductImageEntity;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import java.util.List;

import static org.mapstruct.NullValuePropertyMappingStrategy.IGNORE;

@Mapper(componentModel = "spring")
public interface UserProductMapper {
    @Mapping(target = "viewCount", ignore = true)
    @Mapping(target = "publishDate", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "reference", ignore = true)
    @Mapping(target = "owner", ignore = true)
    @Mapping(target = "modifiedDate", ignore = true)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdDate", ignore = true)
    @Mapping(target = "images", ignore = true)
    ProductEntity toNewEntity(ProductEntity requestDto);

    @Mapping(target = "status", ignore = true)
    @Mapping(target = "reference", ignore = true)
    @Mapping(target = "owner", ignore = true)
    @Mapping(target = "modifiedDate", ignore = true)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdDate", ignore = true)
    @Mapping(target = "images", ignore = true)
    @BeanMapping(nullValuePropertyMappingStrategy = IGNORE)
    void patchMerge(ProductRequestDto requestDto, @MappingTarget ProductEntity competition);

    @Mapping(target = "reference", source = "productEntity.reference")
    @Mapping(target = "images",
            expression = "java(toListOfImageDto(productEntity.getImages()))")
    UserProductResponseDto toResponseDto(ProductEntity productEntity);

    UserProductImageDto toImageDto(ProductImageEntity imageEntity);

    List<UserProductImageDto> toListOfImageDto(List<ProductImageEntity> imageEntities);
}