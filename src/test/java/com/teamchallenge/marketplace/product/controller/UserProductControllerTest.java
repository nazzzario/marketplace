package com.teamchallenge.marketplace.product.controller;

import com.teamchallenge.marketplace.product.dto.response.UserProductResponseDto;
import com.teamchallenge.marketplace.product.persisit.entity.enums.ProductStatusEnum;
import com.teamchallenge.marketplace.product.service.UserProductService;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.MOCK;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = MOCK)
@AutoConfigureMockMvc
class UserProductControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    UserProductService productService;

    @SneakyThrows
    @Test
    void getActiveProductsByUser() {
        when(productService.getProductsWithStatusByUser(ProductStatusEnum.ACTIVE,
                PageRequest.of(0, 6, Sort.by("id").descending())))
                .thenReturn(new PageImpl<UserProductResponseDto>(List.of()));

        mockMvc.perform(get("/api/v1/private/products/active").
                requestAttr("pageable", PageRequest
                        .of(0, 6, Sort.by("id")))
                .with(jwt().authorities(new SimpleGrantedAuthority("ROLE_AUTHORIZED_PERSONNEL"))))
                .andDo(print()).andExpect(status().isOk());
    }

    @SneakyThrows
    @Test
    void getFavoriteProductsByUser() {
        when(productService.getFavoriteProductsByUser(PageRequest
                .of(0, 6, Sort.by("id").descending())))
                .thenReturn(new PageImpl<UserProductResponseDto>(List.of()));

        mockMvc.perform(get("/api/v1/private/products/favorite").
                        requestAttr("pageable", PageRequest
                                .of(0, 6, Sort.by("id")))
                        .with(jwt().authorities(new SimpleGrantedAuthority("ROLE_AUTHORIZED_PERSONNEL"))))
                .andDo(print()).andExpect(status().isOk());
    }

    @SneakyThrows
    @Test
    void getProductsByUserAndProductDisabled() {
        when(productService.getProductsWithStatusByUser(ProductStatusEnum.ACTIVE,
                PageRequest.of(0, 6, Sort.by("id").descending())))
                .thenReturn(new PageImpl<UserProductResponseDto>(List.of()));

        mockMvc.perform(get("/api/v1/private/products/disabled").
                        requestAttr("pageable", PageRequest
                                .of(0, 6, Sort.by("id")))
                        .with(jwt().authorities(new SimpleGrantedAuthority("ROLE_AUTHORIZED_PERSONNEL"))))
                .andDo(print()).andExpect(status().isOk());
    }
}