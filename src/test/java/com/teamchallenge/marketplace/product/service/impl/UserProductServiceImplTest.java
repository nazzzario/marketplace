package com.teamchallenge.marketplace.product.service.impl;

import com.teamchallenge.marketplace.product.dto.response.UserProductImageDto;
import com.teamchallenge.marketplace.product.dto.response.UserProductResponseDto;
import com.teamchallenge.marketplace.product.mapper.UserProductMapper;
import com.teamchallenge.marketplace.product.persisit.entity.ProductEntity;
import com.teamchallenge.marketplace.product.persisit.entity.enums.ProductCategoriesEnum;
import com.teamchallenge.marketplace.product.persisit.entity.enums.ProductStateEnum;
import com.teamchallenge.marketplace.product.persisit.entity.enums.ProductStatusEnum;
import com.teamchallenge.marketplace.product.persisit.repository.ProductRepository;
import com.teamchallenge.marketplace.product.service.ShowUserProductService;
import com.teamchallenge.marketplace.user.persisit.entity.UserEntity;
import com.teamchallenge.marketplace.user.persisit.repository.UserRepository;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE,
        classes = ShowUserProductServiceImpl.class)
@WithMockUser
@TestPropertySource(properties = {"spring.security.jwt.secret=testSecretKey",
        "spring.security.jwt.expiration=1"})
@ActiveProfiles("test")
class UserProductServiceImplTest {
    @Autowired
    private ShowUserProductService productService;

    @MockBean
    private ProductRepository productRepository;
    @MockBean
    private UserRepository userRepository;
    @MockBean
    private UserProductMapper productMapper;
    @MockBean
    private Authentication authenticationMock;

    UserEntity userEntity = new UserEntity();
    PageRequest pageable = PageRequest.of(0, 6, Sort.by("id"));
    PageImpl<ProductEntity> pageEntity = new PageImpl<>(List.of(new ProductEntity()), pageable, 1);
    UUID reference = UUID.randomUUID();
    UserProductImageDto imageDto = new UserProductImageDto("url", 1L);
    UserProductResponseDto responseDto = new UserProductResponseDto(reference,
            ProductCategoriesEnum.CLOTHE, "Kiev","Clothe", "Clothe",
            ProductStateEnum.USED, ProductStatusEnum.ACTIVE, 1, LocalDate.parse("2024-01-01"), List.of(imageDto));
    PageImpl<UserProductResponseDto> pageDto = new PageImpl<>(List.of(responseDto), pageable, 1);
    SecurityContext securityContext;

    @BeforeEach
    void init(){
        userEntity.setId(1L);
        securityContext = SecurityContextHolder.createEmptyContext();
        securityContext.setAuthentication(authenticationMock);
        SecurityContextHolder.setContext(securityContext);

        when(authenticationMock.isAuthenticated()).thenReturn(true);
        when(authenticationMock.getName()).thenReturn("fucke@user");
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(userEntity));
        when(productMapper.toResponseDto(any(ProductEntity.class))).thenReturn(responseDto);
    }

    @SneakyThrows
    @Test
    void getProductsWithStatusByUser() {
        when(productRepository.findByOwnerAndStatus(userEntity, ProductStatusEnum.ACTIVE, pageable))
                .thenReturn(pageEntity);

        Page<UserProductResponseDto> products = productService.getProductsWithStatusByUser(
                ProductStatusEnum.ACTIVE, pageable);

        assertThat(products.stream().count()).isEqualTo(1);
        assertThat(products.stream().toList()).contains(responseDto);
        assertEquals(products.stream().toList().get(0).reference(), reference);
        assertThat(products.stream().toList().get(0).status()).isEqualTo(ProductStatusEnum.ACTIVE);
        assertThat(products.stream().toList().get(0).categoryName()).isEqualTo(ProductCategoriesEnum.CLOTHE);
        assertThat(products.stream().toList().get(0).city()).isEqualTo("Kiev");
        assertThat(products.stream().toList().get(0).state()).isEqualTo(ProductStateEnum.USED);
        assertThat(products.stream().toList().get(0).images()).contains(imageDto);
    }

    @Test
    void getFavoriteProductsByUser() {
        when(productRepository.findByFavoritismId(anyLong(), eq(pageable)))
                .thenReturn(pageEntity);

        Page<UserProductResponseDto> products = productService.getFavoriteProductsByUser(pageable);

        assertThat(products.stream().count()).isEqualTo(1);
        assertThat(products.stream().toList()).contains(responseDto);
        assertEquals(products.stream().toList().get(0).reference(), reference);
        assertThat(products.stream().toList().get(0).status()).isEqualTo(ProductStatusEnum.ACTIVE);
        assertThat(products.stream().toList().get(0).categoryName()).isEqualTo(ProductCategoriesEnum.CLOTHE);
        assertThat(products.stream().toList().get(0).city()).isEqualTo("Kiev");
        assertThat(products.stream().toList().get(0).state()).isEqualTo(ProductStateEnum.USED);
        assertThat(products.stream().toList().get(0).images()).contains(imageDto);
    }
}
