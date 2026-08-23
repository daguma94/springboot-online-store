package com.example.service.impl;

import com.example.dto.OrderItemRequest;
import com.example.dto.OrderRequest;
import com.example.entity.Category;
import com.example.entity.Order;
import com.example.entity.Product;
import com.example.entity.Role;
import com.example.entity.User;
import com.example.exception.InsufficientStockException;
import com.example.exception.ResourceNotFoundException;
import com.example.repository.OrderRepository;
import com.example.repository.ProductRepository;
import com.example.repository.UserRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

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
class OrderServiceImplTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private ProductRepository productRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private OrderServiceImpl orderService;

    private User user;
    private Product product;

    @BeforeEach
    void setUp() {

        user = new User(
                1L,
                "daniel",
                "daniel@example.com",
                "encodedPassword",
                Role.USER
        );

        Category category = new Category(
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
    void shouldCreateOrder() {

        OrderItemRequest itemRequest = new OrderItemRequest();
        itemRequest.setProductId(1L);
        itemRequest.setQuantity(2);

        OrderRequest request = new OrderRequest();
        request.setItems(List.of(itemRequest));

        when(userRepository.findByUsername("daniel"))
                .thenReturn(Optional.of(user));

        when(productRepository.findById(1L))
                .thenReturn(Optional.of(product));

        when(orderRepository.save(any(Order.class)))
                .thenAnswer(invocation -> {
                    Order order = invocation.getArgument(0);
                    order.setId(1L);
                    return order;
                });

        var response = orderService.create(request, "daniel");

        assertNotNull(response);
        assertEquals(1L, response.getId());
        assertEquals(new BigDecimal("179.98"), response.getTotal());
        assertEquals(1, response.getItems().size());

        assertEquals(
                "Mechanical Keyboard",
                response.getItems().get(0).getProductName()
        );

        assertEquals(8, product.getStock());

        verify(userRepository).findByUsername("daniel");
        verify(productRepository).findById(1L);
        verify(orderRepository).save(any(Order.class));
    }

    @Test
    void shouldThrowExceptionWhenStockIsInsufficient() {

        OrderItemRequest itemRequest = new OrderItemRequest();
        itemRequest.setProductId(1L);
        itemRequest.setQuantity(20);

        OrderRequest request = new OrderRequest();
        request.setItems(List.of(itemRequest));

        when(userRepository.findByUsername("daniel"))
                .thenReturn(Optional.of(user));

        when(productRepository.findById(1L))
                .thenReturn(Optional.of(product));

        assertThrows(
                InsufficientStockException.class,
                () -> orderService.create(request, "daniel")
        );

        assertEquals(10, product.getStock());

        verify(userRepository).findByUsername("daniel");
        verify(productRepository).findById(1L);

        verify(orderRepository, never())
                .save(any(Order.class));
    }

    @Test
    void shouldThrowExceptionWhenProductDoesNotExist() {

        OrderItemRequest itemRequest = new OrderItemRequest();
        itemRequest.setProductId(99L);
        itemRequest.setQuantity(2);

        OrderRequest request = new OrderRequest();
        request.setItems(List.of(itemRequest));

        when(userRepository.findByUsername("daniel"))
                .thenReturn(Optional.of(user));

        when(productRepository.findById(99L))
                .thenReturn(Optional.empty());

        assertThrows(
                ResourceNotFoundException.class,
                () -> orderService.create(request, "daniel")
        );

        verify(userRepository).findByUsername("daniel");
        verify(productRepository).findById(99L);

        verify(orderRepository, never())
                .save(any(Order.class));
    }

    @Test
    void shouldCreateOrderWithMultipleProducts() {

        Product secondProduct = new Product(
                2L,
                "Gaming Mouse",
                "Wireless gaming mouse",
                new BigDecimal("49.99"),
                20,
                product.getCategory()
        );

        OrderItemRequest firstItem = new OrderItemRequest();
        firstItem.setProductId(1L);
        firstItem.setQuantity(2);

        OrderItemRequest secondItem = new OrderItemRequest();
        secondItem.setProductId(2L);
        secondItem.setQuantity(3);

        OrderRequest request = new OrderRequest();
        request.setItems(List.of(firstItem, secondItem));

        when(userRepository.findByUsername("daniel"))
                .thenReturn(Optional.of(user));

        when(productRepository.findById(1L))
                .thenReturn(Optional.of(product));

        when(productRepository.findById(2L))
                .thenReturn(Optional.of(secondProduct));

        when(orderRepository.save(any(Order.class)))
                .thenAnswer(invocation -> {
                    Order order = invocation.getArgument(0);
                    order.setId(1L);
                    return order;
                });

        var response = orderService.create(request, "daniel");

        assertNotNull(response);
        assertEquals(1L, response.getId());
        assertEquals(2, response.getItems().size());

        assertEquals(
                new BigDecimal("329.95"),
                response.getTotal()
        );

        assertEquals(8, product.getStock());
        assertEquals(17, secondProduct.getStock());

        verify(userRepository).findByUsername("daniel");
        verify(productRepository).findById(1L);
        verify(productRepository).findById(2L);
        verify(orderRepository).save(any(Order.class));
    }
}
