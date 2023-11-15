package com.teamchallenge.marketplace.product.controller;

import com.teamchallenge.marketplace.common.exception.dto.ExceptionResponseDto;
import com.teamchallenge.marketplace.common.util.ApiPageable;
import com.teamchallenge.marketplace.common.util.ApiSlice;
import com.teamchallenge.marketplace.product.dto.request.ProductRequestDto;
import com.teamchallenge.marketplace.product.dto.response.ProductResponseDto;
import com.teamchallenge.marketplace.product.persisit.entity.enums.CitiesEnum;
import com.teamchallenge.marketplace.product.persisit.entity.enums.ProductCategoriesEnum;
import com.teamchallenge.marketplace.product.persisit.entity.enums.ProductStateEnum;
import com.teamchallenge.marketplace.product.persisit.entity.enums.SortingFieldEnum;
import com.teamchallenge.marketplace.product.service.ProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Slice;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1")
@Tag(name = "Product")
public class ProductController {

    private final ProductService productService;

    @Operation(summary = "Get one product", description = "Get product by its reference UUID",responses = {
            @ApiResponse(responseCode = "200", description = "Product returned"),
            @ApiResponse(responseCode = "400", description = "Invalid input",
                    content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ExceptionResponseDto.class))}),
            @ApiResponse(responseCode = "404", description = "Product by UUID not found",
                    content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ExceptionResponseDto.class))})
    })
    @GetMapping("/public/products/{productReference}")
    public ResponseEntity<ProductResponseDto> getProduct(
            @Parameter(description = "Product reference", required = true)
            @PathVariable(name = "productReference") UUID productReference
    ) {
        ProductResponseDto productByReference = productService.getProductByReference(productReference);

        return new ResponseEntity<>(productByReference, HttpStatus.OK);
    }

    @Operation(summary = "Create product", description = "Create product entity without images",responses = {
            @ApiResponse(responseCode = "201", description = "Product create"),
            @ApiResponse(responseCode = "400", description = "Invalid input",
                    content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ExceptionResponseDto.class))}),
            @ApiResponse(responseCode = "401", description = "User is not authenticated",
                    content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ExceptionResponseDto.class))})
    })
    @PostMapping("/private/products/create")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ProductResponseDto> createProduct(
            @Valid @RequestBody ProductRequestDto requestDto
    ) {
        ProductResponseDto productResponse = productService.createProduct(requestDto);

        return new ResponseEntity<>(productResponse, HttpStatus.CREATED);
    }

    @Operation(summary = "Get all product", description = "Get list of all products",responses = {
            @ApiResponse(responseCode = "200", description = "Product returned")
    })
    @GetMapping("/public/products")
    public ResponseEntity<List<ProductResponseDto>> getAllProducts() {
        List<ProductResponseDto> allProducts = productService.getAllProducts();

        return new ResponseEntity<>(allProducts, HttpStatus.OK);
    }

    @ApiPageable
    @Operation(summary = "Search product by title and city", description = "Search product pages by title and city ",responses = {
            @ApiResponse(responseCode = "200", description = "Products page returned"),
            @ApiResponse(responseCode = "403", description = "Invalid search parameter input",
                    content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ExceptionResponseDto.class))})
    })
    @GetMapping("/public/products/search")
    public ResponseEntity<Page<ProductResponseDto>> getProductsByProductTitle(
            @Parameter(description = "Product title for searching", required = true)
            @RequestParam(name = "product-title") String productTitle,
            @RequestParam(name = "city", required = false) CitiesEnum city,
            Integer page,
            Integer size
    ) {
        Page<ProductResponseDto> productsByProductTitle = productService.getProductsByProductTitle(productTitle, city, page, size);

        return new ResponseEntity<>(productsByProductTitle, HttpStatus.OK);
    }

    @ApiSlice
    @Operation(summary = "Get newest products", description = "Get slice of newest created products sorted",responses = {
            @ApiResponse(responseCode = "200", description = "Slice of products"),
    })
    @GetMapping("/public/products/newest")
    public ResponseEntity<Slice<ProductResponseDto>> getNewestProducts(
            Integer page,
            Integer size
    ) {
        Slice<ProductResponseDto> newestProducts = productService.getNewestProducts(page, size);

        return new ResponseEntity<>(newestProducts, HttpStatus.OK);
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
    @DeleteMapping("/private/products/{productReference}")
    public ResponseEntity<Void> deleteProduct(
            @Parameter(description = "Product reference", required = true)
            @PathVariable UUID productReference
    ) {
        productService.deleteProduct(productReference);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Upload product images", description = "Upload product images by product UUID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Images upload successfully"),
            @ApiResponse(responseCode = "401", description = "Unauthenticated",
                    content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ExceptionResponseDto.class))}),
            @ApiResponse(responseCode = "403", description = "Access denied",
                    content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ExceptionResponseDto.class))}),
            @ApiResponse(responseCode = "404", description = "Product not found",
                    content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ExceptionResponseDto.class))}),
            @ApiResponse(responseCode = "503", description = "Unable to save images",
                    content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ExceptionResponseDto.class))}),
    })
    @PostMapping(path = "/private/products/{productReference}/images", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    public ResponseEntity<ProductResponseDto> uploadProductImages(
            @Parameter(description = "Product reference", required = true)
            @PathVariable(name = "productReference") UUID productReference,
            @Parameter(description = "List of images", required = true)
            @RequestBody List<MultipartFile> images
    ) {
        ProductResponseDto responseDto = productService.uploadImagesToProduct(productReference, images);

        return new ResponseEntity<>(responseDto, HttpStatus.OK);
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
    @PatchMapping("/private/products/{productReference}")
    public ResponseEntity<ProductResponseDto> patchProduct(
            @Parameter(description = "Product reference", required = true)
            @PathVariable(name = "productReference") UUID productReference,
            @RequestBody ProductRequestDto requestDto
    ) {
        ProductResponseDto productResponse = productService.patchProduct(requestDto, productReference);

        return new ResponseEntity<>(productResponse, HttpStatus.OK);
    }

    @ApiPageable
    @Operation(summary = "Find products by category", description = "Find products by category, city, states by pages")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Product pages"),
            @ApiResponse(responseCode = "401", description = "Unauthenticated",
                    content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ExceptionResponseDto.class))}),
            @ApiResponse(responseCode = "403", description = "Invalid search parameters",
                    content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ExceptionResponseDto.class))}),
    })
    @GetMapping("/public/products/listing")
    public ResponseEntity<Page<ProductResponseDto>> findProductByCategory(
            @RequestParam(name = "category") ProductCategoriesEnum categories,
            @RequestParam(name = "city", required = false) CitiesEnum city,
            @RequestParam(name = "states", required = false) List<ProductStateEnum> states,
            Integer page,
            Integer size,
            @RequestParam(name = "sort", defaultValue = "DATE", required = false) SortingFieldEnum sortField) {

        Page<ProductResponseDto> productsByCategory = productService.getAllProductsByCategory(categories, city, states, page, size, sortField);

        return new ResponseEntity<>(productsByCategory, HttpStatus.OK);
    }
}
