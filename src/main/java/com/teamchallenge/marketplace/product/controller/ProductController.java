package com.teamchallenge.marketplace.product.controller;

import com.teamchallenge.marketplace.common.exception.dto.ExceptionResponseDto;
import com.teamchallenge.marketplace.common.util.ApiPageable;
import com.teamchallenge.marketplace.common.util.ApiSlice;
import com.teamchallenge.marketplace.product.dto.response.ProductNewestResponseDto;
import com.teamchallenge.marketplace.product.dto.response.ProductResponseDto;
import com.teamchallenge.marketplace.product.persisit.entity.enums.*;
import com.teamchallenge.marketplace.product.service.ProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/public")
@Tag(name = "Public information of product")
public class ProductController {

    private final ProductService productService;

    @Operation(summary = "Get one product", description = "Get product by its reference UUID",responses = {
            @ApiResponse(responseCode = "200", description = "Product returned"),
            @ApiResponse(responseCode = "400", description = "Invalid input",
                    content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ExceptionResponseDto.class))}),
            @ApiResponse(responseCode = "404", description = "Product by UUID not found",
                    content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ExceptionResponseDto.class))})
    })
    @GetMapping("/products/{productReference}")
    public ResponseEntity<ProductResponseDto> getProduct(
            @Parameter(description = "Product reference", required = true)
            @PathVariable(name = "productReference") UUID productReference
    ) {
        ProductResponseDto productByReference = productService.getProductByReference(productReference);

        return new ResponseEntity<>(productByReference, HttpStatus.OK);
    }

    @Operation(summary = "Get all product", description = "Get list of all products",responses = {
            @ApiResponse(responseCode = "200", description = "Product returned")
    })
    @GetMapping
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
    @GetMapping("/products/search")
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
    @GetMapping("/products/newest")
    public ResponseEntity<Slice<ProductNewestResponseDto>> getNewestProducts(
            Integer page,
            Integer size
    ) {
        Slice<ProductNewestResponseDto> newestProducts = productService.getNewestProducts(page, size);

        return new ResponseEntity<>(newestProducts, HttpStatus.OK);
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
    @GetMapping("/products/listing")
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

    @Operation(summary = "Get one product with status active", description = "Get products with status active " +
            "by user reference UUID. Default optional parameters: page=0, size=6, sort=id, direction=desc",responses = {
            @ApiResponse(responseCode = "200", description = "Product returned"),
            @ApiResponse(responseCode = "400", description = "Invalid input",
                    content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ExceptionResponseDto.class))}),
            @ApiResponse(responseCode = "404", description = "Product by UUID not found",
                    content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ExceptionResponseDto.class))})
    })
    @GetMapping("/{referenceUser}/products/active")
    public ResponseEntity<Page<ProductResponseDto>> getProduct(
            @Parameter(description = "User reference", required = true)
            @PathVariable(name = "referenceUser") UUID referenceUser,
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "6") Integer size,
            @Parameter(description = "The field by which sort", name = "sort", schema = @Schema(defaultValue = "id"))
            @RequestParam(defaultValue = "id") String sort,
            @Parameter(description = "The direction can be asc or desc", name = "direction", schema = @Schema(defaultValue = "desc"))
            @RequestParam(defaultValue = "desc") String direction
    ) {
        Page<ProductResponseDto> productByReference = productService
                .getProductByReferenceUser(ProductStatusEnum.ACTIVE, referenceUser,
                        PageRequest.of(page, size, Sort.Direction.fromString(direction), sort));

        return new ResponseEntity<>(productByReference, HttpStatus.OK);
    }
}
