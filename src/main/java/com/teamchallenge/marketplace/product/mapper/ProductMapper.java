package com.teamchallenge.marketplace.product.mapper;

import com.teamchallenge.marketplace.product.dto.request.ProductRequestDto;
import com.teamchallenge.marketplace.product.dto.response.ProductResponseDto;
import com.teamchallenge.marketplace.product.persisit.entity.ProductEntity;
import com.teamchallenge.marketplace.product.persisit.entity.ProductImage;
import com.teamchallenge.marketplace.user.persisit.entity.UserEntity;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import static org.mapstruct.NullValuePropertyMappingStrategy.IGNORE;
import static org.mapstruct.NullValuePropertyMappingStrategy.SET_TO_NULL;

@Mapper(componentModel = "spring")
public interface ProductMapper {

    @Mapping(target = "reference", ignore = true)
    @Mapping(target = "owner", ignore = true)
    @Mapping(target = "modifiedDate", ignore = true)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdDate", ignore = true)
    @Mapping(target = "images", ignore = true)
    ProductEntity toEntity(ProductRequestDto requestDto);


    @Mapping(target = "ownerPhoneNumber", source = "userEntity.phoneNumber")
    @Mapping(target = "ownerFirstName", source = "userEntity.firstName")
    @Mapping(target = "reference", source = "productEntity.reference")
    @Mapping(target = "images", ignore = true)
    ProductResponseDto toResponseDto(ProductEntity productEntity, UserEntity userEntity);

    @Mapping(target = "reference", ignore = true)
    @Mapping(target = "owner", ignore = true)
    @Mapping(target = "modifiedDate", ignore = true)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdDate", ignore = true)
    @Mapping(target = "images", ignore = true)
    @BeanMapping(nullValuePropertyMappingStrategy = IGNORE)
    void patchMerge(ProductRequestDto requestDto, @MappingTarget ProductEntity competition);

    @Mapping(target = "reference", ignore = true)
    @Mapping(target = "owner", ignore = true)
    @Mapping(target = "modifiedDate", ignore = true)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdDate", ignore = true)
    @Mapping(target = "images", ignore = true)
    @BeanMapping(nullValuePropertyMappingStrategy = SET_TO_NULL)
    void putMerge(ProductRequestDto requestDto, @MappingTarget ProductEntity competition);

    @Mapping(target = "imageUrl", source = "imageUrl")
    @Mapping(target = "reference", ignore = true)
    @Mapping(target = "product", ignore = true)
    @Mapping(target = "modifiedDate", ignore = true)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdDate", ignore = true)
    @Mapping(target = "cover", ignore = true)
    ProductImage toProductImage(String imageUrl);
}
