package com.example.demo.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;

public class DashboardPage extends BasePage {

    public DashboardPage(WebDriver driver) {
        super(driver);
    }

    /**
     * Assert that customer dashboard is visible
     */
    public void assertDashboardVisible() {
        wait.until(ExpectedConditions.or(
                ExpectedConditions.textToBePresentInElementLocated(By.tagName("body"), "Logged in as CUSTOMER"),
                ExpectedConditions.textToBePresentInElementLocated(By.tagName("body"), "Recommended products")));
        pauseForDemo();
    }

    /**
     * Get page text (body content)
     */
    public String getPageText() {
        return driver.findElement(By.tagName("body")).getText();
    }

    /**
     * Check if dashboard shows customer role
     */
    public boolean isCustomerRoleDisplayed() {
        return getPageText().contains("Logged in as CUSTOMER");
    }

    /**
     * Check if recommended products are shown
     */
    public boolean areRecommendedProductsDisplayed() {
        return getPageText().contains("Recommended products");
    }

    /**
     * Check if cart summary is visible
     */
    public boolean isCartSummaryDisplayed() {
        return getPageText().contains("Cart Summary");
    }

    /**
     * Assert toast message contains expected text
     */
    public void assertToastContains(String expectedText) {
        wait.until(ExpectedConditions.textToBePresentInElementLocated(By.cssSelector(".Toastify__toast"), expectedText));
        pauseForDemo();
    }

    /**
     * Open the customer profile menu.
     */
    public void openProfileMenu() {

        try {
            wait.until(ExpectedConditions.invisibilityOfElementLocated(
                    By.cssSelector(".Toastify__toast")));
        } catch (Exception e) {
        }

        WebElement trigger =
                wait.until(ExpectedConditions.elementToBeClickable(
                        By.cssSelector(".profile-trigger")));

        try {
            trigger.click();
        } catch (Exception e) {

            ((org.openqa.selenium.JavascriptExecutor) driver)
                    .executeScript(
                            "arguments[0].click();",
                            trigger);
        }

        pauseForDemo();
    }

    /**
     * Log out from the customer dashboard.
     */
    public void logout() {
        openProfileMenu();
        WebElement logoutButton = wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("//button[contains(@class, 'profile-option danger')]//strong[normalize-space()='Logout']/ancestor::button")));
        logoutButton.click();
        pauseForDemo();
        wait.until(ExpectedConditions.textToBePresentInElementLocated(
                By.tagName("body"),
                "Admin access"));
    }
}
