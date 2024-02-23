package com.teamchallenge.marketplace.admin.controller;

import com.teamchallenge.marketplace.admin.dto.FolderComplaintDto;
import com.teamchallenge.marketplace.admin.service.AdminProductService;
import com.teamchallenge.marketplace.common.exception.dto.ExceptionResponseDto;
import com.teamchallenge.marketplace.product.dto.response.UserProductResponseDto;
import com.teamchallenge.marketplace.product.persisit.entity.enums.ProductStatusEnum;
import com.teamchallenge.marketplace.product.persisit.entity.enums.SortingFieldEnum;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/private/")
@Tag(name = "Admin")
public class AdminProductController {

    private final AdminProductService productService;

    @Operation(summary = "Get page of product with status by user", description = "Get product with status by user for admin" +
            "Default optional parameters: page=0, size=6, sort=id, direction=desc", responses = {
            @ApiResponse(responseCode = "200", description = "Products page returned"),
            @ApiResponse(responseCode = "401", description = "Unauthenticated",
                    content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ExceptionResponseDto.class))}),
            @ApiResponse(responseCode = "403", description = "Access denied",
                    content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ExceptionResponseDto.class))}),
            @ApiResponse(responseCode = "404", description = "Product not found",
                    content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ExceptionResponseDto.class))})

    })
    @PreAuthorize("@userSecurity.checkAdminRights()")
    @GetMapping("{userReference}/products/{status}")
    public ResponseEntity<Page<UserProductResponseDto>> getActiveProductsByUser(
            @PathVariable UUID userReference,
            @PathVariable ProductStatusEnum status,
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "6") Integer size,
            @RequestParam(name = "sort", defaultValue = "DATE", required = false) SortingFieldEnum sort,
            @Parameter(description = "The direction can be asc or desc", name = "direction", schema = @Schema(defaultValue = "desc"))
            @RequestParam(defaultValue = "desc") String direction) {
        Page<UserProductResponseDto> productsByUserAndProductActive = productService
                .getProductsWithStatusByUser(userReference, status,
                        PageRequest.of(page, size, Sort.Direction.fromString(direction), sort.getFiledName()));

        return new ResponseEntity<>(productsByUserAndProductActive, HttpStatus.OK);
    }

    @Operation(summary = "Get page of product with status by user", description = "Get product with status by user for admin" +
            "Default optional parameters: page=0, size=6, sort=id, direction=desc", responses = {
            @ApiResponse(responseCode = "200", description = "Products page returned"),
            @ApiResponse(responseCode = "401", description = "Unauthenticated",
                    content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ExceptionResponseDto.class))}),
            @ApiResponse(responseCode = "403", description = "Access denied",
                    content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ExceptionResponseDto.class))}),
            @ApiResponse(responseCode = "404", description = "Product not found",
                    content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ExceptionResponseDto.class))})

    })
    @PreAuthorize("@userSecurity.checkAdminRights()")
    @GetMapping("{userReference}/favorite/products")
    public ResponseEntity<Page<UserProductResponseDto>> getFavoriteProductsByUser(
            @PathVariable UUID userReference,
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "6") Integer size,
            @RequestParam(name = "sort", defaultValue = "DATE", required = false) SortingFieldEnum sort,
            @Parameter(description = "The direction can be asc or desc", name = "direction", schema = @Schema(defaultValue = "desc"))
            @RequestParam(defaultValue = "desc") String direction) {
        Page<UserProductResponseDto> productsByUserAndProductActive = productService
                .getFavoriteProductsByUser(userReference,
                        PageRequest.of(page, size, Sort.Direction.fromString(direction), sort.getFiledName()));

        return new ResponseEntity<>(productsByUserAndProductActive, HttpStatus.OK);
    }

    @Operation(summary = "Get list of complaints", description = "Get list of complaints", responses = {
            @ApiResponse(responseCode = "200", description = "Get list of complaints"),
            @ApiResponse(responseCode = "401", description = "Unauthenticated",
                    content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ExceptionResponseDto.class))}),
            @ApiResponse(responseCode = "403", description = "Access denied",
                    content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ExceptionResponseDto.class))}),
            @ApiResponse(responseCode = "404", description = "Product not found",
                    content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ExceptionResponseDto.class))})

    })
    @PreAuthorize("@userSecurity.checkAdminRights()")
    @GetMapping("complaint/products")
    public ResponseEntity<List<FolderComplaintDto>> getComplaintProducts() {
        List<FolderComplaintDto> productsByUserAndProductActive = productService.getComplaintProducts();

        return new ResponseEntity<>(productsByUserAndProductActive, HttpStatus.OK);
    }

    @Operation(summary = "Delete complaint", description = "Admin can delete complaint product", responses = {
            @ApiResponse(responseCode = "204", description = "Delete complaint product"),
            @ApiResponse(responseCode = "401", description = "Unauthenticated",
                    content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ExceptionResponseDto.class))}),
            @ApiResponse(responseCode = "403", description = "Access denied",
                    content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ExceptionResponseDto.class))}),
            @ApiResponse(responseCode = "404", description = "Product not found",
                    content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ExceptionResponseDto.class))})

    })
    @PreAuthorize("@userSecurity.checkAdminRights()")
    @DeleteMapping("complaint/product/{productReference}/delete")
    public ResponseEntity<Void> deleteComplaintProduct(
            @PathVariable UUID productReference
    ) {
        productService.deleteComplaint(productReference);

        return ResponseEntity.noContent().build();
    }
}
