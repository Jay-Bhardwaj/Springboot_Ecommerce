package com.example.demo.pages;

import org.junit.jupiter.api.Assertions;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;

public class AdminDashboardPage extends BasePage {

    private static final By PRODUCT_LIST_TITLE = By.xpath("//h3[normalize-space()='Product List']");
    private static final By PRODUCT_FORM_TITLE = By.xpath("//h3[contains(normalize-space(),'product')]");

    public AdminDashboardPage(WebDriver driver) {
        super(driver);
    }

    /**
     * Wait until the admin workspace is visible.
     */
    public void assertDashboardVisible() {
        wait.until(ExpectedConditions.or(
                ExpectedConditions.visibilityOfElementLocated(PRODUCT_FORM_TITLE),
                ExpectedConditions.visibilityOfElementLocated(PRODUCT_LIST_TITLE),
                ExpectedConditions.textToBePresentInElementLocated(By.tagName("body"), "Admin Console")));
    }

    /**
     * Fill the product editor form.
     */
    public void fillProductForm(
            String name,
            String category,
            String price,
            String stockQuantity,
            String imageUrl,
            String description) {
        typeByLabel("Product Name", name);
        typeByLabel("Category", category);
        typeByLabel("Price", price);
        typeByLabel("Stock Quantity", stockQuantity);
        typeByLabel("Image URL", imageUrl);
        pauseForDemo();
        typeByLabel("Description", description);
    }

    /**
     * Create a new product.
     */
    public void createProduct(
            String name,
            String category,
            String price,
            String stockQuantity,
            String imageUrl,
            String description) {
        fillProductForm(name, category, price, stockQuantity, imageUrl, description);
        clickButton("Create Product");
    }

    /**
     * Save the current form while editing an existing product.
     */
    public void updateProduct() {
        clickButton("Update Product");
    }

    /**
     * Click the edit action for a product row identified by product name.
     */
    public void clickEditProduct(String productName) {
        wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("//tr[td[normalize-space()='" + productName + "']]//button[normalize-space()='Edit']")))
                .click();
    }

    /**
     * Click the delete action for a product row identified by product name.
     */
    public void clickDeleteProduct(String productName) {
        wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("//tr[td[normalize-space()='" + productName + "']]//button[normalize-space()='Delete']")))
                .click();
    }

    /**
     * Wait until a product row is visible.
     */
    public void waitForProductRow(String productName) {
        wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//tr[td[normalize-space()='" + productName + "']]")));
    }

    /**
     * Wait until a product row disappears from the table.
     */
    public void waitForProductToDisappear(String productName) {
        wait.until(driver -> driver.findElements(
                By.xpath("//tr[td[normalize-space()='" + productName + "']]")).isEmpty());
    }

    /**
     * Ensure the admin login screen is visible again after logout.
     */
    public void assertAdminLoginVisible() {
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//button[normalize-space()='Login as Admin']")));
        Assertions.assertTrue(driver.getPageSource().contains("Admin access"));
    }

    /**
     * Log out from the admin workspace.
     */
    public void logout() {
        clickButton("Logout");
    }
}
