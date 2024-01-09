package com.teamchallenge.marketplace.product.service.impl;

import com.teamchallenge.marketplace.common.exception.ClientBackendException;
import com.teamchallenge.marketplace.common.exception.ErrorCode;
import com.teamchallenge.marketplace.common.file.FileUpload;
import com.teamchallenge.marketplace.product.dto.response.UserProductImageDto;
import com.teamchallenge.marketplace.product.persisit.entity.ProductEntity;
import com.teamchallenge.marketplace.product.persisit.entity.ProductImageEntity;
import com.teamchallenge.marketplace.product.persisit.repository.ProductImageRepository;
import com.teamchallenge.marketplace.product.persisit.repository.ProductRepository;
import com.teamchallenge.marketplace.product.service.ProductImageService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ProductImageServiceImpl implements ProductImageService {
    private final ProductRepository productRepository;
    private final ProductImageRepository imageRepository;
    private final FileUpload fileUpload;

    @Value("${product.sizeListImage}")
    private int sizeListImage;

    @Override
    public UserProductImageDto createImage(UUID productReference, MultipartFile image) {
        ProductEntity productEntity = productRepository.findByReference(productReference)
                .orElseThrow(() -> new ClientBackendException(ErrorCode.PRODUCT_NOT_FOUND));

        if (productEntity.getImages().size() >= sizeListImage){
            throw new ClientBackendException(ErrorCode.LIMIT_IS_EXHAUSTED);
        }

        ProductImageEntity emptyImageEntity = imageRepository.save(new ProductImageEntity());
        emptyImageEntity.setProduct(productEntity);
        emptyImageEntity.setImageUrl(fileUpload.uploadFile(image,emptyImageEntity.getReference()));

        ProductImageEntity newImageEntity = imageRepository.save(emptyImageEntity);

        return new UserProductImageDto(newImageEntity.getImageUrl(),
                newImageEntity.getId());
    }

    @Override
    public UserProductImageDto uploadImages(Long imageId, MultipartFile image) {
        var imageEntity = imageRepository.findById(imageId).orElseThrow(() ->
                new ClientBackendException(ErrorCode.PRODUCT_NOT_FOUND));
        imageEntity.setImageUrl(fileUpload.uploadFile(image, imageEntity.getReference()));

        var updateImageEntity = imageRepository.save(imageEntity);

        return new UserProductImageDto(updateImageEntity.getImageUrl(),
                updateImageEntity.getId());
    }

    @Override
    public void deleteImages(Long imageId) {
        var imageEntity = imageRepository.findById(imageId).orElseThrow(() ->
                new ClientBackendException(ErrorCode.PRODUCT_NOT_FOUND));
        fileUpload.deleteFile(imageEntity.getReference());

        imageRepository.deleteById(imageId);
    }
}
