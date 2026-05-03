package com.example.demo.dto;

import java.math.BigDecimal;
import java.util.List;

public class CartSummaryResponse {

    private final List<CartItemResponse> items;
    private final Integer itemCount;
    private final BigDecimal subtotal;
    private final BigDecimal gstRate;
    private final BigDecimal gstAmount;
    private final BigDecimal deliveryCharge;
    private final BigDecimal freeDeliveryThreshold;
    private final BigDecimal totalBill;

    public CartSummaryResponse(
            List<CartItemResponse> items,
            Integer itemCount,
            BigDecimal subtotal,
            BigDecimal gstRate,
            BigDecimal gstAmount,
            BigDecimal deliveryCharge,
            BigDecimal freeDeliveryThreshold,
            BigDecimal totalBill) {
        this.items = items;
        this.itemCount = itemCount;
        this.subtotal = subtotal;
        this.gstRate = gstRate;
        this.gstAmount = gstAmount;
        this.deliveryCharge = deliveryCharge;
        this.freeDeliveryThreshold = freeDeliveryThreshold;
        this.totalBill = totalBill;
    }

    public List<CartItemResponse> getItems() {
        return items;
    }

    public Integer getItemCount() {
        return itemCount;
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

    public BigDecimal getFreeDeliveryThreshold() {
        return freeDeliveryThreshold;
    }

    public BigDecimal getTotalBill() {
        return totalBill;
    }
}
