package com.example.demo.tests;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.Duration;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;

import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

public class CheckoutFlowTest {

    private WebDriver driver;
    private WebDriverWait wait;

    @BeforeEach
    void setup() {

        driver = new ChromeDriver();

        driver.manage().window().maximize();

        wait = new WebDriverWait(
                driver,
                Duration.ofSeconds(30));
    }

    @AfterEach
    void tearDown() {

        if (driver != null) {
            driver.quit();
        }
    }

    @Test
    void shouldPlaceOrderSuccessfully() {

        //---------------------------------
        // OPEN APP
        //---------------------------------

        driver.get("http://localhost:3000");

        //---------------------------------
        // LOGIN
        //---------------------------------

        wait.until(
                ExpectedConditions.visibilityOfElementLocated(
                        By.name("email")));

        driver.findElement(By.name("email"))
                .sendKeys("jay@gmail.com");

        driver.findElement(By.name("password"))
                .sendKeys("jay@123");

        wait.until(
                ExpectedConditions.elementToBeClickable(
                        By.xpath("//button[contains(text(),'Customer Login')]")))
                .click();

        //---------------------------------
        // WAIT FOR PRODUCTS
        //---------------------------------

        wait.until(
                ExpectedConditions.visibilityOfElementLocated(
                        By.xpath("//h3[contains(text(),'Recommended products')]")));

        //---------------------------------
        // OPEN FIRST PRODUCT
        //---------------------------------

        wait.until(
                ExpectedConditions.elementToBeClickable(
                        By.xpath("(//button[contains(text(),'View Details')])[1]")))
                .click();

        //---------------------------------
        // ADD TO CART
        //---------------------------------

        wait.until(
                ExpectedConditions.elementToBeClickable(
                        By.xpath("//button[contains(text(),'Add to Cart')]")))
                .click();

        //---------------------------------
        // BACK TO CATALOG
        //---------------------------------

        wait.until(
                ExpectedConditions.elementToBeClickable(
                        By.xpath("//button[contains(text(),'Back to Catalog')]")))
                .click();

        //---------------------------------
        // PROCEED TO CHECKOUT
        //---------------------------------

        wait.until(
                ExpectedConditions.elementToBeClickable(
                        By.xpath("//button[contains(text(),'Proceed to Checkout')]")))
                .click();

        //---------------------------------
        // FILL ADDRESS
        //---------------------------------

        wait.until(
                ExpectedConditions.visibilityOfElementLocated(
                        By.name("customerName")));

        WebElement customerName =
                driver.findElement(By.name("customerName"));

        customerName.clear();
        customerName.sendKeys("Jay Kumar");

        driver.findElement(By.name("phoneNumber"))
                .sendKeys("9876543210");

        driver.findElement(By.name("addressLine1"))
                .sendKeys("Sector 15");

        driver.findElement(By.name("city"))
                .sendKeys("Gurugram");

        driver.findElement(By.name("state"))
                .sendKeys("Haryana");

        driver.findElement(By.name("postalCode"))
                .sendKeys("122001");

        //---------------------------------
        // PLACE ORDER
        //---------------------------------

        wait.until(
                ExpectedConditions.elementToBeClickable(
                        By.xpath("//button[contains(text(),'Place Order')]")))
                .click();

        //---------------------------------
        // VERIFY SUCCESS
        //---------------------------------

        WebElement successTitle =
                wait.until(
                        ExpectedConditions.visibilityOfElementLocated(
                                By.xpath("//*[contains(text(),'Order placed successfully')]")));

        assertTrue(successTitle.isDisplayed());

        WebElement orderNumber =
                driver.findElement(
                        By.xpath("//*[contains(text(),'ORD-')]"));

        assertTrue(orderNumber.getText().contains("ORD-"));
    }
}