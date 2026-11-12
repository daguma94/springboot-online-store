package com.example.controller;

import com.example.service.ProductService;
import com.example.security.JwtService;
import com.example.config.SecurityConfig;
import com.example.dto.ProductRequest;
import com.example.dto.ProductResponse;
import com.example.security.CustomUserDetailsService;

import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.math.BigDecimal;

@WebMvcTest(ProductController.class)
@Import(SecurityConfig.class)
class ProductControllerSecurityTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ProductService productService;

    @MockitoBean
    private JwtService jwtService;

    @MockitoBean
    private CustomUserDetailsService userDetailsService;

    @Test
    void findAllSinJwtDevuelve401() throws Exception {

        mockMvc.perform(get("/api/products"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(username = "daniel", roles = "USER")
    void createConRolUserDevuelve403() throws Exception {

        mockMvc.perform(post("/api/products")
                .contentType("application/json")
                .content("{}"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = "admin", roles = "ADMIN")
    void createConRolAdminDevuelve201() throws Exception {

        when(productService.create(any(ProductRequest.class)))
                .thenReturn(new ProductResponse(
                        1L,
                        "Portátil",
                        "Portátil de prueba",
                        new BigDecimal("999.99"),
                        10,
                        1L,
                        "Informática"
                ));

        mockMvc.perform(post("/api/products")
                .contentType("application/json")
                .content("""
                    {
                        "name": "Portátil",
                        "description": "Portátil de prueba",
                        "price": 999.99,
                        "stock": 10,
                        "categoryId": 1
                    }
                    """))
                .andExpect(status().isCreated());

        verify(productService).create(any(ProductRequest.class));
    }
}