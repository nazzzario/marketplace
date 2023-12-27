package com.teamchallenge.marketplace.product.service.impl;

import com.teamchallenge.marketplace.product.dto.response.UserProductImageDto;
import com.teamchallenge.marketplace.product.dto.response.UserProductResponseDto;
import com.teamchallenge.marketplace.product.mapper.UserProductMapper;
import com.teamchallenge.marketplace.product.persisit.entity.ProductEntity;
import com.teamchallenge.marketplace.product.persisit.entity.enums.ProductCategoriesEnum;
import com.teamchallenge.marketplace.product.persisit.entity.enums.ProductStateEnum;
import com.teamchallenge.marketplace.product.persisit.entity.enums.ProductStatusEnum;
import com.teamchallenge.marketplace.product.persisit.repository.ProductRepository;
import com.teamchallenge.marketplace.product.service.UserProductService;
import com.teamchallenge.marketplace.user.persisit.entity.UserEntity;
import com.teamchallenge.marketplace.user.persisit.repository.UserRepository;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@WithMockUser
@ActiveProfiles("test")
class UserProductServiceImplTest {
    @Autowired
    private UserProductService productService;

    @MockBean
    private ProductRepository productRepository;
    @MockBean
    private UserRepository userRepository;
    @MockBean
    private UserProductMapper productMapper;

    UserEntity userEntity = new UserEntity();
    PageRequest pageable = PageRequest.of(0, 6, Sort.by("id"));
    PageImpl<ProductEntity> pageEntity = new PageImpl<>(List.of(new ProductEntity()), pageable, 1);
    UUID reference = UUID.randomUUID();
    UserProductImageDto imageDto = new UserProductImageDto("url", "reference");
    UserProductResponseDto responseDto = new UserProductResponseDto(reference,
            ProductCategoriesEnum.CLOTHE, "Kiev","Clothe", "Clothe",
            ProductStateEnum.USED, ProductStatusEnum.ACTIVE, 1, List.of(imageDto));
    PageImpl<UserProductResponseDto> pageDto = new PageImpl<>(List.of(responseDto), pageable, 1);

    @SneakyThrows
    @Test
    void getProductsWithStatusByUser() {


        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(userEntity));
        when(productRepository.findByOwnerAndStatus(userEntity, ProductStatusEnum.ACTIVE, pageable))
                .thenReturn(pageEntity);
        when(productMapper.toResponseDto(any())).thenReturn(responseDto);

        Page<UserProductResponseDto> products = productService.getProductsWithStatusByUser(
                ProductStatusEnum.ACTIVE, pageable);

        assertThat(products.stream().count()).isEqualTo(1);
        assertThat(products.stream().toList()).contains(responseDto);
        assertEquals(products.stream().toList().get(0).reference(), reference);
        assertThat(products.stream().toList().get(0).images()).contains(imageDto);
    }

    @Test
    void getFavoriteProductsByUser() {
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(userEntity));
        when(productRepository.findByFavoritismId(anyLong(), pageable))
                .thenReturn(pageEntity);
        when(productMapper.toResponseDto(any())).thenReturn(responseDto);

        Page<UserProductResponseDto> products = productService.getFavoriteProductsByUser(pageable);

        assertThat(products.stream().count()).isEqualTo(1);
        assertThat(products.stream().toList()).contains(responseDto);
        assertEquals(products.stream().toList().get(0).reference(), reference);
        assertThat(products.stream().toList().get(0).images()).contains(imageDto);
    }
}
