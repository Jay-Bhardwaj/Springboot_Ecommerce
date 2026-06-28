package com.example.demo.tests;

import static org.junit.jupiter.api.Assertions.assertFalse;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import com.example.demo.pages.HomePage;
import com.example.demo.pages.LoginPage;
import com.example.demo.pages.ProductDetailPage;
import com.example.demo.pages.ProductListingPage;
import com.example.demo.utils.TestCustomer;

public class CheckoutNegativeTest extends BaseTest {

    private void loginAndOpenCheckout() {
        TestCustomer customer = newCustomer("CheckoutNegative");
        createCustomerByApi(customer);

        AuthSession adminSession = loginByApi("admin@shop.com", "Admin@123");
        SeededProduct seededProduct = createProductByApi(
                adminSession,
                "Checkout Negative Product " + System.currentTimeMillis(),
                "Testing",
                "899",
                "10",
                "https://images.unsplash.com/photo-1523275335684-37898b6baf30",
                "Seeded for the checkout negative test.");

        try {
            HomePage homePage = new HomePage(driver);
            homePage.openApp();

            LoginPage loginPage = homePage.clickCustomerLogin();
            loginPage.loginCustomer(customer);

            WebDriverWait wait = new WebDriverWait(driver, java.time.Duration.ofSeconds(30));
            wait.until(ExpectedConditions.visibilityOfElementLocated(
                    By.xpath("//h3[normalize-space()='Recommended products']")));

            ProductListingPage listingPage = new ProductListingPage(driver);
            listingPage.searchProduct(seededProduct.name());

            ProductDetailPage productDetailPage = listingPage.clickViewDetailsForProduct(seededProduct.name());
            productDetailPage.clickAddToCart();
            productDetailPage.clickBackButton();

            wait.until(ExpectedConditions.elementToBeClickable(
                    By.xpath("//button[contains(text(),'Proceed to Checkout')]"))).click();

            wait.until(ExpectedConditions.visibilityOfElementLocated(By.name("customerName")));
        } finally {
            deleteProductByApi(adminSession, seededProduct.id());
        }
    }

    @Test
    @DisplayName("Should not place order when customer name is empty")
    void shouldNotPlaceOrderWhenCustomerNameIsEmpty() {
        loginAndOpenCheckout();

        driver.findElement(By.name("phoneNumber")).sendKeys("9876543210");
        driver.findElement(By.name("addressLine1")).sendKeys("Sector 15");
        driver.findElement(By.name("city")).sendKeys("Gurugram");
        driver.findElement(By.name("state")).sendKeys("Haryana");
        driver.findElement(By.name("postalCode")).sendKeys("122001");

        driver.findElement(By.xpath("//button[contains(text(),'Place Order')]")).click();

        assertFalse(driver.getPageSource().contains("Order placed successfully"));
    }

    @Test
    @DisplayName("Should not place order when phone number is empty")
    void shouldNotPlaceOrderWhenPhoneNumberIsEmpty() {
        loginAndOpenCheckout();

        driver.findElement(By.name("customerName")).sendKeys("Jay Kumar");
        driver.findElement(By.name("addressLine1")).sendKeys("Sector 15");
        driver.findElement(By.name("city")).sendKeys("Gurugram");
        driver.findElement(By.name("state")).sendKeys("Haryana");
        driver.findElement(By.name("postalCode")).sendKeys("122001");

        driver.findElement(By.xpath("//button[contains(text(),'Place Order')]")).click();

        assertFalse(driver.getPageSource().contains("Order placed successfully"));
    }

    @Test
    @DisplayName("Should not place order when postal code is empty")
    void shouldNotPlaceOrderWhenPostalCodeIsEmpty() {
        loginAndOpenCheckout();

        driver.findElement(By.name("customerName")).sendKeys("Jay Kumar");
        driver.findElement(By.name("phoneNumber")).sendKeys("9876543210");
        driver.findElement(By.name("addressLine1")).sendKeys("Sector 15");
        driver.findElement(By.name("city")).sendKeys("Gurugram");
        driver.findElement(By.name("state")).sendKeys("Haryana");

        driver.findElement(By.xpath("//button[contains(text(),'Place Order')]")).click();

        assertFalse(driver.getPageSource().contains("Order placed successfully"));
    }
}
