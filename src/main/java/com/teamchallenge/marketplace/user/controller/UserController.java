package com.teamchallenge.marketplace.user.controller;

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
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/public/users")
@Tag(name = "User")
public class UserController {

    private final UserService userService;

    @Operation(summary = "Get user", description = "Get user by reference")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User data"),
            @ApiResponse(responseCode = "403", description = "Invalid data"),
            @ApiResponse(responseCode = "404", description = "User not found")
    })
    @GetMapping("/{reference}")
    public ResponseEntity<UserResponseDto> getUserByReference(@PathVariable(name = "reference") UUID reference){
        UserResponseDto userByReference = userService.getUserByReference(reference);

        return ResponseEntity.ok(userByReference);
    }

    @Operation(summary = "User registration", description = "Create new user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "User created"),
            @ApiResponse(responseCode = "403", description = "Invalid user data data"),
    })
    @PostMapping("/registration")
    public ResponseEntity<UserResponseDto> userRegistration(@Valid @RequestBody UserRequestDto requestDto){
        UserResponseDto createdUser = userService.userRegistration(requestDto);

        return new ResponseEntity<>(createdUser, HttpStatus.CREATED);
    }
}
