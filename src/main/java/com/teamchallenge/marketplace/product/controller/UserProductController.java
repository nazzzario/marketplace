package com.teamchallenge.marketplace.product.controller;

import com.teamchallenge.marketplace.common.exception.dto.ExceptionResponseDto;
import com.teamchallenge.marketplace.common.util.ApiPageable;
import com.teamchallenge.marketplace.product.dto.response.UserProductResponseDto;
import com.teamchallenge.marketplace.product.persisit.entity.enums.ProductStatusEnum;
import com.teamchallenge.marketplace.product.service.UserProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.converters.models.PageableAsQueryParam;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/private/products")
@Tag(name = "Product")
public class UserProductController {
    private final UserProductService productService;

    @ApiPageable
    @Operation(summary = "Get page of product with active status by user", description = "Get product with active status by user " +
            "Default optional parameters: page=0, size=6, sort=id, direction=desc", responses = {
            @ApiResponse(responseCode = "200", description = "Products page returned"),
            @ApiResponse(responseCode = "401", description = "Unauthenticated",
                    content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ExceptionResponseDto.class))}),
            @ApiResponse(responseCode = "403", description = "Access denied",
                    content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ExceptionResponseDto.class))}),
            @ApiResponse(responseCode = "404", description = "Product not found",
                    content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ExceptionResponseDto.class))})

    })
    @GetMapping("/active")
    public ResponseEntity<Page<UserProductResponseDto>> getActiveProductsByUser(
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "6") Integer size,
            @RequestParam(defaultValue = "id") String sort,
            @RequestParam(defaultValue = "desc") String direction) {
        Page<UserProductResponseDto> productsByUserAndProductActive = productService
                .getProductsWithStatusByUser(ProductStatusEnum.ACTIVE,
                        PageRequest.of(page, size, Sort.Direction.fromString(direction), sort));

        return new ResponseEntity<>(productsByUserAndProductActive, HttpStatus.OK);
    }

    @ApiPageable
    @Operation(summary = "Get page of favorite product by user", description = "Get page of favorite product by user " +
            "Default optional parameters: page=0, size=6, sort=id, direction=desc", responses = {
            @ApiResponse(responseCode = "200", description = "Products page returned"),
            @ApiResponse(responseCode = "401", description = "Unauthenticated",
                    content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ExceptionResponseDto.class))}),
            @ApiResponse(responseCode = "403", description = "Access denied",
                    content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ExceptionResponseDto.class))}),
            @ApiResponse(responseCode = "404", description = "Product not found",
                    content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ExceptionResponseDto.class))})

    })
    @PageableAsQueryParam
    @GetMapping("/favorite")
    public ResponseEntity<Page<UserProductResponseDto>> getFavoriteProductsByUser(
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "6") Integer size,
            @RequestParam(defaultValue = "id") String sort,
            @RequestParam(defaultValue = "desc") String direction) {
        Page<UserProductResponseDto> productsByUserAndProductActive = productService
                .getFavoriteProductsByUser( PageRequest.of(page, size, Sort.Direction.fromString(direction), sort));

        return new ResponseEntity<>(productsByUserAndProductActive, HttpStatus.OK);
    }

    @ApiPageable
    @Operation(summary = "Get page of product with disabled status by user", description = "Get page of product with disable status by user" +
            "Default optional parameters: page=0, size=6, sort=id, direction=desc", responses = {
            @ApiResponse(responseCode = "200", description = "Products page returned"),
            @ApiResponse(responseCode = "401", description = "Unauthenticated",
                    content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ExceptionResponseDto.class))}),
            @ApiResponse(responseCode = "403", description = "Access denied",
                    content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ExceptionResponseDto.class))}),
            @ApiResponse(responseCode = "404", description = "Product not found",
                    content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ExceptionResponseDto.class))})
    })
    @PageableAsQueryParam
    @GetMapping("/disabled")
    public ResponseEntity<Page<UserProductResponseDto>> getProductsByUserAndProductDisabled(
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "6") Integer size,
            @RequestParam(defaultValue = "id") String sort,
            @RequestParam(defaultValue = "desc") String direction) {
        Page<UserProductResponseDto> productsByUserAndProductActive = productService
                .getProductsWithStatusByUser(ProductStatusEnum.DISABLED,
                        PageRequest.of(page, size, Sort.Direction.fromString(direction), sort));

        return new ResponseEntity<>(productsByUserAndProductActive, HttpStatus.OK);
    }
}