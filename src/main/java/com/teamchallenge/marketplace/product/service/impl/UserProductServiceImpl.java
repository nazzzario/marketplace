package com.teamchallenge.marketplace.product.service.impl;

import com.teamchallenge.marketplace.common.exception.ClientBackendException;
import com.teamchallenge.marketplace.common.exception.ErrorCode;
import com.teamchallenge.marketplace.product.dto.request.ProductRequestDto;
import com.teamchallenge.marketplace.product.dto.response.UserProductResponseDto;
import com.teamchallenge.marketplace.product.mapper.UserProductMapper;
import com.teamchallenge.marketplace.product.persisit.entity.ProductEntity;
import com.teamchallenge.marketplace.product.persisit.entity.enums.ProductCategoriesEnum;
import com.teamchallenge.marketplace.product.persisit.entity.enums.ProductStateEnum;
import com.teamchallenge.marketplace.product.persisit.entity.enums.ProductStatusEnum;
import com.teamchallenge.marketplace.product.persisit.repository.ProductRepository;
import com.teamchallenge.marketplace.product.service.ProductImageService;
import com.teamchallenge.marketplace.product.service.UserProductService;
import com.teamchallenge.marketplace.user.persisit.entity.UserEntity;
import com.teamchallenge.marketplace.user.persisit.entity.enums.RoleEnum;
import com.teamchallenge.marketplace.user.persisit.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserProductServiceImpl implements UserProductService {

    @Value("${product.active.periodsDeadline}")
    private int periodsActive;
    @Value("${product.delete.periodDeadline}")
    private int periodDisabled;
    @Value("${product.disable.size}")
    private int sizeProductDisabled;

    private final ProductRepository productRepository;
    private final UserRepository userRepository;
    private final UserProductMapper productMapper;
    private final ProductImageService productImageService;
    private final RedisTemplate<String, String> redisTemplate;

    @Override
    public UserProductResponseDto createOrGetNewProduct() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        UserEntity userEntity = userRepository.findByEmail(authentication.getName())
                .orElseThrow(() -> new ClientBackendException(ErrorCode.USER_NOT_FOUND));

        ProductEntity productEntity = productRepository.findByOwnerAndStatus(userEntity, ProductStatusEnum.NEW,
                PageRequest.of(0, 6)).stream().findFirst().orElse(null);

        return productMapper.toResponseDto(Objects.requireNonNullElseGet(productEntity, () -> getNewProduct(userEntity)),
                false);

    }

    @Override
    public UserProductResponseDto patchProduct(ProductRequestDto requestDto, UUID productReference) {
        var authentication = SecurityContextHolder.getContext().getAuthentication();

        if (Objects.nonNull(authentication) && authentication.isAuthenticated() &&
                (authentication.getAuthorities().stream().anyMatch(role ->
                        role.getAuthority().equals(RoleEnum.ADMIN.name()) ||
                        role.getAuthority().equals(RoleEnum.ROOT.name())) ||
                userRepository.existsByEmailAndProductsReference(authentication.getName(),
                        productReference))) {
            ProductEntity productEntity = productRepository.findByReference(productReference)
                    .orElseThrow(() -> new ClientBackendException(ErrorCode.PRODUCT_NOT_FOUND));

            productMapper.patchMerge(requestDto, productEntity);

            return productMapper.toResponseDto(productRepository.save(productEntity),
                    redisTemplate.opsForHash().hasKey(RAISE_AD_PREFIX,
                            productEntity.getReference().toString()));
        } else {
            throw new ClientBackendException(ErrorCode.UNKNOWN_SERVER_ERROR);
        }
    }

    @Override
    @Transactional
    public void deleteProduct(UUID productReference) {
        var authentication = SecurityContextHolder.getContext().getAuthentication();

        if (Objects.nonNull(authentication) && authentication.isAuthenticated() &&
                (authentication.getAuthorities().stream().anyMatch(role ->
                        role.getAuthority().equals(RoleEnum.ADMIN.name()) ||
                                role.getAuthority().equals(RoleEnum.ROOT.name())) ||
                userRepository.existsByEmailAndProductsReference(authentication.getName(),
                        productReference))) {
            ProductEntity productEntity = productRepository.findByReference(productReference)
                    .orElseThrow(() -> new ClientBackendException(ErrorCode.PRODUCT_NOT_FOUND));

            userRepository.findByFavoriteProducts(productEntity).forEach(user ->
                    user.getFavoriteProducts().remove(productEntity));

            productEntity.getImages().forEach(image -> productImageService.processDeleteImage(image.getId()));

            redisTemplate.opsForHash().delete(RAISE_AD_PREFIX, productEntity.getReference().toString());

            productRepository.delete(productEntity);
        } else {
            throw new ClientBackendException(ErrorCode.UNKNOWN_SERVER_ERROR);
        }
    }

    @Override
    @Transactional
    public void processDeleteProduct(ProductEntity productEntity) {
        userRepository.findByFavoriteProducts(productEntity).forEach(user ->
                user.getFavoriteProducts().remove(productEntity));

        productEntity.getImages().forEach(image -> productImageService.processDeleteImage(image.getId()));

        redisTemplate.opsForHash().delete(RAISE_AD_PREFIX, productEntity.getReference().toString());

        productRepository.delete(productEntity);
    }

    @Override
    public UserProductResponseDto changeStatusProduct(UUID productReference, ProductStatusEnum status, int period) {
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        if (Objects.nonNull(authentication) && authentication.isAuthenticated() &&
                (authentication.getAuthorities().stream().anyMatch(role ->
                        role.getAuthority().equals(RoleEnum.ADMIN.name()) ||
                                role.getAuthority().equals(RoleEnum.ROOT.name())) ||
                userRepository.existsByEmailAndProductsReference(authentication.getName(),
                        productReference) && !status.equals(ProductStatusEnum.NEW))) {

            ProductEntity productEntity = productRepository.findByReference(productReference)
                    .orElseThrow(() -> new ClientBackendException(ErrorCode.PRODUCT_NOT_FOUND));

            return productMapper.toResponseDto(getProductAndChangeStatus(productEntity, status,
                    getCorrectPeriod(status)), redisTemplate.opsForHash().hasKey(RAISE_AD_PREFIX,
                    productEntity.getReference().toString()));
        } else {
            throw new ClientBackendException(ErrorCode.UNKNOWN_SERVER_ERROR);
        }

    }

    @Override
    public ProductEntity getProductAndChangeStatus(ProductEntity product, ProductStatusEnum status, int period) {
        product.setTimePeriod(period);
        product.setStatus(status);
        return productRepository.save(product);
    }

    @Override
    public void raiseAdProduct(UUID productReference) {
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        if (Objects.nonNull(authentication) &&
                authentication.isAuthenticated() &&
                userRepository.existsByEmailAndProductsReference(authentication.getName(),
                        productReference) &&
                Boolean.FALSE.equals(redisTemplate.opsForHash().hasKey(RAISE_AD_PREFIX,
                        productReference.toString()))) {
            redisTemplate.opsForHash().increment(RAISE_AD_PREFIX, productReference.toString(), 1);
        } else {throw  new ClientBackendException(ErrorCode.LIMIT_IS_EXHAUSTED);}
    }

    @Override
    public List<ProductEntity> getAllProductByUser(UserEntity user) {
        return productRepository.findByOwner(user);
    }

    /**
     * Period with status Active get period from variable.
     * It with status Disabled get period from variable.
     *
     * @param status Status of product.
     */
    private int getCorrectPeriod(ProductStatusEnum status) {
        if (status.equals(ProductStatusEnum.ACTIVE)) {
            return periodsActive;
        } else {
            return periodDisabled;
        }
    }

    /**
     * Create new product with values of first product with status active or default.
     *
     * @param userEntity User is authentication.
     */
    private ProductEntity getNewProduct(UserEntity userEntity) {
        ProductEntity newProductEntity;

        ProductEntity firstActiveProduct = productRepository.findByOwnerAndStatus(userEntity,
                        ProductStatusEnum.ACTIVE, PageRequest.of(0, 6))
                .stream().findFirst().orElse(null);

        if (firstActiveProduct != null) {
            newProductEntity = productMapper.toNewEntity(firstActiveProduct);
        } else {
            newProductEntity = new ProductEntity();
            newProductEntity.setProductTitle("Title");
            newProductEntity.setProductDescription("Description");
            newProductEntity.setCity("Київ");
            newProductEntity.setCategoryName(ProductCategoriesEnum.OTHER);
            newProductEntity.setState(ProductStateEnum.USED);
        }
        newProductEntity.setStatus(ProductStatusEnum.NEW);
        newProductEntity.setOwner(userEntity);

        return productRepository.save(newProductEntity);
    }
}
