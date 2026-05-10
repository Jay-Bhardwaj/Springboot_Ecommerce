package com.example.demo.dto;

import java.math.BigDecimal;

public class CheckoutItemResponse {

    private final Long productId;
    private final String productName;
    private final String category;
    private final String imageUrl;
    private final BigDecimal unitPrice;
    private final Integer quantity;
    private final BigDecimal lineTotal;

    public CheckoutItemResponse(
            Long productId,
            String productName,
            String category,
            String imageUrl,
            BigDecimal unitPrice,
            Integer quantity,
            BigDecimal lineTotal) {
        this.productId = productId;
        this.productName = productName;
        this.category = category;
        this.imageUrl = imageUrl;
        this.unitPrice = unitPrice;
        this.quantity = quantity;
        this.lineTotal = lineTotal;
    }

    public Long getProductId() {
        return productId;
    }

    public String getProductName() {
        return productName;
    }

    public String getCategory() {
        return category;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public BigDecimal getUnitPrice() {
        return unitPrice;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public BigDecimal getLineTotal() {
        return lineTotal;
    }
}
