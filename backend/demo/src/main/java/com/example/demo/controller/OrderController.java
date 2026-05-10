package com.example.demo.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.example.demo.dto.CheckoutRequest;
import com.example.demo.dto.CheckoutResponse;
import com.example.demo.entity.userEntity;
import com.example.demo.repository.userRepository;
import com.example.demo.service.OrderService;
import org.springframework.http.HttpStatus;

@RestController
@RequestMapping("/orders")
public class OrderController {

    private final OrderService orderService;
    private final userRepository userRepository;

    public OrderController(OrderService orderService, userRepository userRepository) {
        this.orderService = orderService;
        this.userRepository = userRepository;
    }

    @PostMapping("/checkout")
    public ResponseEntity<CheckoutResponse> checkout(
            Authentication authentication,
            @RequestBody CheckoutRequest request) {
        if (authentication == null || authentication.getPrincipal() == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Unauthorized user");
        }

        userEntity user = userRepository.findByEmail(authentication.getPrincipal().toString());
        if (user == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found");
        }

        return ResponseEntity.ok(orderService.placeOrder(user, request));
    }
}
