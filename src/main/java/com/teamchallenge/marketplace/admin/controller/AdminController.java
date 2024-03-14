package com.teamchallenge.marketplace.admin.controller;

import com.teamchallenge.marketplace.admin.dto.AdminReportDto;
import com.teamchallenge.marketplace.admin.dto.ComplaintDto;
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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/private/users/")
@Tag(name = "Admin")
public class AdminController {

    private final AdminService adminService;

    @Operation(summary = "Summary report for admins", description = "Summary report for admins")

    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Get report"),
            @ApiResponse(responseCode = "401", description = "Unauthenticated",
                    content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ExceptionResponseDto.class))}),
            @ApiResponse(responseCode = "403", description = "Access denied",
                    content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ExceptionResponseDto.class))})
    })
    @PreAuthorize("@userSecurity.checkAdminRights()")
    @GetMapping("report")
    public ResponseEntity<Page<AdminReportDto>> summaryReport(
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "10") Integer size
    ){
        Page<AdminReportDto> report = adminService.getReport(PageRequest.of(page, size));

        return new ResponseEntity<>(report, HttpStatus.OK);
    }

    @Operation(summary = "Block, unblock user by the admin", description = "If user unblock request block user or " +
            "user block request unblock user by the admin")

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

    @Operation(summary = "Get list of feedback", description = "Get list of feedback", responses = {
            @ApiResponse(responseCode = "200", description = "Get list of feedback"),
            @ApiResponse(responseCode = "401", description = "Unauthenticated",
                    content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ExceptionResponseDto.class))}),
            @ApiResponse(responseCode = "403", description = "Access denied",
                    content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ExceptionResponseDto.class))}),
            @ApiResponse(responseCode = "404", description = "User not found",
                    content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ExceptionResponseDto.class))})

    })
    @PreAuthorize("@userSecurity.checkAdminRights()")
    @GetMapping("feedback/users")
    public ResponseEntity<List<ComplaintDto>> getComplaintProducts() {
        List<ComplaintDto> feedbackUsers = adminService.getFeedbackUsers();

        return new ResponseEntity<>(feedbackUsers, HttpStatus.OK);
    }

    @Operation(summary = "Delete feedback", description = "Admin can delete  feedback user", responses = {
            @ApiResponse(responseCode = "204", description = "Delete feedback user"),
            @ApiResponse(responseCode = "401", description = "Unauthenticated",
                    content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ExceptionResponseDto.class))}),
            @ApiResponse(responseCode = "403", description = "Access denied",
                    content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ExceptionResponseDto.class))}),
            @ApiResponse(responseCode = "404", description = "Product not found",
                    content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ExceptionResponseDto.class))})

    })
    @PreAuthorize("@userSecurity.checkAdminRights()")
    @DeleteMapping("feedback/{userReference}/delete")
    public ResponseEntity<Void> deleteFeedbackProduct(
            @PathVariable UUID userReference
    ) {
        adminService.deleteFeedback(userReference);

        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Change user to fake by the admin", description = "Change user to fake by the admin")

    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Patched user"),
            @ApiResponse(responseCode = "401", description = "Unauthenticated",
                    content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ExceptionResponseDto.class))}),
            @ApiResponse(responseCode = "403", description = "Access denied",
                    content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ExceptionResponseDto.class))})
    })
    @PreAuthorize("@userSecurity.checkAdminRights()")
    @DeleteMapping("{userReference}/fake")
    public ResponseEntity<Void> changeUserToFake(@PathVariable UUID userReference){
        adminService.changeUserToFake(userReference);

        return ResponseEntity.noContent().build();
    }
}
