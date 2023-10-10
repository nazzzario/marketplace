package com.teamchallenge.marketplace.product.service.impl;

import com.teamchallenge.marketplace.common.exception.ClientBackendException;
import com.teamchallenge.marketplace.common.exception.ErrorCode;
import com.teamchallenge.marketplace.common.file.FileUpload;
import com.teamchallenge.marketplace.product.dto.request.ProductRequestDto;
import com.teamchallenge.marketplace.product.dto.response.ProductResponseDto;
import com.teamchallenge.marketplace.product.mapper.ProductMapper;
import com.teamchallenge.marketplace.product.persisit.entity.ProductEntity;
import com.teamchallenge.marketplace.product.persisit.entity.ProductImageEntity;
import com.teamchallenge.marketplace.product.persisit.repository.ProductImageRepository;
import com.teamchallenge.marketplace.product.persisit.repository.ProductRepository;
import com.teamchallenge.marketplace.product.service.ProductService;
import com.teamchallenge.marketplace.user.persisit.entity.UserEntity;
import com.teamchallenge.marketplace.user.persisit.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    private final ProductImageRepository productImageRepository;
    private final UserRepository userRepository;
    private final FileUpload fileUpload;

    private final ProductMapper productMapper;

    @Override
    public ProductResponseDto getProductByReference(UUID reference) {
        ProductEntity productEntity = productRepository.findByReference(reference)
                .orElseThrow(() -> new ClientBackendException(ErrorCode.PRODUCT_NOT_FOUND));

        return productMapper.toResponseDto(productEntity, productEntity.getOwner());
    }

    @Override
    @Transactional
    public ProductResponseDto createProduct(ProductRequestDto requestDto,  UUID userReference) {
        UserEntity userEntity = userRepository.findByReference(userReference)
                .orElseThrow(() -> new ClientBackendException(ErrorCode.USER_NOT_FOUND));

        ProductEntity entity = productMapper.toEntity(requestDto);

        entity.setOwner(userEntity);
        ProductEntity savedEntity = productRepository.save(entity);

        return productMapper.toResponseDto(savedEntity, userEntity);
    }

    @Override
    @Transactional
    public void deleteProduct(UUID productReference) {
        productRepository.deleteByReference(productReference);
    }

    @Override
    public ProductResponseDto putProduct(ProductRequestDto requestDto) {
        return null;
    }

    @Override
    public ProductResponseDto patchProduct(ProductRequestDto requestDto) {
        return null;
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProductResponseDto> getAllProducts() {
        return productRepository.findAll().stream()
                .map(p -> productMapper.toResponseDto(p, p.getOwner()))
                .toList();
    }

    @Override
    public List<ProductResponseDto> getProductsByProductTitle(String productTitle) {
        if (Objects.isNull(productTitle)) {
            throw new ClientBackendException(ErrorCode.INVALID_SEARCH_INPUT);
        }
        return productRepository.findByProductTitleLikeIgnoreCase("%" + productTitle + "%")
                .stream().map(p -> productMapper.toResponseDto(p, p.getOwner()))
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public Slice<ProductResponseDto> getNewestProducts(Integer page, Integer size) {
        PageRequest pageRequest = PageRequest.of(page, size);

        var productsSortedByCreatedDate = productRepository.findByOrderByCreatedDate(pageRequest);

        return productsSortedByCreatedDate.map(p -> productMapper.toResponseDto(p, p.getOwner()));
    }

    @Transactional
    @Override
    public ProductResponseDto uploadImagesToProduct(UUID productReference, List<MultipartFile> images) {
        ProductEntity productEntity = productRepository.findByReference(productReference)
                .orElseThrow(() -> new ClientBackendException(ErrorCode.PRODUCT_NOT_FOUND));

        List<ProductImageEntity> productImagesUrlEntity = fileUpload.uploadFiles(images).stream()
                .map(productMapper::toProductImage)
                .toList();

        productImagesUrlEntity.forEach(pi -> pi.setProduct(productEntity));
        productImageRepository.saveAll(productImagesUrlEntity);
        productEntity.setImages(productImagesUrlEntity);

        return productMapper.toResponseDto(productEntity, productEntity.getOwner());
    }
}
