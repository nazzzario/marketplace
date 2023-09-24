package com.teamchallenge.marketplace.product.controller;

import com.teamchallenge.marketplace.product.dto.request.ProductRequestDto;
import com.teamchallenge.marketplace.product.dto.response.ProductResponseDto;
import com.teamchallenge.marketplace.product.service.ProductService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/public/products")
public class ProductController {

    private final ProductService productService;

    @Operation(description = "Get product by it reference")
    @GetMapping("/{reference}")
    public ResponseEntity<ProductResponseDto> getProduct(@PathVariable(name = "reference") UUID reference) {
        ProductResponseDto productByReference = productService.getProductByReference(reference);

        return new ResponseEntity<>(productByReference, HttpStatus.OK);

    }

    @PostMapping("/users/{reference}/create")
    public ResponseEntity<ProductResponseDto> createProduct(@RequestBody ProductRequestDto requestDto,
                                                            @PathVariable(name = "reference") UUID userReference) {
        ProductResponseDto productResponse = productService.createProduct(requestDto, userReference);

        return new ResponseEntity<>(productResponse, HttpStatus.CREATED);
    }

    @GetMapping("/all")
    public ResponseEntity<List<ProductResponseDto>> getAllProducts(){
        List<ProductResponseDto> allProducts = productService.getAllProducts();

        return new ResponseEntity<>(allProducts, HttpStatus.OK);
    }

    @GetMapping("/search")
    public ResponseEntity<List<ProductResponseDto>> getProductsByProductTitle(@RequestParam(name = "product-title") String productTitle){
        List<ProductResponseDto> productsByProductTitle = productService.getProductsByProductTitle(productTitle);

        return new ResponseEntity<>(productsByProductTitle, HttpStatus.OK);
    }
}
