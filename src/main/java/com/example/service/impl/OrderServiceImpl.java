package com.example.service.impl;

import com.example.dto.OrderItemRequest;
import com.example.dto.OrderItemResponse;
import com.example.dto.OrderRequest;
import com.example.dto.OrderResponse;
import com.example.entity.Order;
import com.example.entity.OrderItem;
import com.example.entity.OrderStatus;
import com.example.entity.Product;
import com.example.entity.User;
import com.example.exception.InsufficientStockException;
import com.example.exception.ResourceNotFoundException;
import com.example.repository.OrderRepository;
import com.example.repository.ProductRepository;
import com.example.repository.UserRepository;
import com.example.service.OrderService;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;

    public OrderServiceImpl(
            OrderRepository orderRepository,
            ProductRepository productRepository,
            UserRepository userRepository) {

        this.orderRepository = orderRepository;
        this.productRepository = productRepository;
        this.userRepository = userRepository;
    }

    @Override
    @Transactional
    public OrderResponse create(OrderRequest request, String username) {

        User user = userRepository.findByUsername(username)
                .orElseThrow(() ->
                        new ResourceNotFoundException("User not found"));

        Order order = new Order();
        order.setUser(user);
        order.setStatus(OrderStatus.PENDING);
        order.setCreatedAt(LocalDateTime.now());

        BigDecimal total = BigDecimal.ZERO;
        List<OrderItem> orderItems = new ArrayList<>();

        for (OrderItemRequest itemRequest : request.getItems()) {

            Product product = productRepository
                    .findById(itemRequest.getProductId())
                    .orElseThrow(() ->
                            new ResourceNotFoundException("Product not found"));

            if (product.getStock() < itemRequest.getQuantity()) {
                throw new InsufficientStockException(
                    "Insufficient stock for product: " + product.getName()
                );
            }

            BigDecimal subtotal = product.getPrice()
                    .multiply(BigDecimal.valueOf(itemRequest.getQuantity()));

            OrderItem orderItem = new OrderItem();

            orderItem.setOrder(order);
            orderItem.setProduct(product);
            orderItem.setQuantity(itemRequest.getQuantity());
            orderItem.setUnitPrice(product.getPrice());
            orderItem.setSubtotal(subtotal);

            orderItems.add(orderItem);

            total = total.add(subtotal);

            product.setStock(
                    product.getStock() - itemRequest.getQuantity()
            );
        }

        order.setItems(orderItems);
        order.setTotal(total);

        Order savedOrder = orderRepository.save(order);

        return toResponse(savedOrder);
    }

    private OrderResponse toResponse(Order order) {

        List<OrderItemResponse> items = order.getItems()
                .stream()
                .map(item -> new OrderItemResponse(
                        item.getProduct().getId(),
                        item.getProduct().getName(),
                        item.getQuantity(),
                        item.getUnitPrice(),
                        item.getSubtotal()
                ))
                .toList();

        return new OrderResponse(
                order.getId(),
                order.getTotal(),
                order.getStatus(),
                order.getCreatedAt(),
                items
        );
    }

    @Override
    @Transactional(readOnly = true)
    public List<OrderResponse> findMyOrders(String username) {

        return orderRepository
                .findByUserUsernameOrderByCreatedAtDesc(username)
                .stream()
                .map(this::toResponse)
                .toList();
    }
}
