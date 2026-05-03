package com.example.demo.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.example.demo.dto.CartItemRequest;
import com.example.demo.dto.CartSummaryResponse;
import com.example.demo.entity.userEntity;
import com.example.demo.repository.userRepository;
import com.example.demo.service.CartService;

@RestController
@RequestMapping("/cart")
public class CartController {

    private final CartService cartService;
    private final userRepository userRepository;

    public CartController(CartService cartService, userRepository userRepository) {
        this.cartService = cartService;
        this.userRepository = userRepository;
    }

    @GetMapping
    public CartSummaryResponse getCart(Authentication authentication) {
        return cartService.getCartSummary(resolveUser(authentication));
    }

    @PostMapping("/items")
    public ResponseEntity<CartSummaryResponse> addToCart(
            Authentication authentication,
            @RequestBody CartItemRequest request) {
        return ResponseEntity.ok(cartService.addToCart(
                resolveUser(authentication),
                request.getProductId(),
                request.getQuantity()));
    }

    @PutMapping("/items/{productId}")
    public ResponseEntity<CartSummaryResponse> updateCartItem(
            Authentication authentication,
            @PathVariable Long productId,
            @RequestBody CartItemRequest request) {
        return ResponseEntity.ok(cartService.updateCartItem(
                resolveUser(authentication),
                productId,
                request.getQuantity()));
    }

    @DeleteMapping("/items/{productId}")
    public ResponseEntity<CartSummaryResponse> removeCartItem(
            Authentication authentication,
            @PathVariable Long productId) {
        return ResponseEntity.ok(cartService.removeCartItem(resolveUser(authentication), productId));
    }

    private userEntity resolveUser(Authentication authentication) {
        if (authentication == null || authentication.getPrincipal() == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Unauthorized user");
        }

        String email = authentication.getPrincipal().toString();
        userEntity user = userRepository.findByEmail(email);

        if (user == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found");
        }

        return user;
    }
}
