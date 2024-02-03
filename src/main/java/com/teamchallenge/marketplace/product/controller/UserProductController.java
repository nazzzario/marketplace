package com.teamchallenge.marketplace.product.controller;

import com.teamchallenge.marketplace.common.exception.dto.ExceptionResponseDto;
import com.teamchallenge.marketplace.product.dto.request.ProductRequestDto;
import com.teamchallenge.marketplace.product.dto.response.UserProductResponseDto;
import com.teamchallenge.marketplace.product.persisit.entity.enums.ProductStatusEnum;
import com.teamchallenge.marketplace.product.service.UserProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/private/product")
@Tag(name = "Product")
public class UserProductController {
    private final UserProductService productService;

    @Operation(summary = "Get NEW or create product", description = "Get product with status NEW or create product entity without images and " +
            "with value from first active product or default.", responses = {
            @ApiResponse(responseCode = "201", description = "Product create"),
            @ApiResponse(responseCode = "400", description = "Invalid input",
                    content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ExceptionResponseDto.class))}),
            @ApiResponse(responseCode = "401", description = "User is not authenticated",
                    content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ExceptionResponseDto.class))})
    })
    @PostMapping("/create")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<UserProductResponseDto> createProduct() {
        UserProductResponseDto productResponse = productService.createOrGetNewProduct();

        return new ResponseEntity<>(productResponse, HttpStatus.CREATED);
    }

    @Operation(summary = "Patch product", description = "Product owner can patch product by its reference")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Product patched successfully"),
            @ApiResponse(responseCode = "401", description = "Unauthenticated",
                    content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ExceptionResponseDto.class))}),
            @ApiResponse(responseCode = "403", description = "Access denied",
                    content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ExceptionResponseDto.class))}),
            @ApiResponse(responseCode = "404", description = "Product not found",
                    content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ExceptionResponseDto.class))})
    })
    @PatchMapping("/{productReference}")
    public ResponseEntity<UserProductResponseDto> patchProduct(
            @Parameter(description = "Product reference", required = true)
            @PathVariable(name = "productReference") UUID productReference,
            @RequestBody ProductRequestDto requestDto
    ) {
        UserProductResponseDto productResponse = productService.patchProduct(requestDto, productReference);

        return new ResponseEntity<>(productResponse, HttpStatus.CREATED);
    }

    @Operation(summary = "Delete product", description = "Product owner can delete product by its reference")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Product deleted successfully"),
            @ApiResponse(responseCode = "401", description = "Unauthenticated",
                    content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ExceptionResponseDto.class))}),
            @ApiResponse(responseCode = "403", description = "Access denied",
                    content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ExceptionResponseDto.class))}),
            @ApiResponse(responseCode = "404", description = "Product not found",
                    content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ExceptionResponseDto.class))})
    })
    @PreAuthorize("@productSecurity.checkOwnership(#productReference, principal.username)")
    @DeleteMapping("/{productReference}")
    public ResponseEntity<Void> deleteProduct(
            @Parameter(description = "Product reference", required = true)
            @PathVariable UUID productReference
    ) {
        productService.deleteProduct(productReference);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Patch status product", description = "Product owner can patch status product by its reference")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Product patched successfully"),
            @ApiResponse(responseCode = "401", description = "Unauthenticated",
                    content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ExceptionResponseDto.class))}),
            @ApiResponse(responseCode = "403", description = "Access denied",
                    content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ExceptionResponseDto.class))}),
            @ApiResponse(responseCode = "404", description = "Product not found",
                    content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ExceptionResponseDto.class))})
    })
    @PatchMapping("/{productReference}/{status}")
    public ResponseEntity<UserProductResponseDto> changeStatusProduct(
            @Parameter(description = "Product reference", required = true)
            @PathVariable(name = "productReference") UUID productReference,
            @Parameter(description = "Product status. Except status NEW, it use only create product", required = true)
            @PathVariable(name = "status") ProductStatusEnum status,
            @Parameter(description = "Product period. It can be ${product.active.periodsDeadline} days with status ACTIVE and " +
                    "${product.delete.periodDeadline} days with status DISABLED")
            @RequestParam(name = "period", defaultValue = "${product.active.periodsDeadline}") int period
    ) {
        UserProductResponseDto productResponse = productService.changeStatusProduct(productReference, status, period);

        return new ResponseEntity<>(productResponse, HttpStatus.CREATED);
    }

    @Operation(summary = "Raise ad of product", description = "Product owner can raise ad of product by its reference")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Product patched successfully"),
            @ApiResponse(responseCode = "401", description = "Unauthenticated",
                    content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ExceptionResponseDto.class))}),
            @ApiResponse(responseCode = "403", description = "Access denied",
                    content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ExceptionResponseDto.class))}),
            @ApiResponse(responseCode = "404", description = "Product not found",
                    content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ExceptionResponseDto.class))})
    })
    @PatchMapping("/{productReference}/active/raise")
    public ResponseEntity<Void> raiseAddProduct(
            @Parameter(description = "Product reference", required = true)
            @PathVariable(name = "productReference") UUID productReference
    ) {
        productService.raiseAdProduct(productReference);

        return ResponseEntity.noContent().build();
    }
}
