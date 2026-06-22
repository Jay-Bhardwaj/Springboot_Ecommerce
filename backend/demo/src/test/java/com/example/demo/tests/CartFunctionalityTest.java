package com.example.demo.tests;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.Duration;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import com.example.demo.pages.DashboardPage;
import com.example.demo.pages.HomePage;
import com.example.demo.pages.LoginPage;
import com.example.demo.pages.ProductDetailPage;
import com.example.demo.pages.ProductListingPage;
import com.example.demo.utils.TestCustomer;

@DisplayName("Cart Functionality Selenium Tests")
class CartFunctionalityTest extends BaseTest {

	private int findFirstInStockProduct() {

	    int productCount =
	            productListingPage().getTotalProductsCount();

	    System.out.println("Total Products = " + productCount);

	    for (int index = 0; index < productCount; index++) {

	        String name =
	                productListingPage().getProductNameByIndex(index);

	        String stock =
	                productListingPage().getStockStatusByIndex(index);

	        System.out.println(
	                "Product = " + name +
	                " | Stock = " + stock);

	        if (productListingPage().isProductInStock(index)) {
	            return index;
	        }
	    }

	    throw new IllegalStateException(
	            "No in-stock product found.");
	}
	    
    private TestCustomer testCustomer;

    private ProductListingPage productListingPage() {
        return new ProductListingPage(driver);
    }

    @BeforeEach
    void setUpCustomerSession() {
    	testCustomer = new TestCustomer(
    	        "Jay",
    	        "jay@gmail.com",
    	        "jay@123"
    	);

        System.out.println("Step 1: open application");
        HomePage homePage = new HomePage(driver);
        homePage.openApp();

        System.out.println("Step 2: open customer login");
        LoginPage loginPage = homePage.clickCustomerLogin();

        System.out.println("Step 3: sign in as customer");
        DashboardPage dashboardPage = loginPage.loginCustomer(testCustomer);
        dashboardPage.assertDashboardVisible();
    }

    private By cartItemLocator(String productName) {
        return By.xpath("//article[contains(@class, 'cart-item')][.//h4[normalize-space()='" + productName + "']]");
    }

    private By cartQuantityLocator(String productName) {
        return By.xpath("//article[contains(@class, 'cart-item')][.//h4[normalize-space()='" + productName + "']]//div[contains(@class, 'quantity-picker')]//span");
    }

    private By cartRemoveButtonLocator(String productName) {
        return By.xpath("//article[contains(@class, 'cart-item')][.//h4[normalize-space()='" + productName + "']]//button[normalize-space()='Remove']");
    }

    private By cartPlusButtonLocator(String productName) {
        return By.xpath("//article[contains(@class, 'cart-item')][.//h4[normalize-space()='" + productName + "']]//button[normalize-space()='+']");
    }
//
//    private int findProductIndexByKeyword(String keyword) {
//        int productCount = productListingPage().getTotalProductsCount();
//        for (int index = 0; index < productCount; index++) {
//            String productName = productListingPage().getProductNameByIndex(index);
//            if (productName.toLowerCase().contains(keyword.toLowerCase())) {
//                if (!productListingPage().isProductInStock(index)) {
//                    throw new IllegalStateException(keyword + " product is currently out of stock.");
//                }
//                return index;
//            }
//        }
//        throw new IllegalStateException("Could not find a product matching keyword: " + keyword);
//    }

    private String openAndAddTargetProduct() {

        ProductListingPage listingPage = productListingPage();

        int productIndex = findFirstInStockProduct();

        String productName =
                listingPage.getProductNameByIndex(productIndex);

        System.out.println(
                "Step 4: open product details for "
                        + productName);

        ProductDetailPage productDetailPage =
                listingPage.clickViewDetailsForProduct(productIndex);

        assertTrue(
                productDetailPage.isAddToCartButtonVisible(),
                "Add to Cart button should be visible.");

        System.out.println(
                "Step 5: add "
                        + productName
                        + " to cart");

        productDetailPage.clickAddToCart();

        // Wait for success toast to disappear
        new WebDriverWait(driver, Duration.ofSeconds(10))
                .until(ExpectedConditions.invisibilityOfElementLocated(
                        By.cssSelector(".Toastify__toast")));

        productDetailPage.clickBackButton();

        new WebDriverWait(driver, Duration.ofSeconds(10))
                .until(webDriver ->
                        !webDriver.findElements(
                                cartItemLocator(productName))
                                .isEmpty());

        return productName;
    }

    private void logoutAndConfirm() {
        System.out.println("Step 8: log out");
        DashboardPage dashboardPage = new DashboardPage(driver);
        dashboardPage.logout();
        assertTrue(driver.findElement(By.tagName("body")).getText().contains("Customer sign in"),
                "User should return to the login page after logout.");
    }

    @Test
    @DisplayName("4. Complete Cart Flow")
    void testCompleteCartFlow() {

        String productName = openAndAddTargetProduct();

        System.out.println("Verify Product Added");

        assertTrue(
                !driver.findElements(
                        cartItemLocator(productName))
                        .isEmpty());

        // Increase Quantity

        System.out.println("Increase Quantity");

        WebElement plusButton =
                new WebDriverWait(driver,
                        Duration.ofSeconds(10))
                        .until(ExpectedConditions.elementToBeClickable(
                                cartPlusButtonLocator(productName)));

        ((JavascriptExecutor) driver)
                .executeScript(
                        "arguments[0].scrollIntoView({block:'center'});",
                        plusButton);

        plusButton.click();

        new WebDriverWait(driver,
                Duration.ofSeconds(10))
                .until(ExpectedConditions
                        .textToBePresentInElementLocated(
                                cartQuantityLocator(productName),
                                "2"));

        assertEquals(
                "2",
                driver.findElement(
                        cartQuantityLocator(productName))
                        .getText());

        // Decrease Quantity

     // Decrease Quantity

        System.out.println("Decrease Quantity");

        WebElement minusButton =
                new WebDriverWait(driver,
                        Duration.ofSeconds(10))
                        .until(ExpectedConditions.elementToBeClickable(
                                By.xpath(
                                        "//article[contains(@class,'cart-item')]"
                                        + "[.//h4[normalize-space()='"
                                        + productName
                                        + "']]//button[normalize-space()='-']")));

        ((JavascriptExecutor) driver)
                .executeScript(
                        "arguments[0].scrollIntoView({block:'center'});",
                        minusButton);

        minusButton.click();

        new WebDriverWait(driver,
                Duration.ofSeconds(10))
                .until(ExpectedConditions
                        .textToBePresentInElementLocated(
                                cartQuantityLocator(productName),
                                "1"));

        assertEquals(
                "1",
                driver.findElement(
                        cartQuantityLocator(productName))
                        .getText());

        // Remove Product

     // Remove Product

        System.out.println("Remove Product");

        WebElement removeButton =
                new WebDriverWait(driver,
                        Duration.ofSeconds(10))
                        .until(ExpectedConditions.elementToBeClickable(
                                cartRemoveButtonLocator(productName)));

        ((JavascriptExecutor) driver)
                .executeScript(
                        "arguments[0].scrollIntoView({block:'center'});",
                        removeButton);

        removeButton.click();

        new WebDriverWait(driver,
                Duration.ofSeconds(10))
                .until(ExpectedConditions
                        .textToBePresentInElementLocated(
                                By.cssSelector("p.empty-state"),
                                "Your cart is empty"));

        // Logout

        logoutAndConfirm();
    }
}
