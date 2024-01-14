package com.teamchallenge.marketplace.product.controller;

import com.teamchallenge.marketplace.product.persisit.entity.enums.ProductStatusEnum;
import com.teamchallenge.marketplace.product.service.ProductService;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Pageable;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.MOCK;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = MOCK, classes = ProductController.class)
@AutoConfigureMockMvc
@ActiveProfiles("test")
class ProductControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ProductService productService;

    @SneakyThrows
    @WithMockUser
    @Test
    void getProduct() {
        mockMvc.perform(get("/api/v1/public/550e8400-e29b-41d4-a716-446655440000/products/active")
                .param("page", "0")
                .param("size", "6")
                .param("sort", "id")
                .param("direction", "desc"))
                .andDo(print()).andExpect(status().isOk());

        verify(productService).getProductByReferenceUser(eq(ProductStatusEnum.ACTIVE),
                eq(UUID.fromString("550e8400-e29b-41d4-a716-446655440000")), any(Pageable.class));
    }
}