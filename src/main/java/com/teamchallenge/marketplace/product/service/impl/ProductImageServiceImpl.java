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
import com.teamchallenge.marketplace.user.persisit.entity.enums.RoleEnum;
import com.teamchallenge.marketplace.user.persisit.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.Objects;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ProductImageServiceImpl implements ProductImageService {
    private final ProductRepository productRepository;
    private final ProductImageRepository imageRepository;
    private final UserRepository userRepository;
    private final FileUpload fileUpload;

    @Value("${product.sizeListImage}")
    private int sizeListImage;

    @Override
    @Transactional
    public UserProductImageDto createImage(UUID productReference, MultipartFile image) {
        var authentication = SecurityContextHolder.getContext().getAuthentication();

        if (Objects.nonNull(authentication) && authentication.isAuthenticated() &&
                (authentication.getAuthorities().contains(new SimpleGrantedAuthority(
                        RoleEnum.ADMIN.name())) ||
                userRepository.existsByEmailAndProductsReference(authentication.getName(),
                        productReference))){
        ProductEntity productEntity = productRepository.findByReference(productReference)
                .orElseThrow(() -> new ClientBackendException(ErrorCode.PRODUCT_NOT_FOUND));

        if (productEntity.getImages().size() >= sizeListImage){
            throw new ClientBackendException(ErrorCode.LIMIT_IS_EXHAUSTED);
        }

        ProductImageEntity emptyImageEntity = new ProductImageEntity();
        emptyImageEntity.setImageUrl("");
        emptyImageEntity.setProduct(productEntity);

        ProductImageEntity transitImageEntity = imageRepository.save(emptyImageEntity);
        transitImageEntity.setImageUrl(fileUpload.uploadFile(image,emptyImageEntity.getReference()));

        ProductImageEntity newImageEntity = imageRepository.save(transitImageEntity);

        return new UserProductImageDto(newImageEntity.getImageUrl(), newImageEntity.getReference(),
                newImageEntity.getId());
        } else {
            throw new ClientBackendException(ErrorCode.UNKNOWN_SERVER_ERROR);
        }
    }

    @Override
    @Transactional
    public UserProductImageDto uploadImages(Long imageId, MultipartFile image) {
        var authentication = SecurityContextHolder.getContext().getAuthentication();

        if (Objects.nonNull(authentication) && authentication.isAuthenticated() &&
                (authentication.getAuthorities().contains(new SimpleGrantedAuthority(
                        RoleEnum.ADMIN.name())) ||
                userRepository.existsByEmailAndProductsImagesId(authentication.getName(),
                        imageId))){
        var imageEntity = imageRepository.findById(imageId).orElseThrow(() ->
                new ClientBackendException(ErrorCode.PRODUCT_NOT_FOUND));
        imageEntity.setImageUrl(fileUpload.uploadFile(image, imageEntity.getReference()));

        var updateImageEntity = imageRepository.save(imageEntity);

        return new UserProductImageDto(updateImageEntity.getImageUrl(),
                updateImageEntity.getReference(), updateImageEntity.getId());
        } else {
            throw new ClientBackendException(ErrorCode.UNKNOWN_SERVER_ERROR);
        }
    }

    @Override
    public void deleteImage(ProductImageEntity entity) {
        fileUpload.deleteFile(entity.getReference());

        imageRepository.delete(entity);
    }

    @Override
    @Transactional
    public void deleteImage(Long imageId) {
        var authentication = SecurityContextHolder.getContext().getAuthentication();

        if (Objects.nonNull(authentication) && authentication.isAuthenticated() &&
                (authentication.getAuthorities().contains(new SimpleGrantedAuthority(
                        RoleEnum.ADMIN.name())) ||
                userRepository.existsByEmailAndProductsImagesId(authentication.getName(),
                        imageId))){
        var imageEntity = imageRepository.findById(imageId).orElseThrow(() ->
                new ClientBackendException(ErrorCode.PRODUCT_NOT_FOUND));
        fileUpload.deleteFile(imageEntity.getReference());

        imageRepository.deleteById(imageId);
        } else {
            throw new ClientBackendException(ErrorCode.UNKNOWN_SERVER_ERROR);
        }
    }
}
