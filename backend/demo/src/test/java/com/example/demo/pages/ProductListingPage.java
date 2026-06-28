package com.example.demo.pages;

import java.util.List;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.ExpectedConditions;

public class ProductListingPage extends BasePage {
    private static final String FRONTEND_URL = System.getProperty("frontend.url", "http://localhost:3000");
    private static final By PRODUCT_CARD_LOCATOR = By.cssSelector("article.catalog-card");
    private static final By EMPTY_STATE_LOCATOR = By.cssSelector("p.empty-state");
    private static final By RESULTS_LOCATOR = By.xpath("//span[contains(normalize-space(.), 'results')]");
    private static final By CART_LOCATOR = By.xpath("//span[contains(normalize-space(.), 'in cart')]");
    private static final By SEARCH_INPUT_LOCATOR = By.name("search");
    private static final By CATEGORY_SELECT_LOCATOR = By.name("category");
    private static final By CLEAR_FILTERS_BUTTON_LOCATOR = By.xpath("//button[normalize-space()='Clear Filters']");

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
     * Wait until the catalog has finished loading.
     */
    private void waitForCatalogReady() {
        wait.until(driver -> {
            if (!driver.findElements(PRODUCT_CARD_LOCATOR).isEmpty()) {
                return true;
            }
            return !driver.findElements(EMPTY_STATE_LOCATOR).isEmpty();
        });
    }

    /**
     * Get all product cards currently visible on the page
     */
    public List<WebElement> getAllProductCards() {
        waitForCatalogReady();
        return driver.findElements(PRODUCT_CARD_LOCATOR);
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

        String stock = getStockStatusByIndex(index);

        return stock.trim().equalsIgnoreCase("IN STOCK");
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
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//main//h1")));
        pauseForDemo();
        return new ProductDetailPage(driver);
    }

    /**
     * Click View Details for the product that matches the given name.
     */
    public ProductDetailPage clickViewDetailsForProduct(String productName) {
        List<WebElement> products = getAllProductCards();
        for (WebElement product : products) {
            if (product.getText().contains(productName)) {
                product.findElement(By.xpath(".//button[contains(@class, 'catalog-action')]")).click();
                wait.until(ExpectedConditions.visibilityOfElementLocated(
                        By.xpath("//h1[normalize-space()='" + productName + "']")));
                pauseForDemo();
                return new ProductDetailPage(driver);
            }
        }
        throw new IllegalArgumentException("Product not found in catalog: " + productName);
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
        WebElement searchInput = wait.until(ExpectedConditions.visibilityOfElementLocated(SEARCH_INPUT_LOCATOR));
        searchInput.clear();
        searchInput.sendKeys(keyword);
        String normalizedKeyword = keyword.trim().toLowerCase();
        wait.until(driver -> {
            List<WebElement> products = driver.findElements(PRODUCT_CARD_LOCATOR);
            if (products.isEmpty()) {
                return !driver.findElements(EMPTY_STATE_LOCATOR).isEmpty();
            }
            return products.stream()
                    .allMatch(product -> product.getText().toLowerCase().contains(normalizedKeyword));
        });
        pauseForDemo();
    }

    /**
     * Filter products by category
     */
    public void filterByCategory(String categoryName) {
        WebElement categorySelect = wait.until(ExpectedConditions.visibilityOfElementLocated(CATEGORY_SELECT_LOCATOR));
        Select select = new Select(categorySelect);
        String normalizedCategory = categoryName.trim().toLowerCase();
        for (WebElement option : select.getOptions()) {
            String optionText = option.getText().trim();
            if (optionText.toLowerCase().equals(normalizedCategory)) {
                select.selectByVisibleText(optionText);
                break;
            }
        }
        if (!select.getFirstSelectedOption().getText().trim().equalsIgnoreCase(categoryName.trim())) {
            throw new IllegalArgumentException("Category not found in filter options: " + categoryName);
        }
        wait.until(driver -> {
            List<WebElement> products = driver.findElements(PRODUCT_CARD_LOCATOR);
            if (products.isEmpty()) {
                return !driver.findElements(EMPTY_STATE_LOCATOR).isEmpty();
            }
            return products.stream()
                    .allMatch(product -> product.getText().toLowerCase().contains(normalizedCategory));
        });
        pauseForDemo();
    }

    /**
     * Clear all active customer filters
     */
    public void clearCustomerFilters() {
        List<WebElement> buttons = driver.findElements(CLEAR_FILTERS_BUTTON_LOCATOR);
        if (!buttons.isEmpty() && buttons.get(0).isEnabled()) {
            buttons.get(0).click();
            wait.until(driver -> !driver.findElements(PRODUCT_CARD_LOCATOR).isEmpty());
            pauseForDemo();
        }
    }

    /**
     * Get "Results" count from toolbar
     */
    public int getResultsCount() {
        wait.until(ExpectedConditions.visibilityOfElementLocated(RESULTS_LOCATOR));
        String text = driver.findElement(RESULTS_LOCATOR).getText();
        return Integer.parseInt(text.replaceAll("[^0-9]", ""));
    }

    /**
     * Get cart count from toolbar
     */
    public int getCartCount() {
        wait.until(ExpectedConditions.visibilityOfElementLocated(CART_LOCATOR));
        String text = driver.findElement(CART_LOCATOR).getText();
        return Integer.parseInt(text.replaceAll("[^0-9]", ""));
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
