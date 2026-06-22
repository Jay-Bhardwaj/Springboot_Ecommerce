package com.example.demo.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

public class CartPage extends BasePage {

    public CartPage(WebDriver driver) {
        super(driver);
    }

    private By cartItem(String productName) {
        return By.xpath(
            "//article[contains(@class,'cart-item')]"
            + "[.//h4[normalize-space()='" + productName + "']]"
        );
    }

    private By quantity(String productName) {
        return By.xpath(
            "//article[contains(@class,'cart-item')]"
            + "[.//h4[normalize-space()='" + productName + "']]"
            + "//div[contains(@class,'quantity-picker')]//span"
        );
    }

    private By plusButton(String productName) {
        return By.xpath(
            "//article[contains(@class,'cart-item')]"
            + "[.//h4[normalize-space()='" + productName + "']]"
            + "//button[normalize-space()='+']"
        );
    }

    private By minusButton(String productName) {
        return By.xpath(
            "//article[contains(@class,'cart-item')]"
            + "[.//h4[normalize-space()='" + productName + "']]"
            + "//button[normalize-space()='-']"
        );
    }

    private By removeButton(String productName) {
        return By.xpath(
            "//article[contains(@class,'cart-item')]"
            + "[.//h4[normalize-space()='" + productName + "']]"
            + "//button[normalize-space()='Remove']"
        );
    }

    public void increaseQuantity(String productName) {
        driver.findElement(plusButton(productName)).click();
    }

    public void decreaseQuantity(String productName) {
        driver.findElement(minusButton(productName)).click();
    }

    public void removeItem(String productName) {
        driver.findElement(removeButton(productName)).click();
    }

    public String getQuantity(String productName) {
        return driver.findElement(quantity(productName)).getText();
    }

    public boolean isProductPresent(String productName) {
        return !driver.findElements(cartItem(productName)).isEmpty();
    }
}