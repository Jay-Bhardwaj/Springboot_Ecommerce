package com.example.demo.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.demo.entity.CartItem;
import com.example.demo.entity.userEntity;

public interface CartItemRepository extends JpaRepository<CartItem, Long> {

    List<CartItem> findByUserOrderByIdAsc(userEntity user);

    Optional<CartItem> findByUserAndProductId(userEntity user, Long productId);

    void deleteByUserAndProductId(userEntity user, Long productId);

    void deleteByProductId(Long productId);
}
