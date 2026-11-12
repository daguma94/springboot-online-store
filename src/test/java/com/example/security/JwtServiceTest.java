package com.example.security;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;

import org.springframework.test.util.ReflectionTestUtils;

import io.jsonwebtoken.JwtException;

import java.util.Base64;

import static org.junit.jupiter.api.Assertions.*;

class JwtServiceTest {

    private JwtService jwtService;
    private UserDetails userDetails;

    @BeforeEach
    void setUp() {

        jwtService = new JwtService();

        byte[] key = new byte[32];
        new java.security.SecureRandom().nextBytes(key);

        String secret = Base64.getEncoder()
                .encodeToString(key);

        ReflectionTestUtils.setField(
                jwtService, "secret", secret);

        ReflectionTestUtils.setField(
                jwtService, "expiration", 3600000L);

        userDetails = User.withUsername("admin")
                .password("passwordCodificada")
                .roles("ADMIN")
                .build();
    }

    @Test
    void tokenValidoDevuelveTrue() {

        String token = jwtService.generateToken(userDetails);

        assertTrue(jwtService.isTokenValid(token, userDetails));
        assertEquals("admin", jwtService.extractUsername(token));
    }

    @Test
    void tokenManipuladoLanzaExcepcion() {

        String token = jwtService.generateToken(userDetails);

        String[] partes = token.split("\\.");

        // Modificamos el contenido firmado, conservando la firma original.
        String payloadManipulado = Base64.getUrlEncoder()
                .withoutPadding()
                .encodeToString(
                        "{\"sub\":\"intruso\"}".getBytes(
                                java.nio.charset.StandardCharsets.UTF_8)
                );

        String tokenManipulado =
                partes[0] + "." + payloadManipulado + "." + partes[2];

        assertThrows(
                JwtException.class,
                () -> jwtService.extractUsername(tokenManipulado)
        );
    }

    @Test
    void tokenCaducadoLanzaExcepcion() {

        ReflectionTestUtils.setField(
                jwtService, "expiration", -10000L);

        String token = jwtService.generateToken(userDetails);

        assertThrows(
                io.jsonwebtoken.ExpiredJwtException.class,
                () -> jwtService.extractUsername(token)
        );
    }
}