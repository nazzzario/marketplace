package com.teamchallenge.marketplace.password.controller;

import com.teamchallenge.marketplace.common.exception.dto.ExceptionResponseDto;
import com.teamchallenge.marketplace.password.dto.request.PasswordResetTokenRequestDto;
import com.teamchallenge.marketplace.password.dto.request.PasswordResetRequestDto;
import com.teamchallenge.marketplace.password.service.PasswordService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1")
@Tag(name = "Reset password", description = "API for reset user password by email")
public class ResetPasswordController {

    private final PasswordService passwordService;

    @Operation(summary = "Reset user forgotten password", description = "Send an email for changing password",responses = {
            @ApiResponse(responseCode = "204", description = "Reset token has been send"),
            @ApiResponse(responseCode = "400", description = "Invalid input",
                    content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ExceptionResponseDto.class))})
    })
    @PostMapping("/public/reset-password")
    public ResponseEntity<Void> sendResetToken(
            @Parameter(description = "Email to reset user password", required = true)
            @Valid @RequestBody PasswordResetTokenRequestDto resetRequestDto) {

        passwordService.sendResetPasswordToken(resetRequestDto);

        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @Operation(summary = "Change forgotten password", description = "Change forgotten password by reset token",responses = {
            @ApiResponse(responseCode = "204", description = "Password changed successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input",
                    content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ExceptionResponseDto.class))})
    })
    @PostMapping("/public/reset-password/{resetToken}")
    public ResponseEntity<Void> changePassword(
            @Parameter(description = "User reset token form email", required = true)
            @PathVariable String resetToken,
            @Valid @RequestBody PasswordResetRequestDto requestDto){

        passwordService.changeForgottenPassword(resetToken, requestDto);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
