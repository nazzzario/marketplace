package com.teamchallenge.marketplace.product.service.impl;

import com.teamchallenge.marketplace.common.exception.ClientBackendException;
import com.teamchallenge.marketplace.common.exception.ErrorCode;
import com.teamchallenge.marketplace.product.dto.request.ProductRequestDto;
import com.teamchallenge.marketplace.product.dto.response.ProductNewestResponseDto;
import com.teamchallenge.marketplace.product.dto.response.ProductResponseDto;
import com.teamchallenge.marketplace.product.mapper.ProductMapper;
import com.teamchallenge.marketplace.product.persisit.entity.ProductEntity;
import com.teamchallenge.marketplace.product.persisit.entity.enums.ProductCategoriesEnum;
import com.teamchallenge.marketplace.product.persisit.entity.enums.ProductStateEnum;
import com.teamchallenge.marketplace.product.persisit.entity.enums.ProductStatusEnum;
import com.teamchallenge.marketplace.product.persisit.entity.enums.SortingFieldEnum;
import com.teamchallenge.marketplace.product.persisit.repository.ProductRepository;
import com.teamchallenge.marketplace.product.service.ProductService;
import com.teamchallenge.marketplace.user.persisit.entity.UserEntity;
import com.teamchallenge.marketplace.user.persisit.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.annotations.BatchSize;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

import static com.teamchallenge.marketplace.common.specification.CustomSpecification.fieldEqual;
import static com.teamchallenge.marketplace.common.specification.CustomSpecification.searchLikeString;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {

    private static final String VIEWS_KEY = "productViews";

    private final ProductRepository productRepository;
    private final UserRepository userRepository;
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
    public void createProduct(ProductRequestDto requestDto, UUID userReference) {
        UserEntity userEntity = userRepository.findByReference(userReference)
                .orElseThrow(() -> new ClientBackendException(ErrorCode.USER_NOT_FOUND));

        ProductEntity entity = productMapper.toEntity(requestDto);
        entity.setStatus(ProductStatusEnum.ACTIVE);

        entity.setOwner(userEntity);
        productRepository.save(entity);
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
    public Page<ProductResponseDto> getProductsByProductTitle(String productTitle, String city, Integer page, Integer size) {
        if (Objects.isNull(productTitle)) {
            throw new ClientBackendException(ErrorCode.INVALID_SEARCH_INPUT);
        }

        return productRepository.findAll(searchProductsLikeTitleAndCity(productTitle, city), PageRequest.of(page, size))
                .map(p -> productMapper.toResponseDto(p, p.getOwner()));
    }

    // TODO: 11/22/23 cover image
    @Override
    @Transactional(readOnly = true)
    public Slice<ProductNewestResponseDto> getNewestProducts(Integer page, Integer size) {
        PageRequest pageRequest = PageRequest.of(page, size, Sort.by(SortingFieldEnum.DATE.getFiledName()).descending());

        var productsSortedByCreatedDate = productRepository.findByOrderByCreatedDate(pageRequest);

        return productsSortedByCreatedDate.map(p -> productMapper.toNewestResponseDto(p, p.getOwner()));
    }

    @Override
    @Transactional(readOnly = true)
    @BatchSize(size = 10)
    public Page<ProductResponseDto> getAllProductsByCategory(ProductCategoriesEnum category,
                                                             String city,
                                                             List<ProductStateEnum> states,
                                                             Integer page,
                                                             Integer size,
                                                             SortingFieldEnum sortField) {

        PageRequest pageRequest = PageRequest.of(page, size, Sort.by(sortField.getFiledName()).descending());

        return productRepository.findAll(getProductByCategoryWithFilters(category, city, states), pageRequest)
                .map(p -> productMapper.toResponseDto(p, p.getOwner()));
    }

    @Override
    public Page<ProductResponseDto> getProductByReferenceUser(ProductStatusEnum status, UUID referenceUser, Pageable pageable) {
        var user = userRepository.findByReference(referenceUser).orElseThrow(() ->
                new ClientBackendException(ErrorCode.USER_NOT_FOUND));

        return productRepository.findByOwnerAndStatus(user, status, pageable)
                .map(productEntity -> productMapper.toResponseDto(productEntity,user));
    }


    public void incrementProductViews(UUID productUUID) {
        redisTemplate.opsForHash().increment(VIEWS_KEY, String.valueOf(productUUID), 1);
    }

    @Scheduled(cron = "${product.view.cron}")
    public void updateDatabase() {
        Map<Object, Object> viewsMap = redisTemplate.opsForHash().entries(VIEWS_KEY);

        if (!viewsMap.isEmpty()) {
            for (Map.Entry<Object, Object> entry : viewsMap.entrySet()) {
                UUID productUUID = UUID.fromString((String) entry.getKey());
                int views = Integer.parseInt((String) entry.getValue());

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
    private Specification<ProductEntity> searchProductsLikeTitleAndCity(String searchInput, String city) {
        return Specification.where((Specification<ProductEntity>) searchLikeString("productTitle", searchInput))
                .and((Specification<ProductEntity>) fieldEqual("status", ProductStatusEnum.ACTIVE))
                .and((r, rq, cb) -> Optional.ofNullable(city).map(c -> cb.equal(r.get("city"), city)).orElse(null));
    }

    @SuppressWarnings(value = "unchecked")
    private Specification<ProductEntity> getProductByCategoryWithFilters(ProductCategoriesEnum category,
                                                                         String city,
                                                                         List<ProductStateEnum> states) {
        return Specification.where((Specification<ProductEntity>) fieldEqual("categoryName", category))
                .and((Specification<ProductEntity>) fieldEqual("status", ProductStatusEnum.ACTIVE))
                .and((Specification<ProductEntity>) fieldEqual("city", city))
                .and((r, rq, cb) -> Optional.ofNullable(states).map(s -> r.get("state").in(states)).orElse(null));
    }
}
