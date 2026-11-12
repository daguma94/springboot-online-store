package com.example.controller;

import com.example.dto.AuthResponse;
import com.example.dto.LoginRequest;
import com.example.security.JwtService;
import com.example.service.UserService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.User;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class AuthControllerTest {

    @Mock
    private UserService userService;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private JwtService jwtService;

    @InjectMocks
    private AuthController authController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void loginConCredencialesIncorrectasDevuelve401() {

        LoginRequest request = new LoginRequest();
        request.setUsername("admin");
        request.setPassword("passwordIncorrecta");

        when(authenticationManager.authenticate(
                any(UsernamePasswordAuthenticationToken.class)
        )).thenThrow(new BadCredentialsException("Credenciales incorrectas"));

        ResponseEntity<?> response = authController.login(request);

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
    }

    @Test
    void loginConCredencialesCorrectasDevuelve200() {

        LoginRequest request = new LoginRequest();
        request.setUsername("admin");
        request.setPassword("password123");

        UserDetails userDetails = User
                .withUsername("admin")
                .password("passwordCodificada")
                .roles("ADMIN")
                .build();

        Authentication authentication = new UsernamePasswordAuthenticationToken(
                userDetails,
                null,
                userDetails.getAuthorities()
        );

        when(authenticationManager.authenticate(
                any(UsernamePasswordAuthenticationToken.class)
        )).thenReturn(authentication);

        when(jwtService.generateToken(userDetails))
                .thenReturn("jwt-de-prueba");

        ResponseEntity<AuthResponse> response =
                authController.login(request);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());

        verify(jwtService).generateToken(userDetails);
    }
}