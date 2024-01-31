package com.teamchallenge.marketplace.product.mapper;

import com.teamchallenge.marketplace.product.dto.response.ProductNewestResponseDto;
import com.teamchallenge.marketplace.product.dto.request.ProductRequestDto;
import com.teamchallenge.marketplace.product.dto.response.ProductResponseDto;
import com.teamchallenge.marketplace.product.persisit.entity.ProductEntity;
import com.teamchallenge.marketplace.product.persisit.entity.ProductImageEntity;
import com.teamchallenge.marketplace.user.persisit.entity.UserEntity;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import java.util.List;

import static org.mapstruct.NullValuePropertyMappingStrategy.IGNORE;
import static org.mapstruct.NullValuePropertyMappingStrategy.SET_TO_NULL;

@Mapper(componentModel = "spring")
public interface ProductMapper {

    @Mapping(target = "viewCount", ignore = true)
    @Mapping(target = "publishDate", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "reference", ignore = true)
    @Mapping(target = "owner", ignore = true)
    @Mapping(target = "modifiedDate", ignore = true)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdDate", ignore = true)
    @Mapping(target = "images", ignore = true)
    ProductEntity toEntity(ProductRequestDto requestDto);


    @Mapping(target = "ownerPhoneNumber", source = "userEntity.phoneNumber")
    @Mapping(target = "ownerUsername", source = "userEntity.username")
    @Mapping(target = "reference", source = "productEntity.reference")
    ProductResponseDto toResponseDto(ProductEntity productEntity, UserEntity userEntity);

    @Mapping(target = "titleImage", expression = "java(getProductCoverImage(productEntity))")
    @Mapping(target = "ownerPhoneNumber", source = "userEntity.phoneNumber")
    @Mapping(target = "ownerUsername", source = "userEntity.username")
    @Mapping(target = "reference", source = "productEntity.reference")
    ProductNewestResponseDto toNewestResponseDto(ProductEntity productEntity, UserEntity userEntity);

    @Mapping(target = "status", ignore = true)
    @Mapping(target = "reference", ignore = true)
    @Mapping(target = "owner", ignore = true)
    @Mapping(target = "modifiedDate", ignore = true)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdDate", ignore = true)
    @Mapping(target = "images", ignore = true)
    @BeanMapping(nullValuePropertyMappingStrategy = IGNORE)
    void patchMerge(ProductRequestDto requestDto, @MappingTarget ProductEntity competition);

    @Mapping(target = "status", ignore = true)
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
    ProductImageEntity toProductImage(String imageUrl);

    List<String> productImagesToImageUrls(List<ProductImageEntity> productImageEntities);

    default String productImageToImageUrl(ProductImageEntity productImageEntity){
        return productImageEntity.getImageUrl();
    }

    default String getProductCoverImage(ProductEntity entity){
        return entity.getImages().stream()
                .filter(ProductImageEntity::isCover)
                .findFirst()
                .map(ProductImageEntity::getImageUrl)
                .orElse("cover image not presented");
    }
}
