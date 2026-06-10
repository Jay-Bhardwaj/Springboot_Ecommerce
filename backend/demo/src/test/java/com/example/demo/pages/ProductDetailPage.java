package com.example.demo.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;

public class ProductDetailPage extends BasePage {

    public ProductDetailPage(WebDriver driver) {
        super(driver);
    }

    /**
     * Get product name from detail page
     */
    public String getProductName() {
        return driver.findElement(By.xpath("//div[@class='product-info-section']//h1")).getText();
    }

    /**
     * Get product price from detail page
     */
    public String getProductPrice() {
        return driver.findElement(By.xpath("//strong[contains(text(), 'Rs.')][1]")).getText();
    }

    /**
     * Get product category from detail page
     */
    public String getProductCategory() {
        return driver.findElement(By.xpath("//span[@class='category-badge']")).getText();
    }

    /**
     * Get product description from detail page
     */
    public String getProductDescription() {
        return driver.findElement(By.xpath("//div[@class='description-section']//p")).getText();
    }

    /**
     * Get stock quantity from detail page
     */
    public String getStockQuantity() {
        return driver.findElement(By.xpath("//span[contains(text(), 'units available')]")).getText();
    }

    /**
     * Check if Add to Cart button is visible
     */
    public boolean isAddToCartButtonVisible() {
        return !driver.findElements(By.xpath("//button[contains(@class, 'add-to-cart-btn')]")).isEmpty();
    }

    /**
     * Click Add to Cart button
     */
    public void clickAddToCart() {
        WebElement addToCartButton = driver.findElement(By.xpath("//button[contains(@class, 'add-to-cart-btn')]"));
        addToCartButton.click();
        pauseForDemo();
    }

    /**
     * Set quantity for product
     */
    public void setQuantity(int quantity) {
        WebElement quantityInput = driver.findElement(By.xpath("//input[@class='qty-input']"));
        quantityInput.clear();
        quantityInput.sendKeys(String.valueOf(quantity));
        pauseForDemo();
    }

    /**
     * Get current quantity value
     */
    public int getQuantity() {
        WebElement quantityInput = driver.findElement(By.xpath("//input[@class='qty-input']"));
        String value = quantityInput.getAttribute("value");
        return Integer.parseInt(value);
    }

    /**
     * Increment quantity button
     */
    public void incrementQuantity() {
        WebElement incrementBtn = driver.findElement(By.xpath("//button[@class='qty-btn'][contains(text(), '+')]"));
        incrementBtn.click();
        pauseForDemo();
    }

    /**
     * Decrement quantity button
     */
    public void decrementQuantity() {
        WebElement decrementBtn = driver.findElement(By.xpath("//button[@class='qty-btn'][contains(text(), '−')]"));
        decrementBtn.click();
        pauseForDemo();
    }

    /**
     * Check if product is in stock
     */
    public boolean isInStock() {
        return !driver.findElements(By.xpath("//span[@class='badge in-stock']")).isEmpty();
    }

    /**
     * Check if product is out of stock
     */
    public boolean isOutOfStock() {
        return !driver.findElements(By.xpath("//span[@class='badge out-of-stock']")).isEmpty();
    }

    /**
     * Go back to product listing
     */
    public ProductListingPage clickBackButton() {
        WebElement backButton = driver.findElement(By.xpath("//button[contains(@class, 'back-button-detail')]"));
        backButton.click();
        pauseForDemo();
        return new ProductListingPage(driver);
    }

    /**
     * Get product rating
     */
    public String getRating() {
        return driver.findElement(By.xpath("//span[@class='rating-text']")).getText();
    }

    /**
     * Check if product has reviews
     */
    public boolean hasReviews() {
        return !driver.findElements(By.xpath("//div[@class='review-card']")).isEmpty();
    }

    /**
     * Get number of reviews
     */
    public int getReviewsCount() {
        java.util.List<org.openqa.selenium.WebElement> reviews = driver.findElements(By.xpath("//div[@class='review-card']"));
        return reviews.size();
    }
}
