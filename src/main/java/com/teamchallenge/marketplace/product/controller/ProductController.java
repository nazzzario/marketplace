package com.teamchallenge.marketplace.product.controller;

import com.teamchallenge.marketplace.common.util.ApiSlice;
import com.teamchallenge.marketplace.product.dto.request.ProductRequestDto;
import com.teamchallenge.marketplace.product.dto.response.ProductResponseDto;
import com.teamchallenge.marketplace.product.service.ProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Slice;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

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
    @PostMapping("/private/products/{userReference}/create")
    public ResponseEntity<ProductResponseDto> createProduct(
            @Parameter(description = "User reference", required = true)
            @PathVariable(name = "userReference") UUID userReference,
            @RequestBody ProductRequestDto requestDto
    ) {
        ProductResponseDto productResponse = productService.createProduct(requestDto, userReference);

        return new ResponseEntity<>(productResponse, HttpStatus.CREATED);
    }

    @Operation(description = "Get all products")
    @GetMapping("/public/products")
    public ResponseEntity<List<ProductResponseDto>> getAllProducts() {
        List<ProductResponseDto> allProducts = productService.getAllProducts();

        return new ResponseEntity<>(allProducts, HttpStatus.OK);
    }

    @Operation(description = "Search products by product title")
    @GetMapping("/public/products/search")
    public ResponseEntity<List<ProductResponseDto>> getProductsByProductTitle(
            @Parameter(description = "Product title for searching", required = true)
            @RequestParam(name = "product-title") String productTitle
    ) {
        List<ProductResponseDto> productsByProductTitle = productService.getProductsByProductTitle(productTitle);

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

    // TODO: 12.10.23 only owner can delete product
    @Operation(description = "Delete a product by its reference")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Product deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Product not found")
    })
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
}
