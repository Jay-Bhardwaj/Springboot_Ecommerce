package com.example.demo.tests;

import static org.junit.jupiter.api.Assertions.*;

import java.time.Duration;

import org.junit.jupiter.api.*;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.*;

public class CheckoutNegativeTest {

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

    private void loginAndOpenCheckout() {
 
    	 System.out.println("Login Started");
        driver.get("http://localhost:3000");
        
  driver.findElement(By.xpath("//button[normalize-space()='Customer Login']")).click();
  wait.until(ExpectedConditions.visibilityOfElementLocated(
          By.xpath("//h2[normalize-space()='Customer sign in']")));
        wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.name("email")));

        driver.findElement(By.name("email"))
                .sendKeys("jay@gmail.com");
      
        
        wait.until(ExpectedConditions.visibilityOfElementLocated(
        		By.xpath("//input[@placeholder='Enter your password']")));

        driver.findElement(By.xpath("//input[@placeholder='Enter your password']"))
                .sendKeys("jay@123");
        
        

        driver.findElement(
                By.xpath("//button[@type='submit' and normalize-space()='Login as Customer']"))
                .click();

        wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("(//button[contains(text(),'View Details')])[1]")))
                .click();

        wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("//button[contains(text(),'Add to Cart')]")))
                .click();

        wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("//button[contains(text(),'Back to Catalog')]")))
                .click();

        wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("//button[contains(text(),'Proceed to Checkout')]")))
                .click();

        wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.name("customerName")));
        System.out.println("User Logged In");
    }

    @Test
    void shouldNotPlaceOrderWhenCustomerNameIsEmpty() {

        loginAndOpenCheckout();
        System.out.println("1st test when customer name is empty");
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

        driver.findElement(
                By.xpath("//button[contains(text(),'Place Order')]"))
                .click();

        assertFalse(
                driver.getPageSource()
                        .contains("Order placed successfully"));
    }

    @Test
    void shouldNotPlaceOrderWhenPhoneNumberIsEmpty() {

        loginAndOpenCheckout();
        System.out.println("2nd test when phone number is empty");
        driver.findElement(By.name("customerName"))
                .sendKeys("Jay Kumar");

        driver.findElement(By.name("addressLine1"))
                .sendKeys("Sector 15");

        driver.findElement(By.name("city"))
                .sendKeys("Gurugram");

        driver.findElement(By.name("state"))
                .sendKeys("Haryana");

        driver.findElement(By.name("postalCode"))
                .sendKeys("122001");

        driver.findElement(
                By.xpath("//button[contains(text(),'Place Order')]"))
                .click();

        assertFalse(
                driver.getPageSource()
                        .contains("Order placed successfully"));
    }

    @Test
    void shouldNotPlaceOrderWhenPostalCodeIsEmpty() {

        loginAndOpenCheckout();
        System.out.println("3rd test when postal code is empty");
        driver.findElement(By.name("customerName"))
                .sendKeys("Jay Kumar");

        driver.findElement(By.name("phoneNumber"))
                .sendKeys("9876543210");

        driver.findElement(By.name("addressLine1"))
                .sendKeys("Sector 15");

        driver.findElement(By.name("city"))
                .sendKeys("Gurugram");

        driver.findElement(By.name("state"))
                .sendKeys("Haryana");

        driver.findElement(
                By.xpath("//button[contains(text(),'Place Order')]"))
                .click();

        assertFalse(
                driver.getPageSource()
                        .contains("Order placed successfully"));
        
        System.out.println("Negative testing is completed.");
    }
}