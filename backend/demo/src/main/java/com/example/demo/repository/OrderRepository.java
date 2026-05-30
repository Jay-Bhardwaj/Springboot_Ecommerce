package com.example.demo.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.demo.entity.Order;
import com.example.demo.entity.userEntity;

import java.util.List;

public interface OrderRepository extends JpaRepository<Order, Long> {

    List<Order> findByUserOrderByPlacedAtDesc(userEntity user);
}
