package com.example.demo.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public class CheckoutResponse {

    private final Long orderId;
    private final String orderNumber;
    private final String customerName;
    private final String customerEmail;
    private final String phoneNumber;
    private final String addressLine1;
    private final String addressLine2;
    private final String city;
    private final String state;
    private final String postalCode;
    private final Integer totalItems;
    private final BigDecimal subtotal;
    private final BigDecimal gstRate;
    private final BigDecimal gstAmount;
    private final BigDecimal deliveryCharge;
    private final BigDecimal totalAmount;
    private final LocalDateTime placedAt;
    private final LocalDate estimatedDeliveryDate;
    private final List<CheckoutItemResponse> items;

    public CheckoutResponse(
            Long orderId,
            String orderNumber,
            String customerName,
            String customerEmail,
            String phoneNumber,
            String addressLine1,
            String addressLine2,
            String city,
            String state,
            String postalCode,
            Integer totalItems,
            BigDecimal subtotal,
            BigDecimal gstRate,
            BigDecimal gstAmount,
            BigDecimal deliveryCharge,
            BigDecimal totalAmount,
            LocalDateTime placedAt,
            LocalDate estimatedDeliveryDate,
            List<CheckoutItemResponse> items) {
        this.orderId = orderId;
        this.orderNumber = orderNumber;
        this.customerName = customerName;
        this.customerEmail = customerEmail;
        this.phoneNumber = phoneNumber;
        this.addressLine1 = addressLine1;
        this.addressLine2 = addressLine2;
        this.city = city;
        this.state = state;
        this.postalCode = postalCode;
        this.totalItems = totalItems;
        this.subtotal = subtotal;
        this.gstRate = gstRate;
        this.gstAmount = gstAmount;
        this.deliveryCharge = deliveryCharge;
        this.totalAmount = totalAmount;
        this.placedAt = placedAt;
        this.estimatedDeliveryDate = estimatedDeliveryDate;
        this.items = items;
    }

    public Long getOrderId() {
        return orderId;
    }

    public String getOrderNumber() {
        return orderNumber;
    }

    public String getCustomerName() {
        return customerName;
    }

    public String getCustomerEmail() {
        return customerEmail;
    }

    public String getPhoneNumber() {
        return phoneNumber;
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

    public Integer getTotalItems() {
        return totalItems;
    }

    public BigDecimal getSubtotal() {
        return subtotal;
    }

    public BigDecimal getGstRate() {
        return gstRate;
    }

    public BigDecimal getGstAmount() {
        return gstAmount;
    }

    public BigDecimal getDeliveryCharge() {
        return deliveryCharge;
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

    public List<CheckoutItemResponse> getItems() {
        return items;
    }
}
