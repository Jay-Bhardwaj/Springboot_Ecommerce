package com.example.demo.pages;

import org.junit.jupiter.api.Assertions;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;

public class OrderHistoryPage extends BasePage {

    public OrderHistoryPage(WebDriver driver) {
        super(driver);
    }

    /**
     * Wait until the order history screen is ready.
     */
    public void assertOrdersPageVisible() {
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//h1[normalize-space()='My orders']")));
    }

    private By orderCard(String orderNumber) {
        return By.xpath("//article[contains(@class,'order-history-card')][.//*[contains(normalize-space(),'" + orderNumber + "')]]");
    }

    /**
     * Wait for the given order number to appear in order history.
     */
    public void waitForOrder(String orderNumber) {
        wait.until(ExpectedConditions.visibilityOfElementLocated(orderCard(orderNumber)));
    }

    /**
     * Assert the order card contains the expected COD label.
     */
    public void assertCashOnDelivery(String orderNumber) {
        String cardText = driver.findElement(orderCard(orderNumber)).getText();
        Assertions.assertTrue(cardText.toLowerCase().contains("cash on delivery"), "Order history should show Cash on Delivery.");
    }

    /**
     * Assert the order card shows placed status.
     */
    public void assertPlacedStatus(String orderNumber) {
        String cardText = driver.findElement(orderCard(orderNumber)).getText();
        Assertions.assertTrue(cardText.toUpperCase().contains("PLACED"), "Order history should show PLACED status.");
    }
}
