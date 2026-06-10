package com.example.demo.pages;

import java.util.List;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;

public class ProductListingPage extends BasePage {
    private static final String FRONTEND_URL = System.getProperty("frontend.url", "http://localhost:3000");

    public ProductListingPage(WebDriver driver) {
        super(driver);
    }

    /**
     * Navigate to the product catalog/listing page
     */
    public void navigateToCatalog() {
        driver.get(FRONTEND_URL);
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//h3[normalize-space()='Recommended products']")));
        pauseForDemo();
    }

    /**
     * Get all product cards currently visible on the page
     */
    public List<WebElement> getAllProductCards() {
        return driver.findElements(By.xpath("//article[@class='catalog-card']"));
    }

    /**
     * Get product name by index
     */
    public String getProductNameByIndex(int index) {
        List<WebElement> products = getAllProductCards();
        if (index >= products.size()) {
            throw new IllegalArgumentException("Product index " + index + " not found. Total products: " + products.size());
        }
        return products.get(index).findElement(By.xpath(".//h3")).getText();
    }

    /**
     * Get product price by index
     */
    public String getProductPriceByIndex(int index) {
        List<WebElement> products = getAllProductCards();
        if (index >= products.size()) {
            throw new IllegalArgumentException("Product index " + index + " not found.");
        }
        return products.get(index).findElement(By.xpath(".//strong[@class='catalog-price']")).getText();
    }

    /**
     * Get product category by index
     */
    public String getProductCategoryByIndex(int index) {
        List<WebElement> products = getAllProductCards();
        if (index >= products.size()) {
            throw new IllegalArgumentException("Product index " + index + " not found.");
        }
        return products.get(index).findElement(By.xpath(".//span[@class='catalog-category']")).getText();
    }

    /**
     * Get product description by index
     */
    public String getProductDescriptionByIndex(int index) {
        List<WebElement> products = getAllProductCards();
        if (index >= products.size()) {
            throw new IllegalArgumentException("Product index " + index + " not found.");
        }
        return products.get(index).findElement(By.xpath(".//p")).getText();
    }

    /**
     * Get stock status by index (In Stock / Sold Out)
     */
    public String getStockStatusByIndex(int index) {
        List<WebElement> products = getAllProductCards();
        if (index >= products.size()) {
            throw new IllegalArgumentException("Product index " + index + " not found.");
        }
        return products.get(index).findElement(By.xpath(".//span[contains(@class, 'stock-badge')]")).getText();
    }

    /**
     * Check if product is in stock by index
     */
    public boolean isProductInStock(int index) {
        return getStockStatusByIndex(index).equals("In Stock");
    }

    /**
     * Click View Details button for a product by index
     */
    public ProductDetailPage clickViewDetailsForProduct(int index) {
        List<WebElement> products = getAllProductCards();
        if (index >= products.size()) {
            throw new IllegalArgumentException("Product index " + index + " not found.");
        }
        products.get(index).findElement(By.xpath(".//button[contains(@class, 'catalog-action')]")).click();
        pauseForDemo();
        return new ProductDetailPage(driver);
    }

    /**
     * Get total number of products displayed
     */
    public int getTotalProductsCount() {
        return getAllProductCards().size();
    }

    /**
     * Check if product is displayed by name
     */
    public boolean isProductDisplayedByName(String productName) {
        List<WebElement> products = getAllProductCards();
        return products.stream()
                .anyMatch(product -> product.getText().contains(productName));
    }

    /**
     * Search for a product by keyword
     */
    public void searchProduct(String keyword) {
        WebElement searchInput = driver.findElement(By.xpath("//input[@placeholder='Search by product name or category']"));
        searchInput.clear();
        searchInput.sendKeys(keyword);
        pauseForDemo();
    }

    /**
     * Filter products by category
     */
    public void filterByCategory(String categoryName) {
        WebElement categoryFilter = driver.findElement(
                By.xpath("//label[contains(text(), '" + categoryName + "')]//input"));
        categoryFilter.click();
        wait.until(ExpectedConditions.stalenessOf(getAllProductCards().get(0)));
        pauseForDemo();
    }

    /**
     * Get "Results" count from toolbar
     */
    public int getResultsCount() {
        String text = driver.findElement(By.xpath("//span[contains(text(), 'results')]")).getText();
        return Integer.parseInt(text.split(" ")[0]);
    }

    /**
     * Get cart count from toolbar
     */
    public int getCartCount() {
        String text = driver.findElement(By.xpath("//span[contains(text(), 'in cart')]")).getText();
        return Integer.parseInt(text.split(" ")[0]);
    }

    /**
     * Check if "Loading catalog..." message is visible
     */
    public boolean isLoadingCatalog() {
        List<WebElement> elements = driver.findElements(By.xpath("//p[contains(@class, 'empty-state')][contains(text(), 'Loading')]"));
        return !elements.isEmpty();
    }

    /**
     * Check if "No products" message is visible
     */
    public boolean isNoProductsMessageDisplayed() {
        List<WebElement> elements = driver.findElements(
                By.xpath("//p[contains(@class, 'empty-state')][contains(text(), 'No products')]"));
        return !elements.isEmpty();
    }

    /**
     * Get all product names
     */
    public List<String> getAllProductNames() {
        return getAllProductCards().stream()
                .map(product -> product.findElement(By.xpath(".//h3")).getText())
                .toList();
    }

    /**
     * Get all product prices
     */
    public List<String> getAllProductPrices() {
        return getAllProductCards().stream()
                .map(product -> product.findElement(By.xpath(".//strong[@class='catalog-price']")).getText())
                .toList();
    }

    /**
     * Get all product categories
     */
    public List<String> getAllProductCategories() {
        return getAllProductCards().stream()
                .map(product -> product.findElement(By.xpath(".//span[@class='catalog-category']")).getText())
                .toList();
    }

    /**
     * Check if "Top Pick" badge is visible for a product by index
     */
    public boolean hasTopPickBadge(int index) {
        List<WebElement> products = getAllProductCards();
        if (index >= products.size()) {
            return false;
        }
        List<WebElement> badges = products.get(index).findElements(By.xpath(".//span[contains(@class, 'offer-badge')]"));
        return !badges.isEmpty();
    }
}
