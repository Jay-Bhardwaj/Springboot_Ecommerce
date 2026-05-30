package com.example.demo.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.example.demo.dto.CartSummaryResponse;
import com.example.demo.dto.CheckoutItemResponse;
import com.example.demo.dto.CheckoutRequest;
import com.example.demo.dto.CheckoutResponse;
import com.example.demo.entity.CartItem;
import com.example.demo.entity.Order;
import com.example.demo.entity.OrderItem;
import com.example.demo.entity.OrderStatus;
import com.example.demo.entity.PaymentMethod;
import com.example.demo.entity.PaymentStatus;
import com.example.demo.entity.Product;
import com.example.demo.entity.userEntity;
import com.example.demo.dto.OrderHistoryResponse;
import com.example.demo.repository.CartItemRepository;
import com.example.demo.repository.OrderRepository;
import com.example.demo.repository.ProductRepository;

@Service
public class OrderService {

    private static final int DELIVERY_ESTIMATE_DAYS = 4;

    private final CartItemRepository cartItemRepository;
    private final CartService cartService;
    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;

    public OrderService(
            CartItemRepository cartItemRepository,
            CartService cartService,
            OrderRepository orderRepository,
            ProductRepository productRepository) {
        this.cartItemRepository = cartItemRepository;
        this.cartService = cartService;
        this.orderRepository = orderRepository;
        this.productRepository = productRepository;
    }

    @Transactional
    public CheckoutResponse placeOrder(userEntity user, CheckoutRequest request) {
        CartSummaryResponse cartSummary = cartService.getCartSummary(user);

        if (cartSummary.getItems().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Cart is empty");
        }

        validateCheckoutRequest(request);

        List<CartItem> cartItems = cartItemRepository.findByUserOrderByIdAsc(user);
        LocalDateTime placedAt = LocalDateTime.now();
        LocalDate estimatedDeliveryDate = placedAt.toLocalDate().plusDays(DELIVERY_ESTIMATE_DAYS);

        Order order = new Order();
        order.setOrderNumber(generateOrderNumber(placedAt));
        order.setUser(user);
        order.setCustomerName(requireText(request.getCustomerName(), "Customer name is required"));
        order.setCustomerEmail(user.getEmail());
        order.setPhoneNumber(requireText(request.getPhoneNumber(), "Phone number is required"));
        order.setAddressLine1(requireText(request.getAddressLine1(), "Address line 1 is required"));
        order.setAddressLine2(trimToNull(request.getAddressLine2()));
        order.setCity(requireText(request.getCity(), "City is required"));
        order.setState(requireText(request.getState(), "State is required"));
        order.setPostalCode(requireText(request.getPostalCode(), "Postal code is required"));
        order.setSubtotal(cartSummary.getSubtotal());
        order.setGstRate(cartSummary.getGstRate());
        order.setGstAmount(cartSummary.getGstAmount());
        order.setDeliveryCharge(cartSummary.getDeliveryCharge());
        order.setTotalAmount(cartSummary.getTotalBill());
        order.setTotalItems(cartSummary.getItemCount());
        order.setOrderStatus(OrderStatus.PLACED);
        order.setPaymentMethod(resolvePaymentMethod(request.getPaymentMethod()));
        order.setPaymentStatus(order.getPaymentMethod() == PaymentMethod.ONLINE ? PaymentStatus.PAID : PaymentStatus.PENDING);
        order.setPlacedAt(placedAt);
        order.setEstimatedDeliveryDate(estimatedDeliveryDate);

        for (CartItem cartItem : cartItems) {
            Product product = productRepository.findById(cartItem.getProduct().getId())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Product not found"));

            int availableStock = product.getStockQuantity() == null ? 0 : product.getStockQuantity();
            int requestedQuantity = cartItem.getQuantity() == null ? 0 : cartItem.getQuantity();

            if (availableStock < requestedQuantity) {
                throw new ResponseStatusException(
                        HttpStatus.BAD_REQUEST,
                        "Insufficient stock for " + product.getName());
            }

            product.setStockQuantity(availableStock - requestedQuantity);
            productRepository.save(product);

            OrderItem orderItem = new OrderItem();
            orderItem.setProductId(product.getId());
            orderItem.setProductName(product.getName());
            orderItem.setCategory(product.getCategory());
            orderItem.setImageUrl(product.getImageUrl());
            orderItem.setUnitPrice(product.getPrice());
            orderItem.setQuantity(requestedQuantity);
            orderItem.setLineTotal(product.getPrice().multiply(java.math.BigDecimal.valueOf(requestedQuantity)));
            order.addItem(orderItem);
        }

        Order savedOrder = orderRepository.save(order);
        cartItemRepository.deleteByUser(user);

        List<CheckoutItemResponse> items = savedOrder.getItems().stream()
                .map(item -> new CheckoutItemResponse(
                        item.getProductId(),
                        item.getProductName(),
                        item.getCategory(),
                        item.getImageUrl(),
                        item.getUnitPrice(),
                        item.getQuantity(),
                        item.getLineTotal()))
                .toList();

        return new CheckoutResponse(
                savedOrder.getId(),
                savedOrder.getOrderNumber(),
                savedOrder.getCustomerName(),
                savedOrder.getCustomerEmail(),
                savedOrder.getPhoneNumber(),
                savedOrder.getAddressLine1(),
                savedOrder.getAddressLine2(),
                savedOrder.getCity(),
                savedOrder.getState(),
                savedOrder.getPostalCode(),
                deriveOrderStatus(savedOrder).name(),
                resolvePaymentMethod(savedOrder).name(),
                resolvePaymentStatus(savedOrder).name(),
                savedOrder.getTotalItems(),
                savedOrder.getSubtotal(),
                savedOrder.getGstRate(),
                savedOrder.getGstAmount(),
                savedOrder.getDeliveryCharge(),
                savedOrder.getTotalAmount(),
                savedOrder.getPlacedAt(),
                savedOrder.getEstimatedDeliveryDate(),
                items);
    }

    @Transactional(readOnly = true)
    public List<OrderHistoryResponse> getOrdersForUser(userEntity user) {
        return orderRepository.findByUserOrderByPlacedAtDesc(user).stream()
                .map(order -> new OrderHistoryResponse(
                        order.getId(),
                        order.getOrderNumber(),
                        deriveOrderStatus(order).name(),
                        resolvePaymentMethod(order).name(),
                        resolvePaymentStatus(order).name(),
                        order.getTotalItems(),
                        order.getTotalAmount(),
                        order.getPlacedAt(),
                        order.getEstimatedDeliveryDate(),
                        order.getAddressLine1(),
                        order.getAddressLine2(),
                        order.getCity(),
                        order.getState(),
                        order.getPostalCode(),
                        order.getItems().stream()
                                .map(item -> new CheckoutItemResponse(
                                        item.getProductId(),
                                        item.getProductName(),
                                        item.getCategory(),
                                        item.getImageUrl(),
                                        item.getUnitPrice(),
                                        item.getQuantity(),
                                        item.getLineTotal()))
                                .toList()))
                .toList();
    }

    private void validateCheckoutRequest(CheckoutRequest request) {
        requireText(request.getCustomerName(), "Customer name is required");
        requireText(request.getPhoneNumber(), "Phone number is required");
        requireText(request.getAddressLine1(), "Address line 1 is required");
        requireText(request.getCity(), "City is required");
        requireText(request.getState(), "State is required");
        requireText(request.getPostalCode(), "Postal code is required");
    }

    private String requireText(String value, String message) {
        String trimmed = trimToNull(value);
        if (trimmed == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, message);
        }
        return trimmed;
    }

    private String trimToNull(String value) {
        if (value == null) {
            return null;
        }

        String trimmed = value.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }

    private String generateOrderNumber(LocalDateTime placedAt) {
        String prefix = placedAt.format(DateTimeFormatter.ofPattern("yyyyMMdd", Locale.ENGLISH));
        String suffix = UUID.randomUUID().toString().replace("-", "").substring(0, 8).toUpperCase(Locale.ENGLISH);
        return "ORD-" + prefix + "-" + suffix;
    }

    private PaymentMethod resolvePaymentMethod(String paymentMethod) {
        if (paymentMethod == null || paymentMethod.isBlank()) {
            return PaymentMethod.COD;
        }

        try {
            return PaymentMethod.valueOf(paymentMethod.trim().toUpperCase(Locale.ENGLISH));
        } catch (IllegalArgumentException exception) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Unsupported payment method");
        }
    }

    private OrderStatus deriveOrderStatus(Order order) {
        if (order.getOrderStatus() == OrderStatus.DELIVERED) {
            return OrderStatus.DELIVERED;
        }

        LocalDate today = LocalDate.now();
        LocalDate estimatedDate = order.getEstimatedDeliveryDate();

        if (!today.isBefore(estimatedDate)) {
            return OrderStatus.DELIVERED;
        }

        if (!today.isBefore(estimatedDate.minusDays(1))) {
            return OrderStatus.IN_TRANSIT;
        }

        if (!today.isBefore(order.getPlacedAt().toLocalDate().plusDays(1))) {
            return OrderStatus.SHIPPED;
        }

        return OrderStatus.PLACED;
    }

    private PaymentMethod resolvePaymentMethod(Order order) {
        return order.getPaymentMethod() == null ? PaymentMethod.COD : order.getPaymentMethod();
    }

    private PaymentStatus resolvePaymentStatus(Order order) {
        return order.getPaymentStatus() == null
                ? (resolvePaymentMethod(order) == PaymentMethod.ONLINE ? PaymentStatus.PAID : PaymentStatus.PENDING)
                : order.getPaymentStatus();
    }
}
