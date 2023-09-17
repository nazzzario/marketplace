package com.teamchallenge.marketplace.product.controller;

import com.teamchallenge.marketplace.product.dto.request.ProductRequestDto;
import com.teamchallenge.marketplace.product.dto.response.ProductResponseDto;
import com.teamchallenge.marketplace.product.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/public/products")
public class ProductController {

    private final ProductService productService;

    @GetMapping("/{reference}")
    public ResponseEntity<ProductResponseDto> getProduct(@PathVariable(name = "reference") UUID reference) {
        ProductResponseDto productByReference = productService.getProductByReference(reference);

        return new ResponseEntity<>(productByReference, HttpStatus.OK);

    }

    @PostMapping("/create")
    public ResponseEntity<ProductResponseDto> createProduct(@RequestBody ProductRequestDto requestDto) {
        ProductResponseDto productResponse = productService.createProduct(requestDto);

        return new ResponseEntity<>(productResponse, HttpStatus.CREATED);
    }
}
