package com.example.demo.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public class OrderHistoryResponse {

    private final Long orderId;
    private final String orderNumber;
    private final String orderStatus;
    private final String paymentMethod;
    private final String paymentStatus;
    private final Integer totalItems;
    private final BigDecimal totalAmount;
    private final LocalDateTime placedAt;
    private final LocalDate estimatedDeliveryDate;
    private final String addressLine1;
    private final String addressLine2;
    private final String city;
    private final String state;
    private final String postalCode;
    private final List<CheckoutItemResponse> items;

    public OrderHistoryResponse(
            Long orderId,
            String orderNumber,
            String orderStatus,
            String paymentMethod,
            String paymentStatus,
            Integer totalItems,
            BigDecimal totalAmount,
            LocalDateTime placedAt,
            LocalDate estimatedDeliveryDate,
            String addressLine1,
            String addressLine2,
            String city,
            String state,
            String postalCode,
            List<CheckoutItemResponse> items) {
        this.orderId = orderId;
        this.orderNumber = orderNumber;
        this.orderStatus = orderStatus;
        this.paymentMethod = paymentMethod;
        this.paymentStatus = paymentStatus;
        this.totalItems = totalItems;
        this.totalAmount = totalAmount;
        this.placedAt = placedAt;
        this.estimatedDeliveryDate = estimatedDeliveryDate;
        this.addressLine1 = addressLine1;
        this.addressLine2 = addressLine2;
        this.city = city;
        this.state = state;
        this.postalCode = postalCode;
        this.items = items;
    }

    public Long getOrderId() {
        return orderId;
    }

    public String getOrderNumber() {
        return orderNumber;
    }

    public String getOrderStatus() {
        return orderStatus;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public String getPaymentStatus() {
        return paymentStatus;
    }

    public Integer getTotalItems() {
        return totalItems;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public LocalDateTime getPlacedAt() {
        return placedAt;
    }

    public LocalDate getEstimatedDeliveryDate() {
        return estimatedDeliveryDate;
    }

    public String getAddressLine1() {
        return addressLine1;
    }

    public String getAddressLine2() {
        return addressLine2;
    }

    public String getCity() {
        return city;
    }

    public String getState() {
        return state;
    }

    public String getPostalCode() {
        return postalCode;
    }

    public List<CheckoutItemResponse> getItems() {
        return items;
    }
}
