package com.example.controller;

import com.example.dto.OrderRequest;
import com.example.dto.OrderResponse;
import com.example.service.OrderService;

import jakarta.validation.Valid;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping
    public ResponseEntity<OrderResponse> create(
            @Valid @RequestBody OrderRequest request,
            Authentication authentication) {

        String username = authentication.getName();

        OrderResponse order = orderService.create(
                request,
                username
        );

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(order);
    }

    @GetMapping
    public ResponseEntity<List<OrderResponse>> findMyOrders(
            Authentication authentication) {

        String username = authentication.getName();

        return ResponseEntity.ok(
                orderService.findMyOrders(username)
        );
    }
}
