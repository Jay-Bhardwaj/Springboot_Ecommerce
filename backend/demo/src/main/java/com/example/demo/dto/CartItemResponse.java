package com.example.demo.dto;

import java.math.BigDecimal;

public class CartItemResponse {

    private final Long productId;
    private final String name;
    private final String category;
    private final String imageUrl;
    private final BigDecimal unitPrice;
    private final Integer quantity;
    private final Integer availableStock;
    private final BigDecimal lineTotal;

    public CartItemResponse(
            Long productId,
            String name,
            String category,
            String imageUrl,
            BigDecimal unitPrice,
            Integer quantity,
            Integer availableStock,
            BigDecimal lineTotal) {
        this.productId = productId;
        this.name = name;
        this.category = category;
        this.imageUrl = imageUrl;
        this.unitPrice = unitPrice;
        this.quantity = quantity;
        this.availableStock = availableStock;
        this.lineTotal = lineTotal;
    }

    public Long getProductId() {
        return productId;
    }

    public String getName() {
        return name;
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

    public Integer getAvailableStock() {
        return availableStock;
    }

    public BigDecimal getLineTotal() {
        return lineTotal;
    }
}
