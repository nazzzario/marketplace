package com.teamchallenge.marketplace.product.controller;

import com.teamchallenge.marketplace.common.util.ApiPageable;
import com.teamchallenge.marketplace.common.util.ApiSlice;
import com.teamchallenge.marketplace.product.dto.request.ProductRequestDto;
import com.teamchallenge.marketplace.product.dto.response.ProductResponseDto;
import com.teamchallenge.marketplace.product.persisit.entity.enums.CitiesEnum;
import com.teamchallenge.marketplace.product.persisit.entity.enums.ProductCategoriesEnum;
import com.teamchallenge.marketplace.product.persisit.entity.enums.ProductStateEnum;
import com.teamchallenge.marketplace.product.persisit.entity.enums.SortingFieldEnum;
import com.teamchallenge.marketplace.product.service.ProductService;
import com.teamchallenge.marketplace.user.persisit.entity.UserEntity;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1")
public class ProductController {

    private final ProductService productService;

    @Operation(description = "Get product by its reference")
    @GetMapping("/public/products/{productReference}")
    public ResponseEntity<ProductResponseDto> getProduct(
            @Parameter(description = "Product reference", required = true)
            @PathVariable(name = "productReference") UUID productReference
    ) {
        ProductResponseDto productByReference = productService.getProductByReference(productReference);

        return new ResponseEntity<>(productByReference, HttpStatus.OK);
    }

    @Operation(description = "Create a new product")
    @PostMapping("/private/products/create")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ProductResponseDto> createProduct(
            @Valid @RequestBody ProductRequestDto requestDto
    ) {
        ProductResponseDto productResponse = productService.createProduct(requestDto);

        return new ResponseEntity<>(productResponse, HttpStatus.CREATED);
    }

    @Operation(description = "Get all products")
    @GetMapping("/public/products")
    public ResponseEntity<List<ProductResponseDto>> getAllProducts() {
        List<ProductResponseDto> allProducts = productService.getAllProducts();

        return new ResponseEntity<>(allProducts, HttpStatus.OK);
    }

    @ApiPageable
    @Operation(description = "Search products by product title")
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
    @Operation(description = "Get the newest products with pagination")
    @GetMapping("/public/products/newest")
    public ResponseEntity<Slice<ProductResponseDto>> getNewestProducts(
            Integer page,
            Integer size
    ) {
        Slice<ProductResponseDto> newestProducts = productService.getNewestProducts(page, size);

        return new ResponseEntity<>(newestProducts, HttpStatus.OK);
    }

    @Operation(description = "Delete a product by its reference")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Product deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Product not found")
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

    @Operation(description = "Upload images to a product")
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

    @Operation(description = "Patch product")
    @PatchMapping("/private/products/{productReference}")
    public ResponseEntity<ProductResponseDto> patchProduct(
            @Parameter(description = "Product reference", required = true)
            @PathVariable(name = "productReference") UUID productReference,
            @RequestBody ProductRequestDto requestDto
    ) {
        ProductResponseDto productResponse = productService.patchProduct(requestDto, productReference);

        return new ResponseEntity<>(productResponse, HttpStatus.OK);
    }

    // TODO: 10/30/23 improve enum as request param
    @Operation(description = "Search products by category")
    @ApiPageable
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Get products by category"),
            @ApiResponse(responseCode = "400", description = "Invalid user request request")
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
