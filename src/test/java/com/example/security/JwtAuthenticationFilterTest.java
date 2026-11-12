package com.example.security;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.security.SignatureException;

import jakarta.servlet.FilterChain;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class JwtAuthenticationFilterTest {

    private JwtService jwtService;
    private CustomUserDetailsService userDetailsService;
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @BeforeEach
    void setUp() {

        jwtService = mock(JwtService.class);
        userDetailsService = mock(CustomUserDetailsService.class);

        jwtAuthenticationFilter = new JwtAuthenticationFilter(
                jwtService,
                userDetailsService
        );
    }

    @Test
    void tokenManipuladoDevuelve401() throws Exception {

        MockHttpServletRequest request =
                new MockHttpServletRequest();

        MockHttpServletResponse response =
                new MockHttpServletResponse();

        FilterChain filterChain = mock(FilterChain.class);

        request.addHeader(
                "Authorization",
                "Bearer token-manipulado"
        );

        when(jwtService.extractUsername("token-manipulado"))
                .thenThrow(new SignatureException("Firma inválida"));

        jwtAuthenticationFilter.doFilter(
                request,
                response,
                filterChain
        );

        assertEquals(401, response.getStatus());

        verify(filterChain, never())
                .doFilter(request, response);
    }

    @Test
    void tokenCaducadoDevuelve401() throws Exception {

        MockHttpServletRequest request =
                new MockHttpServletRequest();

        MockHttpServletResponse response =
                new MockHttpServletResponse();

        FilterChain filterChain = mock(FilterChain.class);

        request.addHeader(
                "Authorization",
                "Bearer token-caducado"
        );

        when(jwtService.extractUsername("token-caducado"))
                .thenThrow(new ExpiredJwtException(
                        null,
                        null,
                        "El token ha caducado"
                ));

        jwtAuthenticationFilter.doFilter(
                request,
                response,
                filterChain
        );

        assertEquals(401, response.getStatus());

        verify(filterChain, never())
                .doFilter(request, response);
    }
}