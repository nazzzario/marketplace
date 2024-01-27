package com.teamchallenge.marketplace.product.controller;

import com.teamchallenge.marketplace.common.exception.ClientBackendException;
import com.teamchallenge.marketplace.common.exception.ErrorCode;
import com.teamchallenge.marketplace.common.exception.dto.ExceptionResponseDto;
import com.teamchallenge.marketplace.product.service.AutomaticChangeProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/public/product/automatic")
@Tag(name = "Automatic processing")
public class AutomaticProcessingController {

    public static final int CODE = 1234;
    private final AutomaticChangeProductService productService;

    @Operation(summary = "Automatic change status product", description = "Select all expired products with users." +
            " For each user, we check whether there is space in the archive, if there is no space," +
            " we delete older products.",responses = {
            @ApiResponse(responseCode = "204", description = "Product change status"),
            @ApiResponse(responseCode = "400", description = "Invalid input",
                    content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ExceptionResponseDto.class))}),
            @ApiResponse(responseCode = "404", description = "Product by UUID not found",
                    content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ExceptionResponseDto.class))})
    })
    @PostMapping("/change/{code}")
    public ResponseEntity<Void> changeStatus(
            @Parameter(description = "Code start", required = true)
            @PathVariable(name = "code") Integer code
    ) {
        if (code != CODE){throw new ClientBackendException(ErrorCode.UNKNOWN_SERVER_ERROR);
        }
        productService.changeStatusFromActiveToDisabled();

        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Automatic delete product with status Disabled", description = "Automatic delete older product with" +
            " deadline date in the archive", responses = {
            @ApiResponse(responseCode = "204", description = "Product change status"),
            @ApiResponse(responseCode = "400", description = "Invalid input",
                    content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ExceptionResponseDto.class))}),
            @ApiResponse(responseCode = "404", description = "Product by UUID not found",
                    content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ExceptionResponseDto.class))})
    })
    @DeleteMapping("/delete/{code}")
    public ResponseEntity<Void> deleteDisableProduct(
            @Parameter(description = "Product reference", required = true)
            @PathVariable(name = "code") Integer code
    ) {
        if (code != CODE){throw new ClientBackendException(ErrorCode.UNKNOWN_SERVER_ERROR);
        }
        productService.deleteDisabledOldProduct();

        return ResponseEntity.noContent().build();
    }

}
