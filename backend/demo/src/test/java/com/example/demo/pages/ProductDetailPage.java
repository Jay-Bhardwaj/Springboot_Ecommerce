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
     * Get product name from detail page.
     */
    public String getProductName() {
        return wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//main//h1"))).getText();
    }

    /**
     * Get product price from detail page.
     */
    public String getProductPrice() {
        return driver.findElement(By.xpath("//strong[contains(text(), 'Rs.')][1]")).getText();
    }

    /**
     * Get product category from detail page.
     */
    public String getProductCategory() {
        return driver.findElement(By.xpath("//span[@class='catalog-category']")).getText();
    }

    /**
     * Get product description from detail page.
     */
    public String getProductDescription() {
        return driver.findElement(By.xpath("//main//p[normalize-space()][1]")).getText();
    }

    /**
     * Get stock quantity from detail page.
     */
    public String getStockQuantity() {
        return driver.findElement(By.xpath("//*[contains(normalize-space(),'units available')]")).getText();
    }

    /**
     * Check if Add to Cart button is visible.
     */
    public boolean isAddToCartButtonVisible() {
        return !driver.findElements(By.xpath("//button[normalize-space()='Add to Cart']")).isEmpty();
    }

    /**
     * Click Add to Cart button.
     */
    public void clickAddToCart() {
        WebElement addToCartButton = wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("//button[normalize-space()='Add to Cart']")));
        addToCartButton.click();
        pauseForDemo();
    }

    /**
     * Set quantity for product.
     */
    public void setQuantity(int quantity) {
        WebElement quantityInput = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//main//input[@type='number']")));
        quantityInput.clear();
        quantityInput.sendKeys(String.valueOf(quantity));
        pauseForDemo();
    }

    /**
     * Get current quantity value.
     */
    public int getQuantity() {
        WebElement quantityInput = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//main//input[@type='number']")));
        String value = quantityInput.getAttribute("value");
        return Integer.parseInt(value);
    }

    /**
     * Increment quantity button.
     */
    public void incrementQuantity() {
        WebElement incrementBtn = wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("//main//button[normalize-space()='+']")));
        incrementBtn.click();
        pauseForDemo();
    }

    /**
     * Decrement quantity button.
     */
    public void decrementQuantity() {
        WebElement decrementBtn = wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("//main//button[normalize-space()='-']")));
        decrementBtn.click();
        pauseForDemo();
    }

    /**
     * Check if product is in stock.
     */
    public boolean isInStock() {
        return !driver.findElements(By.xpath("//*[contains(normalize-space(),'in stock') or contains(normalize-space(),'Ready to ship')]")).isEmpty();
    }

    /**
     * Check if product is out of stock.
     */
    public boolean isOutOfStock() {
        return !driver.findElements(By.xpath("//*[contains(normalize-space(),'Out of Stock') or contains(normalize-space(),'Currently unavailable')]")).isEmpty();
    }

    /**
     * Go back to product listing.
     */
    public ProductListingPage clickBackButton() {
        WebElement backButton = wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("//button[normalize-space()='Back to catalog']")));
        backButton.click();
        wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//h3[normalize-space()='Recommended products']")));
        pauseForDemo();
        return new ProductListingPage(driver);
    }

    /**
     * Get product rating.
     */
    public String getRating() {
        return driver.findElement(By.xpath("//span[@class='rating-text']")).getText();
    }

    /**
     * Check if product has reviews.
     */
    public boolean hasReviews() {
        return !driver.findElements(By.xpath("//div[@class='review-card']")).isEmpty();
    }

    /**
     * Get number of reviews.
     */
    public int getReviewsCount() {
        java.util.List<org.openqa.selenium.WebElement> reviews = driver.findElements(By.xpath("//div[@class='review-card']"));
        return reviews.size();
    }
}
