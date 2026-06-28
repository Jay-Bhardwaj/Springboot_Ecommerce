package com.example.demo.tests;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.Duration;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

import com.example.demo.pages.DashboardPage;
import com.example.demo.pages.HomePage;
import com.example.demo.pages.LoginPage;
import com.example.demo.pages.OrderHistoryPage;
import com.example.demo.pages.ProductDetailPage;
import com.example.demo.pages.ProductListingPage;
import com.example.demo.utils.TestCustomer;

public class CustomerOrderSummaryE2ETest extends BaseTest {

    private static final Pattern ORDER_NUMBER_PATTERN = Pattern.compile("ORD-[A-Za-z0-9-]+");

    @Test
    @DisplayName("Should place a COD order and verify it in order summary")
    void shouldPlaceCodOrderAndVerifyOrderSummary() {
        TestCustomer customer = newCustomer("OrderSummary");
        createCustomerByApi(customer);

        AuthSession adminSession = loginByApi("admin@shop.com", "Admin@123");
        SeededProduct seededProduct = createProductByApi(
                adminSession,
                "Order Summary Product " + System.currentTimeMillis(),
                "Testing",
                "1299",
                "10",
                "https://images.unsplash.com/photo-1523275335684-37898b6baf30",
                "Seeded by Selenium API setup for the order summary test.");

        try {
            HomePage homePage = new HomePage(driver);
            homePage.openApp();

            LoginPage loginPage = homePage.clickCustomerLogin();
            DashboardPage dashboardPage = loginPage.loginCustomer(customer);
            dashboardPage.assertDashboardVisible();

            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(30));
            wait.until(ExpectedConditions.visibilityOfElementLocated(
                    By.xpath("//h3[normalize-space()='Recommended products']")));

            ProductListingPage listingPage = new ProductListingPage(driver);
            listingPage.searchProduct(seededProduct.name());
            ProductDetailPage productDetailPage = listingPage.clickViewDetailsForProduct(seededProduct.name());

            productDetailPage.clickAddToCart();
            productDetailPage.clickBackButton();

            wait.until(ExpectedConditions.elementToBeClickable(
                    By.xpath("//button[normalize-space()='Proceed to Checkout']"))).click();

            fillCheckoutFormForCod(wait, customer);

            String successText = placeOrderAndCaptureSuccessText(wait);
            String orderNumber = extractOrderNumber(successText);

            assertTrue(successText.contains("Order placed successfully"),
                    "Order confirmation should be shown after placing the order.");
            assertTrue(successText.contains(orderNumber),
                    "Success screen should show the generated order number.");

            wait.until(ExpectedConditions.elementToBeClickable(
                    By.xpath("//button[normalize-space()='Continue Shopping']"))).click();

            dashboardPage.assertDashboardVisible();

            wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector(".profile-trigger"))).click();
            wait.until(ExpectedConditions.elementToBeClickable(
                    By.xpath("//button[contains(@class,'profile-option')]//strong[normalize-space()='My Orders']/ancestor::button")))
                    .click();

            OrderHistoryPage orderHistoryPage = new OrderHistoryPage(driver);
            orderHistoryPage.assertOrdersPageVisible();
            orderHistoryPage.waitForOrder(orderNumber);
            orderHistoryPage.assertCashOnDelivery(orderNumber);
            orderHistoryPage.assertPlacedStatus(orderNumber);

            assertTrue(driver.getPageSource().contains(seededProduct.name()),
                    "Order summary should include the purchased product.");
        } finally {
            deleteProductByApi(adminSession, seededProduct.id());
        }
    }

    private void fillCheckoutFormForCod(WebDriverWait wait, TestCustomer customer) {
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.name("customerName")));
        driver.findElement(By.name("customerName")).clear();
        driver.findElement(By.name("customerName")).sendKeys(customer.name());
        driver.findElement(By.name("phoneNumber")).sendKeys("9876543210");
        driver.findElement(By.name("addressLine1")).sendKeys("Sector 15");
        driver.findElement(By.name("addressLine2")).sendKeys("Near Central Park");
        driver.findElement(By.name("city")).sendKeys("Gurugram");
        driver.findElement(By.name("state")).sendKeys("Haryana");
        driver.findElement(By.name("postalCode")).sendKeys("122001");

        Select paymentMethod = new Select(driver.findElement(By.name("paymentMethod")));
        paymentMethod.selectByVisibleText("Cash on Delivery");
    }

    private String placeOrderAndCaptureSuccessText(WebDriverWait wait) {
        wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("//button[normalize-space()='Place Order']"))).click();

        wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//*[contains(normalize-space(),'Order placed successfully')]")));

        String bodyText = driver.findElement(By.tagName("main")).getText();
        Matcher matcher = ORDER_NUMBER_PATTERN.matcher(bodyText);
        if (!matcher.find()) {
            throw new IllegalStateException("Could not find an order number on the success screen.");
        }
        return bodyText;
    }

    private String extractOrderNumber(String text) {
        Matcher matcher = ORDER_NUMBER_PATTERN.matcher(text);
        if (matcher.find()) {
            return matcher.group();
        }
        throw new IllegalStateException("Could not extract the order number from the success text.");
    }
}
