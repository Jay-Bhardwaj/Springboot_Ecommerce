package com.ecommerce.config;

import com.ecommerce.entity.Product;
import com.ecommerce.repository.ProductRepository;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;

@Component
@Order(1)
public class ProductSeeder implements ApplicationRunner {
    private final ProductRepository productRepository;

    public ProductSeeder(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    @Override
    public void run(ApplicationArguments args) {
        if (productRepository.count() > 0) {
            return;
        }

        productRepository.saveAll(List.of(
                product("Noise Cancelling Headphones", "Comfortable wireless headphones with rich sound and all-day battery.", "3499", 25, "Electronics", "https://images.unsplash.com/photo-1505740420928-5e560c06d30e?auto=format&fit=crop&w=900&q=80"),
                product("Classic Linen Shirt", "A breathable, relaxed-fit linen shirt for everyday wear.", "1799", 40, "Fashion", "https://images.unsplash.com/photo-1603252109303-2751441dd157?auto=format&fit=crop&w=900&q=80"),
                product("Ceramic Pour-Over Set", "Minimal ceramic coffee dripper and mug set for a better morning ritual.", "1299", 18, "Home & Kitchen", "https://images.unsplash.com/photo-1495474472287-4d71bcdd2085?auto=format&fit=crop&w=900&q=80"),
                product("Everyday Carry Backpack", "Water-resistant backpack with a padded laptop compartment and organized pockets.", "2499", 30, "Accessories", "https://images.unsplash.com/photo-1553062407-98eeb64c6a62?auto=format&fit=crop&w=900&q=80"),
                product("Smart Fitness Watch", "Track workouts, sleep and notifications with a bright, durable display.", "4999", 16, "Electronics", "https://images.unsplash.com/photo-1523275335684-37898b6baf30?auto=format&fit=crop&w=900&q=80"),
                product("Scented Soy Candle", "Hand-poured soy wax candle with warm amber and sandalwood notes.", "699", 45, "Home & Kitchen", "https://images.unsplash.com/photo-1603006905003-be475563bc59?auto=format&fit=crop&w=900&q=80"),
                product("Urban Trail Sneakers", "Lightweight everyday sneakers with cushioned support and a clean silhouette.", "2899", 22, "Fashion", "https://images.unsplash.com/photo-1542291026-7eec264c27ff?auto=format&fit=crop&w=900&q=80"),
                product("Insulated Steel Bottle", "Keeps drinks cold for 24 hours and hot for 12 hours.", "899", 50, "Accessories", "https://images.unsplash.com/photo-1602143407151-7111542de6e8?auto=format&fit=crop&w=900&q=80")
        ));
    }

    private Product product(String name, String description, String price, int stock, String category, String imageUrl) {
        Product product = new Product();
        product.setName(name);
        product.setDescription(description);
        product.setPrice(new BigDecimal(price));
        product.setStock(stock);
        product.setCategory(category);
        product.setImageUrl(imageUrl);
        product.setActive(true);
        return product;
    }
}
