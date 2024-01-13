package com.teamchallenge.marketplace.product.controller;

import com.teamchallenge.marketplace.product.dto.response.UserProductResponseDto;
import com.teamchallenge.marketplace.product.persisit.entity.enums.ProductStatusEnum;
import com.teamchallenge.marketplace.product.service.ShowUserProductService;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.MOCK;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = MOCK, classes = ShowUserProductController.class)
@AutoConfigureMockMvc
@ActiveProfiles("test")
class UserProductControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    ShowUserProductService productService;



    @SneakyThrows
    @WithMockUser
    @Test
    void getActiveProductsByUser() {
        mockMvc.perform(get("/api/v1/private/products/active")
                        .param("page", "0")
                        .param("size", "6")
                        .param("sort", "id")
                        .param("direction", "desc"))
                .andDo(print()).andExpect(status().isOk());

        verify(productService).getProductsWithStatusByUser(eq(ProductStatusEnum.ACTIVE),
                any(Pageable.class));
    }

    @SneakyThrows
    @WithMockUser
    @Test
    void getFavoriteProductsByUser() {
        mockMvc.perform(get("/api/v1/private/products/favorite")
                .param("page", "0")
                .param("size", "6")
                .param("sort", "id")
                .param("direction", "desc"))
                .andDo(print()).andExpect(status().isOk());

        verify(productService).getFavoriteProductsByUser(any(Pageable.class));
    }

    @SneakyThrows
    @WithMockUser
    @Test
    void getProductsByUserAndProductDisabled() {
        mockMvc.perform(get("/api/v1/private/products/disabled")
                        .param("page", "0")
                        .param("size", "6")
                        .param("sort", "id")
                        .param("direction", "desc"))
                .andDo(print()).andExpect(status().isOk());

        verify(productService).getProductsWithStatusByUser(eq(ProductStatusEnum.DISABLED),
                any(Pageable.class));
    }

    @SneakyThrows
    @Test
    void getActiveProductsByUserNotAuthorizeUser() {
        when(productService.getProductsWithStatusByUser(ProductStatusEnum.ACTIVE,
                PageRequest.of(0, 6, Sort.by("id"))))
                .thenReturn(new PageImpl<UserProductResponseDto>(List.of()));

        mockMvc.perform(get("/api/v1/private/products/active"))
                .andDo(print()).andExpect(status().isUnauthorized());
    }

    @SneakyThrows
    @Test
    void getFavoriteProductsByUserNotAuthorizeUser() {
        when(productService.getFavoriteProductsByUser(PageRequest
                .of(0, 6, Sort.by("id"))))
                .thenReturn(new PageImpl<UserProductResponseDto>(List.of()));

        mockMvc.perform(get("/api/v1/private/products/favorite"))
                .andDo(print()).andExpect(status().isUnauthorized());
    }
    @SneakyThrows
    @Test
    void getProductsByUserAndProductDisabledNotAuthorizeUser() {
        when(productService.getProductsWithStatusByUser(ProductStatusEnum.ACTIVE,
                PageRequest.of(0, 6, Sort.by("id"))))
                .thenReturn(new PageImpl<UserProductResponseDto>(List.of()));

        mockMvc.perform(get("/api/v1/private/products/disabled"))
                .andDo(print()).andExpect(status().isUnauthorized());
    }
}
