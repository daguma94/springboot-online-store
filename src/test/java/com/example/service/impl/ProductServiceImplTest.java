package com.example.service.impl;

import com.example.dto.ProductRequest;
import com.example.entity.Category;
import com.example.entity.Product;
import com.example.exception.ResourceNotFoundException;
import com.example.repository.CategoryRepository;
import com.example.repository.ProductRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductServiceImplTest {

    @Mock
    private ProductRepository productRepository;

    @Mock
    private CategoryRepository categoryRepository;

    @InjectMocks
    private ProductServiceImpl productService;

    private Category category;
    private Product product;

    @BeforeEach
    void setUp() {

        category = new Category(
                1L,
                "Peripherals",
                "Computer peripherals"
        );

        product = new Product(
                1L,
                "Mechanical Keyboard",
                "RGB keyboard",
                new BigDecimal("89.99"),
                10,
                category
        );
    }

    @Test
    void shouldFindProductById() {

        when(productRepository.findById(1L))
                .thenReturn(Optional.of(product));

        var response = productService.findById(1L);

        assertNotNull(response);
        assertEquals(1L, response.getId());
        assertEquals("Mechanical Keyboard", response.getName());
        assertEquals(new BigDecimal("89.99"), response.getPrice());

        verify(productRepository).findById(1L);
    }

    @Test
    void shouldThrowExceptionWhenProductDoesNotExist() {

        when(productRepository.findById(99L))
                .thenReturn(Optional.empty());

        assertThrows(
                ResourceNotFoundException.class,
                () -> productService.findById(99L)
        );

        verify(productRepository).findById(99L);
    }

    @Test
    void shouldCreateProduct() {

        ProductRequest request = new ProductRequest();
        request.setName("Gaming Mouse");
        request.setDescription("Wireless gaming mouse");
        request.setPrice(new BigDecimal("49.99"));
        request.setStock(20);
        request.setCategoryId(1L);

        when(categoryRepository.findById(1L))
                .thenReturn(Optional.of(category));

        when(productRepository.save(any(Product.class)))
                .thenAnswer(invocation -> {
                    Product savedProduct = invocation.getArgument(0);
                    savedProduct.setId(2L);
                    return savedProduct;
                });

        var response = productService.create(request);

        assertNotNull(response);
        assertEquals(2L, response.getId());
        assertEquals("Gaming Mouse", response.getName());
        assertEquals(new BigDecimal("49.99"), response.getPrice());
        assertEquals(20, response.getStock());
        assertEquals(1L, response.getCategoryId());

        verify(categoryRepository).findById(1L);
        ArgumentCaptor<Product> productCaptor =
            ArgumentCaptor.forClass(Product.class);

        verify(productRepository).save(productCaptor.capture());

        Product capturedProduct = productCaptor.getValue();

        assertEquals("Gaming Mouse", capturedProduct.getName());
        assertEquals("Wireless gaming mouse", capturedProduct.getDescription());
        assertEquals(new BigDecimal("49.99"), capturedProduct.getPrice());
        assertEquals(20, capturedProduct.getStock());
        assertEquals(category, capturedProduct.getCategory());
    }

    @Test
    void shouldFindAllProducts() {

        Product secondProduct = new Product(
                2L,
                "Gaming Mouse",
                "Wireless gaming mouse",
                new BigDecimal("49.99"),
                20,
                category
        );

        when(productRepository.findAll())
                .thenReturn(List.of(product, secondProduct));

        var response = productService.findAll();

        assertNotNull(response);
        assertEquals(2, response.size());

        assertEquals("Mechanical Keyboard", response.get(0).getName());
        assertEquals("Gaming Mouse", response.get(1).getName());

        verify(productRepository).findAll();
    }
}
