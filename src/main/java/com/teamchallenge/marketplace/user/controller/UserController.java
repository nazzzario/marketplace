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

    @GetMapping("/info")
    public ResponseEntity<UserResponseDto> getCurrentUserInfo(){
        UserResponseDto userResponseDto = new UserResponseDto(null, "Катерина Meow", "+ 093 83 65 121");

        return new ResponseEntity<>(userResponseDto, HttpStatus.OK);
    }

    @GetMapping("/{reference}")
    public ResponseEntity<UserResponseDto> getUserByReference(@PathVariable(name = "reference") UUID reference){
        UserResponseDto userByReference = userService.getUserByReference(reference);

        return ResponseEntity.ok(userByReference);
    }

    @PostMapping("/create")
    public ResponseEntity<UserResponseDto> createUser(@RequestBody UserRequestDto requestDto){
        UserResponseDto createdUser = userService.createUser(requestDto);

        return new ResponseEntity<>(createdUser, HttpStatus.CREATED);
    }
}
