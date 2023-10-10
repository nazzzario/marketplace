package com.teamchallenge.marketplace.user.controller;

import com.teamchallenge.marketplace.user.dto.request.UserRequestDto;
import com.teamchallenge.marketplace.user.dto.response.UserResponseDto;
import com.teamchallenge.marketplace.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/public/users")
public class UserController {

    private final UserService userService;

    @GetMapping("/{reference}")
    public ResponseEntity<UserResponseDto> getUserByReference(@PathVariable(name = "reference") UUID reference){
        UserResponseDto userByReference = userService.getUserByReference(reference);

        return ResponseEntity.ok(userByReference);
    }

    @PostMapping("/registration")
    public ResponseEntity<UserResponseDto> userRegistration(@RequestBody UserRequestDto requestDto){
        UserResponseDto createdUser = userService.userRegistration(requestDto);

        return new ResponseEntity<>(createdUser, HttpStatus.CREATED);
    }
}
