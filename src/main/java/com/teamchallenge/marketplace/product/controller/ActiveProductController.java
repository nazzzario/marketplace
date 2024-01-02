package com.teamchallenge.marketplace.product.controller;

import com.teamchallenge.marketplace.common.exception.dto.ExceptionResponseDto;
import com.teamchallenge.marketplace.product.dto.response.ProductResponseDto;
import com.teamchallenge.marketplace.product.persisit.entity.enums.ProductStatusEnum;
import com.teamchallenge.marketplace.product.service.ActiveProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/public/products/active")
@Tag(name = "Product")
public class ActiveProductController {

    private final ActiveProductService activeProductService;
    @Operation(summary = "Get one product", description = "Get products by its reference UUID",responses = {
            @ApiResponse(responseCode = "200", description = "Product returned"),
            @ApiResponse(responseCode = "400", description = "Invalid input",
                    content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ExceptionResponseDto.class))}),
            @ApiResponse(responseCode = "404", description = "Product by UUID not found",
                    content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ExceptionResponseDto.class))})
    })
    @GetMapping("/{referenceUser}")
    public ResponseEntity<Page<ProductResponseDto>> getProduct(
            @Parameter(description = "User reference", required = true)
            @PathVariable(name = "referenceUser") UUID referenceUser
    ) {
        Page<ProductResponseDto> productByReference = activeProductService
                .getProductByReferenceUser(ProductStatusEnum.ACTIVE, referenceUser);

        return new ResponseEntity<>(productByReference, HttpStatus.OK);
    }
}
