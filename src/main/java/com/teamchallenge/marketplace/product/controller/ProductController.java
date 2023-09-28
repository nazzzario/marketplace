package com.teamchallenge.marketplace.product.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.teamchallenge.marketplace.common.file.FileUpload;
import com.teamchallenge.marketplace.common.util.ApiSlice;
import com.teamchallenge.marketplace.product.dto.request.ProductRequestDto;
import com.teamchallenge.marketplace.product.dto.response.ProductResponseDto;
import com.teamchallenge.marketplace.product.service.ProductService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.data.domain.Slice;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/public/products")
public class ProductController {

    private final ProductService productService;
    private final FileUpload fileUpload;

    @Operation(description = "Get product by it reference")
    @GetMapping("/{reference}")
    public ResponseEntity<ProductResponseDto> getProduct(@PathVariable(name = "reference") UUID reference) {
        ProductResponseDto productByReference = productService.getProductByReference(reference);

        return new ResponseEntity<>(productByReference, HttpStatus.OK);
    }

    @PostMapping(
            value = "/{userReference}/create",
            produces = "application/json",
            consumes = "multipart/form-data"
    )
    public ResponseEntity<ProductResponseDto> createProduct(@RequestPart String requestDto,
                                                            @RequestPart List<MultipartFile> images,
                                                            @PathVariable(name = "userReference") UUID userReference) {
        ProductRequestDto productRequestDto;
        try {
            productRequestDto = new ObjectMapper().readValue(requestDto, ProductRequestDto.class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        if(Objects.isNull(productRequestDto)){
            return new ResponseEntity<>(null, HttpStatus.NO_CONTENT);
        }
        ProductResponseDto productResponse = productService.createProduct(productRequestDto, images, userReference);

        return new ResponseEntity<>(productResponse, HttpStatus.CREATED);
    }

    @GetMapping("/all")
    public ResponseEntity<List<ProductResponseDto>> getAllProducts() {
        List<ProductResponseDto> allProducts = productService.getAllProducts();

        return new ResponseEntity<>(allProducts, HttpStatus.OK);
    }

    @GetMapping("/search")
    public ResponseEntity<List<ProductResponseDto>> getProductsByProductTitle(@RequestParam(name = "product-title") String productTitle) {
        List<ProductResponseDto> productsByProductTitle = productService.getProductsByProductTitle(productTitle);

        return new ResponseEntity<>(productsByProductTitle, HttpStatus.OK);
    }

    @ApiSlice
    @GetMapping("/newest")
    public ResponseEntity<Slice<ProductResponseDto>> getNewestProducts(Integer page, Integer size) {
        var newestProducts = productService.getNewestProducts(page, size);

        return new ResponseEntity<>(newestProducts, HttpStatus.OK);
    }

    @DeleteMapping("/{reference}")
    public ResponseEntity<Void> deleteProduct(@PathVariable UUID reference) {
        productService.deleteProduct(reference);

        return ResponseEntity.noContent().build();
    }

    @SneakyThrows
    @PostMapping(
            value = "/image",
            consumes = "*/*"
    )
    public ResponseEntity<String> uploadPhoto(@RequestBody List<MultipartFile> file) {
        String url = fileUpload.uploadFile(file.get(0), null);

        return ResponseEntity.ok(url);
    }
}
