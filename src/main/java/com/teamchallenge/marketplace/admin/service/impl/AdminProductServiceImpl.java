package com.teamchallenge.marketplace.admin.service.impl;

import com.teamchallenge.marketplace.admin.dto.ComplaintDto;
import com.teamchallenge.marketplace.admin.dto.FolderComplaintDto;
import com.teamchallenge.marketplace.admin.service.AdminProductService;
import com.teamchallenge.marketplace.common.exception.ClientBackendException;
import com.teamchallenge.marketplace.common.exception.ErrorCode;
import com.teamchallenge.marketplace.product.dto.response.UserProductResponseDto;
import com.teamchallenge.marketplace.product.mapper.ProductMapper;
import com.teamchallenge.marketplace.product.mapper.UserProductMapper;
import com.teamchallenge.marketplace.product.persisit.entity.enums.ProductStatusEnum;
import com.teamchallenge.marketplace.product.persisit.repository.ProductRepository;
import com.teamchallenge.marketplace.product.service.UserProductService;
import com.teamchallenge.marketplace.user.persisit.entity.UserEntity;
import com.teamchallenge.marketplace.user.persisit.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AdminProductServiceImpl implements AdminProductService {
    public static final String COMPLAINT_PREFIX = "Complaint_";
    private final UserRepository userRepository;
    private final ProductRepository productRepository;
    private final UserProductMapper userProductMapper;
    private final ProductMapper productMapper;
    private final RedisTemplate<String, String> redisTemplate;

    @Override
    public Page<UserProductResponseDto> getProductsWithStatusByUser(UUID userReference, ProductStatusEnum status, Pageable pageable) {
        UserEntity userEntity = userRepository.findByReference(userReference)
                .orElseThrow(() -> new ClientBackendException(ErrorCode.USER_NOT_FOUND));
        return productRepository.findByOwnerAndStatus(userEntity, status, pageable)
                .map(product -> userProductMapper.toResponseDto(product,
                        redisTemplate.opsForHash().hasKey(UserProductService.RAISE_AD_PREFIX, product.getReference().toString())));
    }

    @Override
    public Page<UserProductResponseDto> getFavoriteProductsByUser(UUID userReference, Pageable pageable) {
        UserEntity userEntity = userRepository.findByReference(userReference)
                .orElseThrow(() -> new ClientBackendException(ErrorCode.USER_NOT_FOUND));
        return productRepository.findByFavoritismId(userEntity.getId(), pageable)
                .map(product -> userProductMapper.toResponseDto(product,
                        redisTemplate.opsForHash().hasKey(UserProductService.RAISE_AD_PREFIX,
                                product.getReference().toString())));
    }

    @Override
    public void deleteComplaint(UUID productReference) {
        redisTemplate.delete(COMPLAINT_PREFIX + productReference.toString());
    }

    @Override
    public List<FolderComplaintDto> getComplaintProducts() {
        Set<String> keys = redisTemplate.keys(COMPLAINT_PREFIX + "*");
        if (keys != null) {
            return keys.stream()
                    .map(key -> new FolderComplaintDto(
                            productMapper.toResponseDto(
                                    productRepository.findByReference(
                                            UUID.fromString(key.replace(COMPLAINT_PREFIX, "")))
                                            .orElseThrow(() -> new ClientBackendException(
                                                    ErrorCode.PRODUCT_NOT_FOUND)),
                                    userRepository.findByProductsReference(UUID.fromString(key.replace(
                                            COMPLAINT_PREFIX, ""))).orElseThrow(() ->
                                            new ClientBackendException(ErrorCode.USER_NOT_FOUND))),
                                    redisTemplate.opsForHash().entries(key).entrySet().stream()
                                            .map(complaint -> new ComplaintDto(
                                                    UUID.fromString((String) complaint.getKey()),
                                                    (String) complaint.getValue())
                                            ).toList()
                            )
                    ).toList();
        }
        return List.of();
    }
}
