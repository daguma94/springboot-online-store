package com.example.service;

import java.util.List;

import com.example.dto.OrderRequest;
import com.example.dto.OrderResponse;

public interface OrderService {

    OrderResponse create(OrderRequest request, String username);

    List<OrderResponse> findMyOrders(String username);
}
