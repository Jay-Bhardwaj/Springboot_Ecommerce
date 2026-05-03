package com.example.demo.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.example.demo.dto.CartItemResponse;
import com.example.demo.dto.CartSummaryResponse;
import com.example.demo.entity.CartItem;
import com.example.demo.entity.Product;
import com.example.demo.entity.userEntity;
import com.example.demo.repository.CartItemRepository;
import com.example.demo.repository.ProductRepository;

@Service
public class CartService {

    private static final BigDecimal GST_RATE = new BigDecimal("0.18");
    private static final BigDecimal DELIVERY_CHARGE = new BigDecimal("40.00");
    private static final BigDecimal FREE_DELIVERY_THRESHOLD = new BigDecimal("500.00");

    private final CartItemRepository cartItemRepository;
    private final ProductRepository productRepository;

    public CartService(CartItemRepository cartItemRepository, ProductRepository productRepository) {
        this.cartItemRepository = cartItemRepository;
        this.productRepository = productRepository;
    }

    public CartSummaryResponse getCartSummary(userEntity user) {
        List<CartItemResponse> items = cartItemRepository.findByUserOrderByIdAsc(user).stream()
                .map(this::toCartItemResponse)
                .toList();

        return buildSummary(items);
    }

    public CartSummaryResponse addToCart(userEntity user, Long productId, Integer quantity) {
        int normalizedQuantity = normalizeQuantity(quantity);
        Product product = loadProduct(productId);

        validateStock(product, normalizedQuantity);

        CartItem cartItem = cartItemRepository.findByUserAndProductId(user, productId)
                .orElseGet(() -> {
                    CartItem newItem = new CartItem();
                    newItem.setUser(user);
                    newItem.setProduct(product);
                    newItem.setQuantity(0);
                    return newItem;
                });

        int nextQuantity = cartItem.getQuantity() + normalizedQuantity;
        validateStock(product, nextQuantity);
        cartItem.setQuantity(nextQuantity);
        cartItemRepository.save(cartItem);

        return getCartSummary(user);
    }

    public CartSummaryResponse updateCartItem(userEntity user, Long productId, Integer quantity) {
        int normalizedQuantity = quantity == null ? 0 : quantity;
        CartItem cartItem = cartItemRepository.findByUserAndProductId(user, productId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Cart item not found"));

        if (normalizedQuantity <= 0) {
            cartItemRepository.delete(cartItem);
            return getCartSummary(user);
        }

        validateStock(cartItem.getProduct(), normalizedQuantity);
        cartItem.setQuantity(normalizedQuantity);
        cartItemRepository.save(cartItem);

        return getCartSummary(user);
    }

    public CartSummaryResponse removeCartItem(userEntity user, Long productId) {
        cartItemRepository.deleteByUserAndProductId(user, productId);
        return getCartSummary(user);
    }

    private Product loadProduct(Long productId) {
        return productRepository.findById(productId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Product not found"));
    }

    private int normalizeQuantity(Integer quantity) {
        if (quantity == null || quantity <= 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Quantity must be at least 1");
        }

        return quantity;
    }

    private void validateStock(Product product, int quantity) {
        int availableStock = product.getStockQuantity() == null ? 0 : product.getStockQuantity();

        if (availableStock <= 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Product is out of stock");
        }

        if (quantity > availableStock) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Requested quantity exceeds stock");
        }
    }

    private CartItemResponse toCartItemResponse(CartItem cartItem) {
        Product product = cartItem.getProduct();
        BigDecimal unitPrice = safeMoney(product.getPrice());
        BigDecimal lineTotal = unitPrice.multiply(BigDecimal.valueOf(cartItem.getQuantity()))
                .setScale(2, RoundingMode.HALF_UP);

        return new CartItemResponse(
                product.getId(),
                product.getName(),
                product.getCategory(),
                product.getImageUrl(),
                unitPrice,
                cartItem.getQuantity(),
                product.getStockQuantity(),
                lineTotal);
    }

    private CartSummaryResponse buildSummary(List<CartItemResponse> items) {
        int itemCount = items.stream()
                .mapToInt(CartItemResponse::getQuantity)
                .sum();

        BigDecimal subtotal = items.stream()
                .map(CartItemResponse::getLineTotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add)
                .setScale(2, RoundingMode.HALF_UP);

        BigDecimal gstAmount = subtotal.multiply(GST_RATE)
                .setScale(2, RoundingMode.HALF_UP);

        BigDecimal deliveryCharge = subtotal.compareTo(BigDecimal.ZERO) > 0
                && subtotal.compareTo(FREE_DELIVERY_THRESHOLD) < 0
                        ? DELIVERY_CHARGE
                        : BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP);

        BigDecimal totalBill = subtotal.add(gstAmount).add(deliveryCharge)
                .setScale(2, RoundingMode.HALF_UP);

        return new CartSummaryResponse(
                items,
                itemCount,
                subtotal,
                GST_RATE,
                gstAmount,
                deliveryCharge,
                FREE_DELIVERY_THRESHOLD,
                totalBill);
    }

    private BigDecimal safeMoney(BigDecimal value) {
        return value == null ? BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP) : value.setScale(2, RoundingMode.HALF_UP);
    }
}
