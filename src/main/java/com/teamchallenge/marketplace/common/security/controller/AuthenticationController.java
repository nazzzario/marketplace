package com.teamchallenge.marketplace.common.security.controller;

import com.teamchallenge.marketplace.common.security.bean.UserAccount;
import com.teamchallenge.marketplace.common.security.dto.request.AuthenticationRequest;
import com.teamchallenge.marketplace.common.security.dto.response.AuthenticationResponse;
import com.teamchallenge.marketplace.common.security.service.JwtService;
import com.teamchallenge.marketplace.user.persisit.entity.UserEntity;
import com.teamchallenge.marketplace.user.persisit.repository.UserRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/public")
@Tag(name = "Authentication")
public class AuthenticationController {

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final UserRepository userRepository;


    @PostMapping("/auth")
    @Operation(summary = "Authenticate user", description = "Input user credentials to get JWT token", responses = {
            @ApiResponse(responseCode = "200", description = "User authentication token returned"),
            @ApiResponse(responseCode = "400", description = "Invalid input"),
            @ApiResponse(responseCode = "401", description = "Invalid user credentials")
    })
    public ResponseEntity<AuthenticationResponse> authenticate(@Valid @RequestBody AuthenticationRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.email(), request.password()
                )
        );

        UserEntity userEntity = userRepository.findByEmail(request.email()).orElseThrow();
        String token = jwtService.generateToken(UserAccount.fromUserEntityToCustomUserDetails(userEntity));

        return new ResponseEntity<>(new AuthenticationResponse(userEntity.getReference(),token), HttpStatus.OK);
    }
}
