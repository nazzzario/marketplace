package com.teamchallenge.marketplace.product.controller;

import com.teamchallenge.marketplace.common.exception.dto.ExceptionResponseDto;
import com.teamchallenge.marketplace.common.util.ApiPageable;
import com.teamchallenge.marketplace.product.dto.response.UserProductResponseDto;
import com.teamchallenge.marketplace.product.persisit.entity.enums.ProductStatusEnum;
import com.teamchallenge.marketplace.product.service.UserProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.converters.models.PageableAsQueryParam;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/private")
@Tag(name = "Product")
public class UserProductController {
    private final UserProductService productService;

    @ApiPageable
    @Operation(summary = "Get page of product with active status by user", description = "Get product with active status by user",responses = {
            @ApiResponse(responseCode = "200", description = "Products page returned"),
            @ApiResponse(responseCode = "403", description = "Invalid search parameter input",
                    content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ExceptionResponseDto.class))})

    })
    @PageableAsQueryParam
    @GetMapping("/products/active")
    public ResponseEntity<Page<UserProductResponseDto>> getActiveProductsByUser(
            @Parameter(hidden = true) @PageableDefault(sort = { "id" }, size = 6,
                    direction = Sort.Direction.DESC) Pageable pageable) {
        Page<UserProductResponseDto> productsByUserAndProductActive = productService
                .getProductsWithStatusByUser(ProductStatusEnum.ACTIVE, pageable);

        return new ResponseEntity<>(productsByUserAndProductActive, HttpStatus.OK);
    }

    @ApiPageable
    @Operation(summary = "Get page of favorite product by user", description = "Get page of favorite product by user ",responses = {
            @ApiResponse(responseCode = "200", description = "Products page returned"),
            @ApiResponse(responseCode = "403", description = "Invalid search parameter input",
                    content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ExceptionResponseDto.class))})

    })
    @PageableAsQueryParam
    @GetMapping("/products/favorite")
    public ResponseEntity<Page<UserProductResponseDto>> getFavoriteProductsByUser(
            @Parameter(hidden = true) @PageableDefault(sort = { "id" }, size = 6,
                    direction = Sort.Direction.DESC) Pageable pageable,
            Authentication authentication
    ) {
        Page<UserProductResponseDto> productsByUserAndProductActive = productService
                .getFavoriteProductsByUser(pageable);

        return new ResponseEntity<>(productsByUserAndProductActive, HttpStatus.OK);
    }

    @ApiPageable
    @Operation(summary = "Get page of product with disabled status by user", description = "Get page of product with disable status by user",responses = {
            @ApiResponse(responseCode = "200", description = "Products page returned"),
            @ApiResponse(responseCode = "403", description = "Invalid search parameter input",
                    content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ExceptionResponseDto.class))})

    })
    @PageableAsQueryParam
    @GetMapping("/products/disabled")
    public ResponseEntity<Page<UserProductResponseDto>> getProductsByUserAndProductDisabled(
            @Parameter(hidden = true) @PageableDefault(sort = { "id" }, size = 6,
                    direction = Sort.Direction.DESC) Pageable pageable

    ) {
        Page<UserProductResponseDto> productsByUserAndProductActive = productService
                .getProductsWithStatusByUser(ProductStatusEnum.DISABLED,pageable);

        return new ResponseEntity<>(productsByUserAndProductActive, HttpStatus.OK);
    }
}
