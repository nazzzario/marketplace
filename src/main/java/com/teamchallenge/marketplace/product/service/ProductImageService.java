package com.teamchallenge.marketplace.product.service;

import com.teamchallenge.marketplace.product.dto.response.UserProductImageDto;
import com.teamchallenge.marketplace.product.persisit.entity.ProductImageEntity;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

public interface ProductImageService {
    UserProductImageDto createImage(UUID productReference, MultipartFile image);

    UserProductImageDto uploadImages(Long imageId, MultipartFile image);

    void deleteImage(Long imageId);

    void deleteImage(ProductImageEntity entity);

    void processDeleteImage(Long imageId);
}
