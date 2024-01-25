package com.teamchallenge.marketplace.product.controller;

import com.teamchallenge.marketplace.common.exception.dto.ExceptionResponseDto;
import com.teamchallenge.marketplace.product.dto.response.UserProductImageDto;
import com.teamchallenge.marketplace.product.service.ProductImageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/private/product")
@Tag(name = "Image product")
public class ProductImagesController {
    private final ProductImageService imageService;
    @Operation(summary = "Create product images", description = "Create product images by product UUID")
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
    @PostMapping(path = "/{productReference}/image", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    public ResponseEntity<UserProductImageDto> createProductImages(
            @Parameter(description = "Product reference", required = true)
            @PathVariable(name = "productReference") UUID productReference,
            @Parameter(description = "Image", required = true)
            @RequestBody MultipartFile image
    ) {
        UserProductImageDto responseDto = imageService.createImage(productReference, image);

        return new ResponseEntity<>(responseDto, HttpStatus.OK);
    }
    @Operation(summary = "Upload product images", description = "Upload product images by image id")
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

    @PatchMapping(path = "/image/{imageId}", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    public ResponseEntity<UserProductImageDto> uploadProductImages(
            @Parameter(description = "Image id", required = true)
            @PathVariable(name = "imageId") Long imageId,
            @Parameter(description = "Image", required = true)
            MultipartFile image
    ) {
        UserProductImageDto responseDto = imageService.uploadImages(imageId, image);

        return new ResponseEntity<>(responseDto, HttpStatus.OK);
    }

    @Operation(summary = "Delete product image", description = "Delete image by image id")
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

    @DeleteMapping(path = "/image/{imageId}")
    public ResponseEntity<Void> deleteProductImages(
            @Parameter(description = "Image id", required = true)
            @PathVariable(name = "imageId") Long imageId
    ) {
        imageService.deleteImage(imageId);

        return ResponseEntity.noContent().build();
    }
}
