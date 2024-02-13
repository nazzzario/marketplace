package com.teamchallenge.marketplace.admin.controller;

import com.teamchallenge.marketplace.admin.service.AdminService;
import com.teamchallenge.marketplace.common.exception.dto.ExceptionResponseDto;
import com.teamchallenge.marketplace.user.persisit.entity.enums.RoleEnum;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/private/users/")
@Tag(name = "Admin")
public class AdminController {

    private final AdminService adminService;

    @Operation(summary = "Block user by the admin", description = "Block user by the admin")

    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Patched user"),
            @ApiResponse(responseCode = "401", description = "Unauthenticated",
                    content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ExceptionResponseDto.class))}),
            @ApiResponse(responseCode = "403", description = "Access denied",
                    content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ExceptionResponseDto.class))})
    })
    @PreAuthorize("@userSecurity.checkAdminRights()")
    @PatchMapping("{userReference}/block")
    public ResponseEntity<Void> blockUser(@PathVariable UUID userReference){
        adminService.blockUser(userReference);

        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Change user role by the root", description = "Change user role by the root")

    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Patched user"),
            @ApiResponse(responseCode = "401", description = "Unauthenticated",
                    content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ExceptionResponseDto.class))}),
            @ApiResponse(responseCode = "403", description = "Access denied",
                    content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ExceptionResponseDto.class))})
    })
    @PreAuthorize("@userSecurity.checkRootRights()")
    @PatchMapping("{userReference}/change/{role}")
    public ResponseEntity<Void> changeUserRole(@PathVariable UUID userReference,
                                               @PathVariable RoleEnum role){
        adminService.changeUserRole(userReference, role);

        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Delete user by the admin", description = "Delete user by the admin")

    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Patched user"),
            @ApiResponse(responseCode = "401", description = "Unauthenticated",
                    content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ExceptionResponseDto.class))}),
            @ApiResponse(responseCode = "403", description = "Access denied",
                    content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ExceptionResponseDto.class))})
    })
    @PreAuthorize("@userSecurity.checkAdminRights()")
    @DeleteMapping("{userReference}/delete")
    public ResponseEntity<Void> deleteUser(@PathVariable UUID userReference){
        adminService.deleteUser(userReference);

        return ResponseEntity.noContent().build();
    }
}
