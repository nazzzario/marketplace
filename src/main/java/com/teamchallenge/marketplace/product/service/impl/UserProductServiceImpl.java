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

    private static final String COMPLAINT_PREFIX = "Complaint_";
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

        if (Objects.nonNull(authentication) && authentication.isAuthenticated()){
            if (authentication.getAuthorities().stream().anyMatch(role ->
                    role.getAuthority().equals(RoleEnum.ADMIN.name()) ||
                            role.getAuthority().equals(RoleEnum.ROOT.name())) ||
                    userRepository.existsByEmailAndProductsReference(authentication.getName(),
                            productReference)){
                ProductEntity productEntity = productRepository.findByReference(productReference)
                        .orElseThrow(() -> new ClientBackendException(ErrorCode.PRODUCT_NOT_FOUND));

                productMapper.patchMerge(requestDto, productEntity);

                return productMapper.toResponseDto(productRepository.save(productEntity),
                        redisTemplate.opsForHash().hasKey(RAISE_AD_PREFIX,
                                productEntity.getReference().toString()));
            } else {throw new ClientBackendException(ErrorCode.USER_NOT_OWN_AD);}
        } else {throw new ClientBackendException(ErrorCode.FORBIDDEN);}
    }

    @Override
    @Transactional
    public void deleteProduct(UUID productReference) {
        var authentication = SecurityContextHolder.getContext().getAuthentication();

        if (Objects.nonNull(authentication) && authentication.isAuthenticated()){
            if (authentication.getAuthorities().stream().anyMatch(role ->
                    role.getAuthority().equals(RoleEnum.ADMIN.name()) ||
                            role.getAuthority().equals(RoleEnum.ROOT.name())) ||
                    userRepository.existsByEmailAndProductsReference(authentication.getName(),
                            productReference)){
                ProductEntity productEntity = productRepository.findByReference(productReference)
                        .orElseThrow(() -> new ClientBackendException(ErrorCode.PRODUCT_NOT_FOUND));

                userRepository.findByFavoriteProducts(productEntity).forEach(user ->
                        user.getFavoriteProducts().remove(productEntity));

                productEntity.getImages().forEach(image -> productImageService.processDeleteImage(image.getId()));

                redisTemplate.opsForHash().delete(RAISE_AD_PREFIX, productEntity.getReference().toString());

                productRepository.delete(productEntity);
            } else { throw new ClientBackendException(ErrorCode.USER_NOT_OWN_AD);}
        } else {
            throw new ClientBackendException(ErrorCode.FORBIDDEN);
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
        if (Objects.nonNull(authentication) && authentication.isAuthenticated()){
            if (authentication.getAuthorities().stream().anyMatch(role ->
                    role.getAuthority().equals(RoleEnum.ADMIN.name()) ||
                            role.getAuthority().equals(RoleEnum.ROOT.name())) ||
                    userRepository.existsByEmailAndProductsReference(authentication.getName(),
                            productReference)){
                if (!status.equals(ProductStatusEnum.NEW)){
                ProductEntity productEntity = productRepository.findByReference(productReference)
                        .orElseThrow(() -> new ClientBackendException(ErrorCode.PRODUCT_NOT_FOUND));

                return productMapper.toResponseDto(getProductAndChangeStatus(productEntity, status,
                        getCorrectPeriod(status)), redisTemplate.opsForHash().hasKey(RAISE_AD_PREFIX,
                        productEntity.getReference().toString()));
                } else {throw  new ClientBackendException(ErrorCode.NOT_CHANGE_STATUS_TO_NEW);}
            } else {throw new ClientBackendException(ErrorCode.USER_NOT_OWN_AD);}
        } else {
            throw new ClientBackendException(ErrorCode.FORBIDDEN);
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
        if (Objects.nonNull(authentication) && authentication.isAuthenticated()){
            if (userRepository.existsByEmailAndProductsReferenceAndProductsStatus(authentication.getName(),
                    productReference, ProductStatusEnum.ACTIVE)){
                if (Boolean.FALSE.equals(redisTemplate.opsForHash().hasKey(RAISE_AD_PREFIX,
                        productReference.toString()))){
                    redisTemplate.opsForHash().increment(RAISE_AD_PREFIX, productReference.toString(), 1);
                } else {throw new ClientBackendException(ErrorCode.LIMIT_IS_EXHAUSTED);}
            } else {throw  new ClientBackendException(ErrorCode.PRODUCT_NOT_ACTIVE);}
        } else {throw  new ClientBackendException(ErrorCode.FORBIDDEN);}
    }

    @Override
    public String complaintProduct(UUID productReference, UUID userReference, String message) {
        redisTemplate.opsForHash().put(COMPLAINT_PREFIX + productReference.toString(),
                userReference.toString(), message);
        return "Дякуємо за звернення. Ми розлянемо вашу скаргу";
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

        ProductEntity firstActiveProduct = productRepository.findByOwnerAndStatus(userEntity,
                        ProductStatusEnum.ACTIVE, PageRequest.of(0, 6))
                .stream().findFirst().orElse(null);

        ProductEntity newProductEntity = new ProductEntity();
        if (firstActiveProduct != null) {
            newProductEntity.setCity(firstActiveProduct.getCity());
            newProductEntity.setRegion(firstActiveProduct.getRegion());
        } else {
            newProductEntity.setCity("Київ");
            newProductEntity.setRegion("Київський");
        }
        newProductEntity.setProductTitle("");
        newProductEntity.setCategoryName(ProductCategoriesEnum.OTHER);
        newProductEntity.setState(ProductStateEnum.USED);
        newProductEntity.setProductDescription("");
        newProductEntity.setStatus(ProductStatusEnum.NEW);
        newProductEntity.setOwner(userEntity);

        return productRepository.save(newProductEntity);
    }
}
