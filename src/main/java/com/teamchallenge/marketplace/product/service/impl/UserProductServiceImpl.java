package com.teamchallenge.marketplace.product.service.impl;

import com.teamchallenge.marketplace.common.exception.ClientBackendException;
import com.teamchallenge.marketplace.common.exception.ErrorCode;
import com.teamchallenge.marketplace.product.dto.request.ProductRequestDto;
import com.teamchallenge.marketplace.product.dto.response.UserProductResponseDto;
import com.teamchallenge.marketplace.product.mapper.UserProductMapper;
import com.teamchallenge.marketplace.product.persisit.entity.ProductEntity;
import com.teamchallenge.marketplace.product.persisit.entity.enums.CitiesEnum;
import com.teamchallenge.marketplace.product.persisit.entity.enums.ProductCategoriesEnum;
import com.teamchallenge.marketplace.product.persisit.entity.enums.ProductStateEnum;
import com.teamchallenge.marketplace.product.persisit.entity.enums.ProductStatusEnum;
import com.teamchallenge.marketplace.product.persisit.repository.ProductRepository;
import com.teamchallenge.marketplace.product.service.ProductImageService;
import com.teamchallenge.marketplace.product.service.UserProductService;
import com.teamchallenge.marketplace.user.persisit.entity.UserEntity;
import com.teamchallenge.marketplace.user.persisit.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.Objects;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserProductServiceImpl implements UserProductService {
    private final ProductRepository productRepository;
    private final UserRepository userRepository;
    private final UserProductMapper productMapper;
    private final ProductImageService productImageService;

    @Value("${product.sizeProductDisabled}")
    private int sizeProductDisabled;

    @Override
    public UserProductResponseDto createOrGetNewProduct() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        UserEntity userEntity = userRepository.findByEmail(authentication.getName())
                .orElseThrow(() -> new ClientBackendException(ErrorCode.USER_NOT_FOUND));

        return productMapper.toResponseDto(
                productRepository.findByOwnerAndStatus(userEntity, ProductStatusEnum.NEW,
                PageRequest.of(0, 6)).stream().findFirst()
                .orElse(getNewProduct(userEntity))
        );

    }

    @Override
    public UserProductResponseDto patchProduct(ProductRequestDto requestDto, UUID productReference) {
        var authentication = SecurityContextHolder.getContext().getAuthentication();

        if (Objects.nonNull(authentication) && authentication.isAuthenticated() &&
                userRepository.existsByEmailAndProductsReference(authentication.getName(),
                        productReference)){
        ProductEntity productEntity = productRepository.findByReference(productReference)
                .orElseThrow(() -> new ClientBackendException(ErrorCode.PRODUCT_NOT_FOUND));

        productMapper.patchMerge(requestDto, productEntity);

        return productMapper.toResponseDto(productRepository.save(productEntity));
        } else {
            throw new ClientBackendException(ErrorCode.UNKNOWN_SERVER_ERROR);
        }
    }

    @Override
    public void deleteProduct(UUID productReference) {
        var authentication = SecurityContextHolder.getContext().getAuthentication();

        if (Objects.nonNull(authentication) && authentication.isAuthenticated() &&
                userRepository.existsByEmailAndProductsReference(authentication.getName(),
                        productReference)){
        ProductEntity productEntity = productRepository.findByReference(productReference)
                .orElseThrow(() -> new ClientBackendException(ErrorCode.PRODUCT_NOT_FOUND));

        productEntity.getImages().forEach(image -> productImageService.deleteImage(image.getId()));

        productRepository.delete(productEntity);
        } else {
            throw new ClientBackendException(ErrorCode.UNKNOWN_SERVER_ERROR);
        }
    }

    @Override
    public UserProductResponseDto changeStatusProduct(UUID productReference, ProductStatusEnum status) {
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        if (Objects.nonNull(authentication) && authentication.isAuthenticated()){
            String email = authentication.getName();
            UserEntity userEntity = userRepository.findByEmail(email)
                    .orElseThrow(() -> new ClientBackendException(ErrorCode.USER_NOT_FOUND));

            ProductEntity productEntity = productRepository.findByReference(productReference)
                    .orElseThrow(() -> new ClientBackendException(ErrorCode.PRODUCT_NOT_FOUND));

            if (status.equals(ProductStatusEnum.DISABLED) &&
                    userEntity.getProducts().size() >= sizeProductDisabled){
                throw new ClientBackendException(ErrorCode.LIMIT_IS_EXHAUSTED);
            }

            productEntity.setStatus(status);

            return productMapper.toResponseDto(productRepository.save(productEntity));
        } else {
            throw new ClientBackendException(ErrorCode.UNKNOWN_SERVER_ERROR);
        }


    }

    /**
     * Create new product with values of first product with status active or default.
     *
     * @param userEntity User is authentication.
     * */
    private ProductEntity getNewProduct(UserEntity userEntity) {
        ProductEntity newProductEntity;

        ProductEntity firstActiveProduct = productRepository.findByOwnerAndStatus(userEntity,
                ProductStatusEnum.ACTIVE, PageRequest.of(0, 6))
                .stream().findFirst().orElse(null);

        if (firstActiveProduct != null){
            newProductEntity = productMapper.toNewEntity(firstActiveProduct);
        }else {
            newProductEntity = new ProductEntity();
            newProductEntity.setProductTitle("Title");
            newProductEntity.setProductDescription("Description");
            newProductEntity.setCity(CitiesEnum.KYIV);
            newProductEntity.setCategoryName(ProductCategoriesEnum.OTHER);
            newProductEntity.setState(ProductStateEnum.USED);
        }
        newProductEntity.setStatus(ProductStatusEnum.NEW);
        newProductEntity.setOwner(userEntity);

        return productRepository.save(newProductEntity);
    }
}
