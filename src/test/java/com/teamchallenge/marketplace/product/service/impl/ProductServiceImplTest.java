package com.teamchallenge.marketplace.product.service.impl;

import com.teamchallenge.marketplace.common.security.bean.RootUserInitializer;
import com.teamchallenge.marketplace.product.dto.response.ProductResponseDto;
import com.teamchallenge.marketplace.product.mapper.ProductMapper;
import com.teamchallenge.marketplace.product.persisit.entity.ProductEntity;
import com.teamchallenge.marketplace.product.persisit.entity.enums.ProductCategoriesEnum;
import com.teamchallenge.marketplace.product.persisit.entity.enums.ProductStateEnum;
import com.teamchallenge.marketplace.product.persisit.entity.enums.ProductStatusEnum;
import com.teamchallenge.marketplace.product.persisit.repository.ProductRepository;
import com.teamchallenge.marketplace.product.service.ProductService;
import com.teamchallenge.marketplace.user.persisit.entity.UserEntity;
import com.teamchallenge.marketplace.user.persisit.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE,
        classes = ProductServiceImpl.class)
@ActiveProfiles("test")
class ProductServiceImplTest {
    @Autowired
    private ProductService productService;
    @MockBean
    private UserRepository userRepository;
    @MockBean
    private ProductRepository productRepository;
    @MockBean
    private RedisTemplate<String, String> redisTemplate;
    @MockBean
    private ProductMapper productMapper;
    @MockBean
    RootUserInitializer rootUser;

    @Test
    void getProductByReferenceUser() {
        UserEntity user = new UserEntity();
        PageRequest pageable = PageRequest.of(0, 6, Sort.by("id"));
        PageImpl<ProductEntity> pageEntity = new PageImpl<>(List.of(new ProductEntity()), pageable, 1);
        UUID reference = UUID.randomUUID();
        var productDto = new ProductResponseDto(reference,
                ProductCategoriesEnum.CLOTHE, "Kiev", "Kiev","Clothe", "Clothe",
                "User","0-00-000", ProductStateEnum.USED,
                ProductStatusEnum.ACTIVE, 1, LocalDate.parse("2024-01-01"), List.of("image"));

        when(userRepository.findByReference(reference)).thenReturn(Optional.of(user));
        when(productRepository.findByOwnerAndStatus(eq(user), eq(ProductStatusEnum.ACTIVE),
                any(Pageable.class))).thenReturn(pageEntity);
        when(productMapper.toResponseDto(any(ProductEntity.class), eq(user))).thenReturn(productDto);

        var products = productService.getProductByReferenceUser(ProductStatusEnum.ACTIVE,
                reference, pageable);

        assertThat(products.stream().count()).isEqualTo(1);
        assertThat(products.stream().toList()).contains(productDto);
        assertThat(products.stream().toList().get(0).reference()).isEqualTo(reference);
        assertThat(products.stream().toList().get(0).status()).isEqualTo(ProductStatusEnum.ACTIVE);
        assertThat(products.stream().toList().get(0).categoryName()).isEqualTo(ProductCategoriesEnum.CLOTHE);
        assertThat(products.stream().toList().get(0).city()).isEqualTo("Kiev");
        assertThat(products.stream().toList().get(0).state()).isEqualTo(ProductStateEnum.USED);
        assertThat(products.stream().toList().get(0).images()).contains("image");
    }
}