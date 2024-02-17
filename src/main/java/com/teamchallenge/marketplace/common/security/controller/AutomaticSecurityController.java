package com.teamchallenge.marketplace.common.security.controller;

import com.teamchallenge.marketplace.common.exception.ClientBackendException;
import com.teamchallenge.marketplace.common.exception.ErrorCode;
import com.teamchallenge.marketplace.common.exception.dto.ExceptionResponseDto;
import com.teamchallenge.marketplace.common.security.service.AutomaticSecurityServiceImpl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/public/security/automatic")
@Tag(name = "Automatic processing")
public class AutomaticSecurityController {
    private static final int CODE = 1234;
    private final AutomaticSecurityServiceImpl automaticService;

    @Operation(summary = "Automatic change status product", description = "Select all expired products with users." +
            " For each user, we check whether there is space in the archive, if there is no space," +
            " we delete older products.",responses = {
            @ApiResponse(responseCode = "204", description = "Product change status"),
            @ApiResponse(responseCode = "400", description = "Invalid input",
                    content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ExceptionResponseDto.class))}),
            @ApiResponse(responseCode = "404", description = "Product by UUID not found",
                    content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ExceptionResponseDto.class))})
    })
    @PutMapping("/reset/{code}")
    public ResponseEntity<Void> resetAttempts(
            @Parameter(description = "Code start", required = true)
            @PathVariable(name = "code") Integer code
    ) {
        if (code != CODE){throw new ClientBackendException(ErrorCode.UNKNOWN_SERVER_ERROR);
        }
        automaticService.resetAttempt();

        return ResponseEntity.noContent().build();
    }
}
