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
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserProductServiceImpl implements UserProductService {
    private ProductRepository productRepository;
    private UserRepository userRepository;
    private UserProductMapper productMapper;
    private ProductImageService productImageService;

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
        ProductEntity productEntity = productRepository.findByReference(productReference)
                .orElseThrow(() -> new ClientBackendException(ErrorCode.PRODUCT_NOT_FOUND));

        productMapper.patchMerge(requestDto, productEntity);

        return productMapper.toResponseDto(productRepository.save(productEntity));
    }

    @Override
    public void deleteProduct(UUID productReference) {
        ProductEntity productEntity = productRepository.findByReference(productReference)
                .orElseThrow(() -> new ClientBackendException(ErrorCode.PRODUCT_NOT_FOUND));

        productEntity.getImages().forEach(image -> productImageService.deleteImages(image.getId()));

        productRepository.delete(productEntity);
    }

    @Override
    public UserProductResponseDto changeStatusProduct(UUID productReference, ProductStatusEnum status) {
        ProductEntity productEntity = productRepository.findByReference(productReference)
                .orElseThrow(() -> new ClientBackendException(ErrorCode.PRODUCT_NOT_FOUND));
        productEntity.setStatus(status);

        return productMapper.toResponseDto(productRepository.save(productEntity));
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
