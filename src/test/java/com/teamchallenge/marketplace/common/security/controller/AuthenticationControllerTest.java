package com.teamchallenge.marketplace.common.security.controller;

import com.teamchallenge.marketplace.common.security.bean.UserAccount;
import com.teamchallenge.marketplace.common.security.dto.request.AuthenticationRequest;
import com.teamchallenge.marketplace.common.security.dto.response.AuthenticationResponse;
import com.teamchallenge.marketplace.common.security.service.JwtService;
import com.teamchallenge.marketplace.user.persisit.entity.UserEntity;
import com.teamchallenge.marketplace.user.persisit.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.AuthenticationException;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE,
        classes = AuthenticationController.class)
@ActiveProfiles("test")
class AuthenticationControllerTest {

    @Autowired
    private AuthenticationController controller;

    @MockBean
    private UserRepository repository;
    @MockBean
    private JwtService jwtService;

    @Test
    void whenAuthenticateSuccessful() {
        String token = "token";
        when(repository.findByEmail(anyString())).thenReturn(getDefaultUser());
        when(jwtService.generateToken(any(UserAccount.class))).thenReturn(token);
        ResponseEntity<AuthenticationResponse> authenticate = controller.authenticate(new AuthenticationRequest("", ""));
        assertNotNull(authenticate);
        assertEquals(token, authenticate.getBody().authenticationToken());
    }

    @Test
    void whenAuthenticateFailed() {
        when(repository.findByEmail(anyString())).thenReturn(getEmptyUser());
        assertThrows(AuthenticationException.class,() -> controller.authenticate(new AuthenticationRequest("", "")));
    }


    @Test
    void whenAuthenticateForbidden() {
        when(repository.findByEmail(anyString())).thenReturn(getEmptyUser());
        assertThrows(AuthenticationException.class,() -> controller.authenticate(new AuthenticationRequest("", "")));
    }

    @Test
    void whenAuthenticateConflict() {
        when(repository.findByEmail(anyString())).thenReturn(getEmptyUser());
        assertThrows(AuthenticationException.class,() -> controller.authenticate(new AuthenticationRequest("", "")));
    }

    @Test
    void whenAuthenticateByPhoneSuccessful() {
        String token = "token";
        when(repository.findByPhoneNumber(anyString())).thenReturn(getDefaultUser());
        when(jwtService.generateToken(any(UserAccount.class))).thenReturn(token);
        ResponseEntity<AuthenticationResponse> authenticate = controller.authenticate(new AuthenticationRequest("", ""));
        assertNotNull(authenticate);
        assertEquals(token, authenticate.getBody().authenticationToken());
    }
    @Test
    void whenAuthenticateByPhoneFailed() {
        when(repository.findByPhoneNumber(anyString())).thenReturn(getEmptyUser());
        assertThrows(AuthenticationException.class,() -> controller.authenticate(new AuthenticationRequest("", "")));
    }

    @Test
    void whenAuthenticateByPhoneForbidden() {
        when(repository.findByPhoneNumber(anyString())).thenReturn(getEmptyUser());
        assertThrows(AuthenticationException.class,() -> controller.authenticate(new AuthenticationRequest("", "")));
    }

    @Test
    void whenAuthenticateByPhoneConflict() {
        when(repository.findByPhoneNumber(anyString())).thenReturn(getEmptyUser());
        assertThrows(AuthenticationException.class,() -> controller.authenticate(new AuthenticationRequest("", "")));
    }

    private Optional<UserEntity> getDefaultUser() {
        UserEntity user = new UserEntity();
        user.setUsername("Ivan Petrov");
        user.setEmail("ivan.petrov@example.ua");
        user.setPassword("bezpechnyParol456");
        user.setPhoneNumber("+380987654321");
        return Optional.of(user);
    }

    private Optional<UserEntity> getEmptyUser() {
        return Optional.empty();
    }
}