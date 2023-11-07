package com.teamchallenge.marketplace.user.controller;

import com.teamchallenge.marketplace.user.dto.request.UserPatchRequestDto;
import com.teamchallenge.marketplace.user.dto.request.UserRequestDto;
import com.teamchallenge.marketplace.user.dto.response.UserResponseDto;
import com.teamchallenge.marketplace.user.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/")
@Tag(name = "User")
public class UserController {

    private final UserService userService;

    @Operation(summary = "Get user", description = "Get user by reference")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User data"),
            @ApiResponse(responseCode = "403", description = "Invalid data"),
            @ApiResponse(responseCode = "404", description = "User not found")
    })
    @GetMapping("public/users/{reference}")
    public ResponseEntity<UserResponseDto> getUserByReference(@PathVariable(name = "reference") UUID reference){
        UserResponseDto userByReference = userService.getUserByReference(reference);

        return ResponseEntity.ok(userByReference);
    }

    @Operation(summary = "User registration", description = "Create new user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "User created"),
            @ApiResponse(responseCode = "403", description = "Invalid user data"),
    })
    @PostMapping("public/users/registration")
    public ResponseEntity<UserResponseDto> userRegistration(@Valid @RequestBody UserRequestDto requestDto){
        UserResponseDto createdUser = userService.userRegistration(requestDto);

        return new ResponseEntity<>(createdUser, HttpStatus.CREATED);
    }

    @Operation(summary = "Edit user information", description = "Change user data by it reference. " +
            "1. GET user by reference\n2.Pass user data with updated fields")

    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Patched user"),
            @ApiResponse(responseCode = "401", description = "Unauthenticated"),
            @ApiResponse(responseCode = "403", description = "Access denied")
    })
    @PreAuthorize("@userSecurity.checkReference(#userReference)")
    @PatchMapping("private/users/{userReference}")
    public ResponseEntity<UserResponseDto> patchUser(
            @PathVariable UUID userReference,
            @Valid @RequestBody UserPatchRequestDto requestDto){
        UserResponseDto patchedUser = userService.patchUser(userReference, requestDto);

        return new ResponseEntity<>(patchedUser, HttpStatus.OK);
    }
}
