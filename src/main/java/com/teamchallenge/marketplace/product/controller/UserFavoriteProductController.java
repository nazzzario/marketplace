package com.teamchallenge.marketplace.product.controller;

import com.teamchallenge.marketplace.common.exception.dto.ExceptionResponseDto;
import com.teamchallenge.marketplace.product.service.UserFavoriteProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/private/products")
@Tag(name = "favorites")
public class UserFavoriteProductController {
    private final UserFavoriteProductService productService;
    @Operation(summary = "Set product to user favorites", description = "An authenticated user can add product to his favorite product list")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Product add to favorites list successfully"),
            @ApiResponse(responseCode = "401", description = "Unauthenticated",
                    content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ExceptionResponseDto.class))}),
            @ApiResponse(responseCode = "403", description = "Invalid product reference",
                    content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ExceptionResponseDto.class))}),
            @ApiResponse(responseCode = "404", description = "Product not found",
                    content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ExceptionResponseDto.class))}),
            @ApiResponse(responseCode = "400", description = "Cannot add product to favorites",
                    content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ExceptionResponseDto.class))})

    })
    @PostMapping("/{productReference}/favorites")
    public ResponseEntity<Void> addProductToFavorites(@PathVariable("productReference") UUID productReference){
        productService.addProductToFavorites(productReference);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @Operation(summary = "Remove product from user favorites", description = "An authenticated user can remove product from his favorite product list")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Product removed from favorites list successfully"),
            @ApiResponse(responseCode = "401", description = "Unauthenticated",
                    content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ExceptionResponseDto.class))}),
            @ApiResponse(responseCode = "403", description = "Invalid product reference",
                    content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ExceptionResponseDto.class))}),
            @ApiResponse(responseCode = "404", description = "Product not found",
                    content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ExceptionResponseDto.class))}),
            @ApiResponse(responseCode = "400", description = "Cannot add product to favorites",
                    content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ExceptionResponseDto.class))})
    })
    @DeleteMapping("/{productReference}/favorites")
    public ResponseEntity<Void> removeProductFromFavorites(@PathVariable("productReference") UUID productReference){
        productService.removeProductFromFavorites(productReference);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
