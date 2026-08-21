package com.example.repository;

import com.example.entity.Order;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderRepository extends JpaRepository<Order, Long> {

    List<Order> findByUserUsernameOrderByCreatedAtDesc(String username);
}
