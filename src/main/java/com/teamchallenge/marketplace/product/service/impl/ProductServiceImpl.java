package com.teamchallenge.marketplace.product.service.impl;

import com.teamchallenge.marketplace.common.exception.ClientBackendException;
import com.teamchallenge.marketplace.common.exception.ErrorCode;
import com.teamchallenge.marketplace.common.file.FileUpload;
import com.teamchallenge.marketplace.product.dto.request.ProductRequestDto;
import com.teamchallenge.marketplace.product.dto.response.ProductResponseDto;
import com.teamchallenge.marketplace.product.mapper.ProductMapper;
import com.teamchallenge.marketplace.product.persisit.entity.ProductEntity;
import com.teamchallenge.marketplace.product.persisit.entity.ProductImageEntity;
import com.teamchallenge.marketplace.product.persisit.entity.enums.*;
import com.teamchallenge.marketplace.product.persisit.repository.ProductImageRepository;
import com.teamchallenge.marketplace.product.persisit.repository.ProductRepository;
import com.teamchallenge.marketplace.product.service.ProductService;
import com.teamchallenge.marketplace.user.persisit.entity.UserEntity;
import com.teamchallenge.marketplace.user.persisit.repository.UserRepository;
import jakarta.persistence.criteria.Predicate;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.annotations.BatchSize;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.*;
import java.util.concurrent.TimeUnit;

import static com.teamchallenge.marketplace.common.specification.CustomSpecification.fieldEqual;
import static com.teamchallenge.marketplace.common.specification.CustomSpecification.searchLikeString;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {

    private static final String VIEWS_KEY = "productViews";
    private final ProductRepository productRepository;
    private final ProductImageRepository productImageRepository;
    private final UserRepository userRepository;
    private final FileUpload fileUpload;
    private final RedisTemplate<String, String> redisTemplate;

    private final ProductMapper productMapper;

    // TODO: 11/8/23 Find better solution maybe Redis 
    @Override
    @Transactional
    public ProductResponseDto getProductByReference(UUID reference) {
        ProductEntity productEntity = productRepository.findByReference(reference)
                .orElseThrow(() -> new ClientBackendException(ErrorCode.PRODUCT_NOT_FOUND));
        incrementProductViews(reference);
        return productMapper.toResponseDto(productEntity, productEntity.getOwner());
    }

    @Override
    @Transactional
    public ProductResponseDto createProduct(ProductRequestDto requestDto) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        UserEntity userEntity = userRepository.findByEmail(authentication.getName())
                .orElseThrow(() -> new ClientBackendException(ErrorCode.USER_NOT_FOUND));

        ProductEntity entity = productMapper.toEntity(requestDto);
        entity.setStatus(ProductStatusEnum.ACTIVE);

        entity.setOwner(userEntity);
        ProductEntity savedEntity = productRepository.save(entity);

        return productMapper.toResponseDto(savedEntity, userEntity);
    }

    @Override
    public void createProduct(ProductRequestDto requestDto, UUID userReference) {
        UserEntity userEntity = userRepository.findByReference(userReference)
                .orElseThrow(() -> new ClientBackendException(ErrorCode.USER_NOT_FOUND));

        ProductEntity entity = productMapper.toEntity(requestDto);

        entity.setOwner(userEntity);
        productRepository.save(entity);
    }

    @Override
    @Transactional
    public void deleteProduct(UUID productReference) {
        ProductEntity productEntity = productRepository.findByReference(productReference)
                .orElseThrow(() -> new ClientBackendException(ErrorCode.PRODUCT_NOT_FOUND));

        productRepository.deleteByReference(productReference);
    }

    @Override
    public ProductResponseDto putProduct(ProductRequestDto requestDto) {
        return null;
    }

    @Override
    @Transactional
    public ProductResponseDto patchProduct(ProductRequestDto requestDto, UUID productReference) {
        ProductEntity productEntity = productRepository.findByReference(productReference)
                .orElseThrow(() -> new ClientBackendException(ErrorCode.PRODUCT_NOT_FOUND));

        productMapper.patchMerge(requestDto, productEntity);

        return productMapper.toResponseDto(productEntity, productEntity.getOwner());
    }

    @Override
    @Transactional(readOnly = true)
    @BatchSize(size = 10)
    public List<ProductResponseDto> getAllProducts() {
        return productRepository.findAll().stream()
                .map(p -> productMapper.toResponseDto(p, p.getOwner()))
                .toList();
    }

    @Override
    public Page<ProductResponseDto> getProductsByProductTitle(String productTitle, CitiesEnum city, Integer page, Integer size) {
        if (Objects.isNull(productTitle)) {
            throw new ClientBackendException(ErrorCode.INVALID_SEARCH_INPUT);
        }

        return productRepository.findAll(searchProductsLikeTitleAndCity(productTitle, city), PageRequest.of(page, size))
                .map(p -> productMapper.toResponseDto(p, p.getOwner()));
    }

    @Override
    @Transactional(readOnly = true)
    public Slice<ProductResponseDto> getNewestProducts(Integer page, Integer size) {
        PageRequest pageRequest = PageRequest.of(page, size, Sort.by(SortingFieldEnum.DATE.getFiledName()).descending());

        var productsSortedByCreatedDate = productRepository.findByOrderByCreatedDate(pageRequest);

        return productsSortedByCreatedDate.map(p -> productMapper.toResponseDto(p, p.getOwner()));
    }

    @Override
    @Transactional
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

    @Override
    @Transactional(readOnly = true)
    @BatchSize(size = 10)
    public Page<ProductResponseDto> getAllProductsByCategory(ProductCategoriesEnum category,
                                                             CitiesEnum city,
                                                             List<ProductStateEnum> states,
                                                             Integer page,
                                                             Integer size,
                                                             SortingFieldEnum sortField) {

        PageRequest pageRequest = PageRequest.of(page, size, Sort.by(sortField.getFiledName()).descending());

        return productRepository.findAll(getProductByCategoryWithFilters(category, city, states), pageRequest)
                .map(p -> productMapper.toResponseDto(p, p.getOwner()));
    }

    public void incrementProductViews(UUID productUUID) {
        redisTemplate.opsForHash().increment(VIEWS_KEY, String.valueOf(productUUID), 1);
    }

    @Scheduled(fixedDelay = 30, timeUnit = TimeUnit.MINUTES)
    @Transactional
    public void updateDatabase() {
        Map<Object, Object> viewsMap = redisTemplate.opsForHash().entries(VIEWS_KEY);

        if (!viewsMap.isEmpty()) {
            for (Map.Entry<Object, Object> entry : viewsMap.entrySet()) {
                UUID productUUID = UUID.fromString((String) entry.getKey());
                Integer views = Integer.parseInt((String) entry.getValue());

                Optional<ProductEntity> byReference = productRepository.findByReference(productUUID);
                if (byReference.isPresent()) {
                    ProductEntity productEntity = byReference.get();
                    productEntity.setViewCount(productEntity.getViewCount() + views);
                }
            }
        }

        redisTemplate.delete(VIEWS_KEY);
    }

    @SuppressWarnings(value = "unchecked")
    private Specification<ProductEntity> searchProductsLikeTitleAndCity(String searchInput, CitiesEnum city) {
        return Specification.where((Specification<ProductEntity>) searchLikeString("productTitle", searchInput))
                .and((Specification<ProductEntity>) fieldEqual("status", ProductStatusEnum.ACTIVE))
                .and((r, rq, cb) -> Optional.ofNullable(city).map(c -> cb.equal(r.get("city"), city)).orElse(null));
    }

    @SuppressWarnings(value = "unchecked")
    private Specification<ProductEntity> getProductByCategoryWithFilters(ProductCategoriesEnum category,
                                                                         CitiesEnum city,
                                                                         List<ProductStateEnum> states) {
        return Specification.where((Specification<ProductEntity>) fieldEqual("categoryName", category))
                .and((Specification<ProductEntity>) fieldEqual("status", ProductStatusEnum.ACTIVE))
                .and((Specification<ProductEntity>) fieldEqual("city", city))
                .and((r, rq, cb) -> Optional.ofNullable(states).map(s -> r.get("state").in(states)).orElse(null));
    }
}
