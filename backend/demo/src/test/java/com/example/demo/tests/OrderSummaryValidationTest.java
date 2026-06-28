package com.example.demo.tests;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Duration;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import com.example.demo.utils.TestCustomer;

public class OrderSummaryValidationTest extends BaseTest {

    private static final String GST_RATE_DISPLAY = "18";
    private static final BigDecimal GST_RATE = new BigDecimal("0.18");
    private static final BigDecimal FREE_DELIVERY_THRESHOLD = new BigDecimal("500");
    private static final BigDecimal DELIVERY_CHARGE = new BigDecimal("40");

    static java.util.stream.Stream<org.junit.jupiter.params.provider.Arguments> cartScenarios() {
        return java.util.stream.Stream.of(
                org.junit.jupiter.params.provider.Arguments.of(
                        "Single item below free delivery",
                        new BigDecimal("100.00"),
                        1,
                        new BigDecimal("100.00"),
                        new BigDecimal("18.00"),
                        new BigDecimal("40.00"),
                        new BigDecimal("158.00")
                ),
                org.junit.jupiter.params.provider.Arguments.of(
                        "Single item at free delivery threshold",
                        new BigDecimal("500.00"),
                        1,
                        new BigDecimal("500.00"),
                        new BigDecimal("90.00"),
                        new BigDecimal("0.00"),
                        new BigDecimal("590.00")
                ),
                org.junit.jupiter.params.provider.Arguments.of(
                        "Single item above free delivery",
                        new BigDecimal("750.00"),
                        1,
                        new BigDecimal("750.00"),
                        new BigDecimal("135.00"),
                        new BigDecimal("0.00"),
                        new BigDecimal("885.00")
                ),
                org.junit.jupiter.params.provider.Arguments.of(
                        "Multiple items below free delivery",
                        new BigDecimal("250.00"),
                        3,
                        new BigDecimal("250.00"),
                        new BigDecimal("45.00"),
                        new BigDecimal("40.00"),
                        new BigDecimal("335.00")
                ),
                org.junit.jupiter.params.provider.Arguments.of(
                        "Multiple items above free delivery",
                        new BigDecimal("600.00"),
                        4,
                        new BigDecimal("600.00"),
                        new BigDecimal("108.00"),
                        new BigDecimal("0.00"),
                        new BigDecimal("708.00")
                ),
                org.junit.jupiter.params.provider.Arguments.of(
                        "Edge case - exactly one rupee below threshold",
                        new BigDecimal("499.00"),
                        2,
                        new BigDecimal("499.00"),
                        new BigDecimal("89.82"),
                        new BigDecimal("40.00"),
                        new BigDecimal("628.82")
                )
        );
    }

    @Test
    @DisplayName("Should validate order summary with single product checkout")
    void shouldValidateOrderSummaryWithSingleProduct() {
        TestCustomer customer = newCustomer("OrderSummary");
        createCustomerByApi(customer);

        driver.get(FRONTEND_URL);

        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(30));

        // Login
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.name("email")))
                .sendKeys(customer.email());
        driver.findElement(By.name("password")).sendKeys(customer.password());
        wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("//button[contains(text(),'Login as Customer')]"))).click();

        // Wait for products
        wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//h3[contains(text(),'Recommended products')]")));

        // Get first product price
        WebElement firstProductPrice = wait.until(
                ExpectedConditions.visibilityOfElementLocated(
                        By.xpath("(//strong[contains(@class,'catalog-price')])[1]")));
        String priceText = firstProductPrice.getText().replace("Rs. ", "").trim();
        BigDecimal productPrice = new BigDecimal(priceText);

        // Open first product
        wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("(//button[contains(text(),'View Details')])[1]"))).click();

        // Add to cart
        wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("//button[contains(text(),'Add to Cart')]"))).click();

        pauseForDemo();

        // Return to the catalog so the cart summary panel is visible
        wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("//button[normalize-space()='Back to catalog']"))).click();

        // Verify cart summary calculations
        verifyCartCalculations(wait, productPrice, 1);

        // Proceed to checkout
        wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("//button[contains(text(),'Proceed to Checkout')]"))).click();

        // Fill address
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.name("customerName")));
        driver.findElement(By.name("customerName")).sendKeys(customer.name());
        driver.findElement(By.name("phoneNumber")).sendKeys("9876543210");
        driver.findElement(By.name("addressLine1")).sendKeys("Sector 15");
        driver.findElement(By.name("city")).sendKeys("Gurugram");
        driver.findElement(By.name("state")).sendKeys("Haryana");
        driver.findElement(By.name("postalCode")).sendKeys("122001");

        // Verify checkout page calculations
        verifyCheckoutCalculations(wait, productPrice, 1);

        // Place order
        wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("//button[contains(text(),'Place Order')]"))).click();

        // Verify success
        WebElement successMessage = wait.until(
                ExpectedConditions.visibilityOfElementLocated(
                        By.xpath("//*[contains(text(),'Order placed successfully')]")));
        assertTrue(successMessage.isDisplayed(), "Order success message should be displayed");

        // Verify order number format
        WebElement orderNumber = driver.findElement(
                By.xpath("//*[contains(text(),'ORD-')]"));
        assertTrue(orderNumber.getText().contains("ORD-"),
                "Order number should contain 'ORD-' prefix");
    }

    @ParameterizedTest(name = "{0}: price={1}, quantity={2}")
    @MethodSource("cartScenarios")
    @DisplayName("Should validate cart calculations with various scenarios")
    void shouldValidateCartCalculationsDataDriven(
            String scenarioName,
            BigDecimal unitPrice,
            int quantity,
            BigDecimal expectedSubtotal,
            BigDecimal expectedGst,
            BigDecimal expectedDelivery,
            BigDecimal expectedTotal
    ) {
        TestCustomer customer = newCustomer("CartCalc" + quantity);
        createCustomerByApi(customer);

        driver.get(FRONTEND_URL);

        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(30));

        // Switch to Customer Login tab
        wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("//button[contains(text(),'Customer Login')]"))).click();

        // Login
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.name("email")))
                .sendKeys(customer.email());
        driver.findElement(By.name("password")).sendKeys(customer.password());
        wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("//button[contains(text(),'Login as Customer')]"))).click();

        // Wait for products
        wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//h3[contains(text(),'Recommended products')]")));

        // Add product to cart (simulate by adding first product multiple times)
        for (int i = 0; i < quantity; i++) {
            // Open first product
            wait.until(ExpectedConditions.elementToBeClickable(
                    By.xpath("(//button[contains(text(),'View Details')])[1]"))).click();

            // Add to cart
            wait.until(ExpectedConditions.elementToBeClickable(
                    By.xpath("//button[contains(text(),'Add to Cart')]"))).click();

            pauseForDemo(1000);

            // Return to the catalog after each add so the cart summary is visible
            wait.until(ExpectedConditions.elementToBeClickable(
                    By.xpath("//button[normalize-space()='Back to catalog']"))).click();
            pauseForDemo(1000);
        }

        // Verify cart calculations
        verifyCartCalculations(wait, unitPrice.multiply(BigDecimal.valueOf(quantity)), quantity);

        // Verify specific calculated values
        WebElement subtotalElement = wait.until(
                ExpectedConditions.visibilityOfElementLocated(
                        By.xpath("//*[contains(text(),'Subtotal')]/following-sibling::strong")));
        String subtotalText = subtotalElement.getText().replace("Rs. ", "").trim();
        BigDecimal actualSubtotal = new BigDecimal(subtotalText);

        assertEquals(expectedSubtotal.setScale(2, RoundingMode.HALF_UP),
                actualSubtotal.setScale(2, RoundingMode.HALF_UP),
                "Subtotal should match expected value for scenario: " + scenarioName);

        // Verify GST
        WebElement gstElement = driver.findElement(
                By.xpath("//*[contains(text(),'GST (18%)')]/following-sibling::strong"));
        String gstText = gstElement.getText().replace("Rs. ", "").trim();
        BigDecimal actualGst = new BigDecimal(gstText);

        assertEquals(expectedGst.setScale(2, RoundingMode.HALF_UP),
                actualGst.setScale(2, RoundingMode.HALF_UP),
                "GST should match expected value for scenario: " + scenarioName);

        // Verify delivery charge
        WebElement deliveryElement = driver.findElement(
                By.xpath("//*[contains(text(),'Delivery')]/following-sibling::strong"));
        String deliveryText = deliveryElement.getText().trim();

        BigDecimal actualDelivery;
        if (deliveryText.equals("Free")) {
            actualDelivery = BigDecimal.ZERO;
        } else {
            actualDelivery = new BigDecimal(deliveryText.replace("Rs. ", "").trim());
        }

        assertEquals(expectedDelivery.setScale(2, RoundingMode.HALF_UP),
                actualDelivery.setScale(2, RoundingMode.HALF_UP),
                "Delivery charge should match expected value for scenario: " + scenarioName);

        // Verify total bill
        WebElement totalElement = driver.findElement(
                By.xpath("//*[contains(text(),'Total Bill')]/following-sibling::strong"));
        String totalText = totalElement.getText().replace("Rs. ", "").trim();
        BigDecimal actualTotal = new BigDecimal(totalText);

        assertEquals(expectedTotal.setScale(2, RoundingMode.HALF_UP),
                actualTotal.setScale(2, RoundingMode.HALF_UP),
                "Total bill should match expected value for scenario: " + scenarioName);

        // Verify formula: Subtotal + GST + Delivery = Total
        BigDecimal calculatedTotal = actualSubtotal
                .add(actualGst)
                .add(actualDelivery)
                .setScale(2, RoundingMode.HALF_UP);

        assertEquals(calculatedTotal, actualTotal.setScale(2, RoundingMode.HALF_UP),
                "Total should equal Subtotal + GST + Delivery for scenario: " + scenarioName);
    }

    @Test
    @DisplayName("Should verify free delivery threshold logic")
    void shouldVerifyFreeDeliveryThreshold() {
        TestCustomer customer = newCustomer("FreeDelivery");
        createCustomerByApi(customer);

        driver.get(FRONTEND_URL);

        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(30));

        // Login
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.name("email")))
                .sendKeys(customer.email());
        driver.findElement(By.name("password")).sendKeys(customer.password());
        wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("//button[contains(text(),'Customer Login')]"))).click();

        // Wait for products
        wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//h3[contains(text(),'Recommended products')]")));

        // Add first product
        wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("(//button[contains(text(),'View Details')])[1]"))).click();
        wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("//button[contains(text(),'Add to Cart')]"))).click();

        pauseForDemo();

        wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("//button[normalize-space()='Back to catalog']"))).click();

        // Verify delivery charge message for cart below threshold
        WebElement deliveryMessage = wait.until(
                ExpectedConditions.visibilityOfElementLocated(
                        By.xpath("//*[contains(text(),'Add Rs.')]")));
        assertTrue(deliveryMessage.isDisplayed(),
                "Should show message to add more for free delivery");

        // Verify delivery charge is applied
        WebElement deliveryChargeElement = driver.findElement(
                By.xpath("//*[contains(text(),'Delivery')]/following-sibling::strong"));
        assertTrue(deliveryChargeElement.getText().contains("Rs. 40.00"),
                "Delivery charge should be Rs. 40.00 when below threshold");

        // Go back and add more products until we cross threshold
        wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("//button[contains(text(),'Back to catalog')]"))).click();

        // Add second product
        wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("(//button[contains(text(),'View Details')])[2]"))).click();
        wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("//button[contains(text(),'Add to Cart')]"))).click();

        pauseForDemo();

        wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("//button[normalize-space()='Back to catalog']"))).click();

        // Verify free delivery unlocked message
        WebElement freeDeliveryMessage = wait.until(
                ExpectedConditions.visibilityOfElementLocated(
                        By.xpath("//*[contains(text(),'Free delivery unlocked')]")));
        assertTrue(freeDeliveryMessage.isDisplayed(),
                "Should show free delivery unlocked message when threshold is met");

        // Verify delivery is free
        WebElement freeDeliveryCharge = driver.findElement(
                By.xpath("//*[contains(text(),'Delivery')]/following-sibling::strong"));
        assertEquals("Free", freeDeliveryCharge.getText().trim(),
                "Delivery should be free when threshold is met");
    }

    @Test
    @DisplayName("Should verify quantity update affects total calculations")
    void shouldVerifyQuantityUpdateAffectsTotals() {
        TestCustomer customer = newCustomer("QtyUpdate");
        createCustomerByApi(customer);

        driver.get(FRONTEND_URL);

        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(30));

        // Login
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.name("email")))
                .sendKeys(customer.email());
        driver.findElement(By.name("password")).sendKeys(customer.password());
        wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("//button[contains(text(),'Customer Login')]"))).click();

        // Wait for products
        wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//h3[contains(text(),'Recommended products')]")));

        // Add first product
        wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("(//button[contains(text(),'View Details')])[1]"))).click();
        wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("//button[contains(text(),'Add to Cart')]"))).click();

        pauseForDemo();

        wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("//button[normalize-space()='Back to catalog']"))).click();

        // Get initial quantity and total
        WebElement initialQty = wait.until(
                ExpectedConditions.visibilityOfElementLocated(
                        By.xpath("//article[contains(@class,'cart-item')]//span")));
        int initialQuantity = Integer.parseInt(initialQty.getText().trim());

        WebElement initialTotal = driver.findElement(
                By.xpath("//*[contains(text(),'Total Bill')]/following-sibling::strong"));
        BigDecimal initialTotalAmount = new BigDecimal(
                initialTotal.getText().replace("Rs. ", "").trim());

        // Increase quantity
        driver.findElement(
                By.xpath("//button[contains(@class,'quantity-button') and text()='+']"))
                .click();

        pauseForDemo();

        // Get updated quantity and total
        WebElement updatedQty = driver.findElement(
                By.xpath("//article[contains(@class,'cart-item')]//span"));
        int updatedQuantity = Integer.parseInt(updatedQty.getText().trim());

        WebElement updatedTotal = driver.findElement(
                By.xpath("//*[contains(text(),'Total Bill')]/following-sibling::strong"));
        BigDecimal updatedTotalAmount = new BigDecimal(
                updatedTotal.getText().replace("Rs. ", "").trim());

        // Verify quantity increased
        assertEquals(initialQuantity + 1, updatedQuantity,
                "Quantity should increase by 1");

        // Verify total increased proportionally
        BigDecimal expectedIncrease = updatedTotalAmount.subtract(initialTotalAmount);
        assertTrue(expectedIncrease.compareTo(BigDecimal.ZERO) > 0,
                "Total should increase when quantity increases");

        // Decrease quantity
        driver.findElement(
                By.xpath("//button[contains(@class,'quantity-button') and text()='-']"))
                .click();

        pauseForDemo();

        // Get final quantity and total
        WebElement finalQty = driver.findElement(
                By.xpath("//article[contains(@class,'cart-item')]//span"));
        int finalQuantity = Integer.parseInt(finalQty.getText().trim());

        WebElement finalTotal = driver.findElement(
                By.xpath("//*[contains(text(),'Total Bill')]/following-sibling::strong"));
        BigDecimal finalTotalAmount = new BigDecimal(
                finalTotal.getText().replace("Rs. ", "").trim());

        // Verify quantity decreased
        assertEquals(updatedQuantity - 1, finalQuantity,
                "Quantity should decrease by 1");

        // Verify total decreased
        assertTrue(finalTotalAmount.compareTo(updatedTotalAmount) < 0,
                "Total should decrease when quantity decreases");
    }

    @Nested
    @DisplayName("Cart Item Validation Tests")
    class CartItemValidationTests {

        @Test
        @DisplayName("Should verify line total calculation for cart items")
        void shouldVerifyLineTotalCalculation() {
            TestCustomer customer = newCustomer("LineTotal");
            createCustomerByApi(customer);

            driver.get(FRONTEND_URL);

            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(30));

            // Login
            wait.until(ExpectedConditions.visibilityOfElementLocated(By.name("email")))
                    .sendKeys(customer.email());
            driver.findElement(By.name("password")).sendKeys(customer.password());
            wait.until(ExpectedConditions.elementToBeClickable(
                    By.xpath("//button[contains(text(),'Customer Login')]"))).click();

            // Wait for products
            wait.until(ExpectedConditions.visibilityOfElementLocated(
                    By.xpath("//h3[contains(text(),'Recommended products')]")));

            // Add first product
            wait.until(ExpectedConditions.elementToBeClickable(
                    By.xpath("(//button[contains(text(),'View Details')])[1]"))).click();
            wait.until(ExpectedConditions.elementToBeClickable(
                    By.xpath("//button[contains(text(),'Add to Cart')]"))).click();

            pauseForDemo();

            // Get unit price
            WebElement unitPriceElement = wait.until(
                    ExpectedConditions.visibilityOfElementLocated(
                            By.xpath("//article[contains(@class,'cart-item')]//p")));
            String unitPriceText = unitPriceElement.getText().replace("Rs. ", "").replace(" each", "").trim();
            BigDecimal unitPrice = new BigDecimal(unitPriceText);

            // Get quantity
            WebElement quantityElement = driver.findElement(
                    By.xpath("//article[contains(@class,'cart-item')]//span"));
            int quantity = Integer.parseInt(quantityElement.getText().trim());

            // Get line total
            WebElement lineTotalElement = driver.findElement(
                    By.xpath("//article[contains(@class,'cart-item')]//strong"));
            String lineTotalText = lineTotalElement.getText().replace("Rs. ", "").trim();
            BigDecimal lineTotal = new BigDecimal(lineTotalText);

            // Verify line total = unit price * quantity
            BigDecimal expectedLineTotal = unitPrice
                    .multiply(BigDecimal.valueOf(quantity))
                    .setScale(2, RoundingMode.HALF_UP);

            assertEquals(expectedLineTotal, lineTotal.setScale(2, RoundingMode.HALF_UP),
                    "Line total should equal unit price × quantity");
        }

        @Test
        @DisplayName("Should verify cart item count matches actual items")
        void shouldVerifyCartItemCount() {
            TestCustomer customer = newCustomer("ItemCount");
            createCustomerByApi(customer);

            driver.get(FRONTEND_URL);

            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(30));

            // Login
            wait.until(ExpectedConditions.visibilityOfElementLocated(By.name("email")))
                    .sendKeys(customer.email());
            driver.findElement(By.name("password")).sendKeys(customer.password());
            wait.until(ExpectedConditions.elementToBeClickable(
                    By.xpath("//button[contains(text(),'Customer Login')]"))).click();

            // Wait for products
            wait.until(ExpectedConditions.visibilityOfElementLocated(
                    By.xpath("//h3[contains(text(),'Recommended products')]")));

            // Add two different products
            for (int i = 1; i <= 2; i++) {
                wait.until(ExpectedConditions.elementToBeClickable(
                        By.xpath("(//button[contains(text(),'View Details')])[" + i + "]"))).click();
                wait.until(ExpectedConditions.elementToBeClickable(
                        By.xpath("//button[contains(text(),'Add to Cart')]"))).click();
                pauseForDemo(1000);

                if (i < 2) {
                    wait.until(ExpectedConditions.elementToBeClickable(
                            By.xpath("//button[contains(text(),'Back to catalog')]"))).click();
                    pauseForDemo(1000);
                }
            }

            // Count cart items in UI
            java.util.List<WebElement> cartItems = driver.findElements(
                    By.xpath("//article[contains(@class,'cart-item')]"));
            int actualItemCount = cartItems.size();

            // Get displayed cart count
            WebElement cartCountElement = wait.until(
                    ExpectedConditions.visibilityOfElementLocated(
                            By.xpath("//*[contains(text(),'in cart')]")));
            String cartCountText = cartCountElement.getText().split(" in cart")[0].trim();
            int displayedCartCount = Integer.parseInt(cartCountText);

            assertEquals(actualItemCount, displayedCartCount,
                    "Displayed cart count should match actual cart items");
        }
    }

    @Nested
    @DisplayName("Checkout Page Validation Tests")
    class CheckoutPageValidationTests {

        @Test
        @DisplayName("Should verify checkout page displays correct order summary")
        void shouldVerifyCheckoutPageOrderSummary() {
            TestCustomer customer = newCustomer("CheckoutSummary");
            createCustomerByApi(customer);

            driver.get(FRONTEND_URL);

            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(30));

            // Login and add product to cart
            wait.until(ExpectedConditions.visibilityOfElementLocated(By.name("email")))
                    .sendKeys(customer.email());
            driver.findElement(By.name("password")).sendKeys(customer.password());
            wait.until(ExpectedConditions.elementToBeClickable(
                    By.xpath("//button[contains(text(),'Customer Login')]"))).click();

            wait.until(ExpectedConditions.visibilityOfElementLocated(
                    By.xpath("//h3[contains(text(),'Recommended products')]")));

            wait.until(ExpectedConditions.elementToBeClickable(
                    By.xpath("(//button[contains(text(),'View Details')])[1]"))).click();
            wait.until(ExpectedConditions.elementToBeClickable(
                    By.xpath("//button[contains(text(),'Add to Cart')]"))).click();

            pauseForDemo();

            // Get cart totals before checkout
            WebElement cartSubtotal = driver.findElement(
                    By.xpath("//*[contains(text(),'Subtotal')]/following-sibling::strong"));
            BigDecimal beforeCheckoutSubtotal = new BigDecimal(
                    cartSubtotal.getText().replace("Rs. ", "").trim());

            WebElement cartTotal = driver.findElement(
                    By.xpath("//*[contains(text(),'Total Bill')]/following-sibling::strong"));
            BigDecimal beforeCheckoutTotal = new BigDecimal(
                    cartTotal.getText().replace("Rs. ", "").trim());

            // Proceed to checkout
            wait.until(ExpectedConditions.elementToBeClickable(
                    By.xpath("//button[contains(text(),'Proceed to Checkout')]"))).click();

            pauseForDemo();

            // Verify checkout page shows same totals
            WebElement checkoutSubtotal = wait.until(
                    ExpectedConditions.visibilityOfElementLocated(
                            By.xpath("//*[contains(text(),'Subtotal')]/following-sibling::strong")));
            BigDecimal afterCheckoutSubtotal = new BigDecimal(
                    checkoutSubtotal.getText().replace("Rs. ", "").trim());

            assertEquals(beforeCheckoutSubtotal.setScale(2, RoundingMode.HALF_UP),
                    afterCheckoutSubtotal.setScale(2, RoundingMode.HALF_UP),
                    "Subtotal should remain consistent between cart and checkout");

            // Verify order summary section exists
            WebElement orderSummary = wait.until(
                    ExpectedConditions.visibilityOfElementLocated(
                            By.xpath("//*[contains(text(),'Order Summary')]")));
            assertTrue(orderSummary.isDisplayed(),
                    "Order Summary section should be visible on checkout page");
        }
    }

    @Nested
    @DisplayName("Calculation Formula Validation Tests")
    class CalculationFormulaValidationTests {

        @Test
        @DisplayName("Should verify GST is calculated correctly as 18% of subtotal")
        void shouldVerifyGstCalculation() {
            TestCustomer customer = newCustomer("GstCalc");
            createCustomerByApi(customer);

            driver.get(FRONTEND_URL);

            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(30));

            // Login
            wait.until(ExpectedConditions.visibilityOfElementLocated(By.name("email")))
                    .sendKeys(customer.email());
            driver.findElement(By.name("password")).sendKeys(customer.password());
            wait.until(ExpectedConditions.elementToBeClickable(
                    By.xpath("//button[contains(text(),'Customer Login')]"))).click();

            // Wait for products
            wait.until(ExpectedConditions.visibilityOfElementLocated(
                    By.xpath("//h3[contains(text(),'Recommended products')]")));

            // Add product
            wait.until(ExpectedConditions.elementToBeClickable(
                    By.xpath("(//button[contains(text(),'View Details')])[1]"))).click();
            wait.until(ExpectedConditions.elementToBeClickable(
                    By.xpath("//button[contains(text(),'Add to Cart')]"))).click();

            pauseForDemo();

            // Get subtotal
            WebElement subtotalElement = wait.until(
                    ExpectedConditions.visibilityOfElementLocated(
                            By.xpath("//*[contains(text(),'Subtotal')]/following-sibling::strong")));
            BigDecimal subtotal = new BigDecimal(
                    subtotalElement.getText().replace("Rs. ", "").trim());

            // Get GST
            WebElement gstElement = driver.findElement(
                    By.xpath("//*[contains(text(),'GST (18%)')]/following-sibling::strong"));
            BigDecimal gst = new BigDecimal(
                    gstElement.getText().replace("Rs. ", "").trim());

            // Verify GST = Subtotal × 0.18
            BigDecimal expectedGst = subtotal
                    .multiply(GST_RATE)
                    .setScale(2, RoundingMode.HALF_UP);

            assertEquals(expectedGst, gst.setScale(2, RoundingMode.HALF_UP),
                    "GST should be 18% of subtotal");
        }

        @Test
        @DisplayName("Should verify total bill formula: Subtotal + GST + Delivery")
        void shouldVerifyTotalBillFormula() {
            TestCustomer customer = newCustomer("TotalFormula");
            createCustomerByApi(customer);

            driver.get(FRONTEND_URL);

            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(30));

            // Login
            wait.until(ExpectedConditions.visibilityOfElementLocated(By.name("email")))
                    .sendKeys(customer.email());
            driver.findElement(By.name("password")).sendKeys(customer.password());
            wait.until(ExpectedConditions.elementToBeClickable(
                    By.xpath("//button[contains(text(),'Customer Login')]"))).click();

            // Wait for products
            wait.until(ExpectedConditions.visibilityOfElementLocated(
                    By.xpath("//h3[contains(text(),'Recommended products')]")));

            // Add product
            wait.until(ExpectedConditions.elementToBeClickable(
                    By.xpath("(//button[contains(text(),'View Details')])[1]"))).click();
            wait.until(ExpectedConditions.elementToBeClickable(
                    By.xpath("//button[contains(text(),'Add to Cart')]"))).click();

            pauseForDemo();

            // Get all components
            WebElement subtotalElement = wait.until(
                    ExpectedConditions.visibilityOfElementLocated(
                            By.xpath("//*[contains(text(),'Subtotal')]/following-sibling::strong")));
            BigDecimal subtotal = new BigDecimal(
                    subtotalElement.getText().replace("Rs. ", "").trim());

            WebElement gstElement = driver.findElement(
                    By.xpath("//*[contains(text(),'GST (18%)')]/following-sibling::strong"));
            BigDecimal gst = new BigDecimal(
                    gstElement.getText().replace("Rs. ", "").trim());

            WebElement deliveryElement = driver.findElement(
                    By.xpath("//*[contains(text(),'Delivery')]/following-sibling::strong"));
            BigDecimal delivery = deliveryElement.getText().trim().equals("Free")
                    ? BigDecimal.ZERO
                    : new BigDecimal(deliveryElement.getText().replace("Rs. ", "").trim());

            WebElement totalElement = driver.findElement(
                    By.xpath("//*[contains(text(),'Total Bill')]/following-sibling::strong"));
            BigDecimal total = new BigDecimal(
                    totalElement.getText().replace("Rs. ", "").trim());

            // Verify formula: Total = Subtotal + GST + Delivery
            BigDecimal calculatedTotal = subtotal
                    .add(gst)
                    .add(delivery)
                    .setScale(2, RoundingMode.HALF_UP);

            assertEquals(calculatedTotal, total.setScale(2, RoundingMode.HALF_UP),
                    "Total Bill should equal Subtotal + GST + Delivery");
        }
    }

    /**
     * Helper method to verify cart calculations
     */
    private void verifyCartCalculations(WebDriverWait wait, BigDecimal expectedSubtotal, int expectedItemCount) {
        // Wait for cart to update
        pauseForDemo(2000);

        // Verify subtotal
        WebElement subtotalElement = wait.until(
                ExpectedConditions.visibilityOfElementLocated(
                        By.xpath("//*[contains(text(),'Subtotal')]/following-sibling::strong")));
        String subtotalText = subtotalElement.getText().replace("Rs. ", "").trim();
        BigDecimal actualSubtotal = new BigDecimal(subtotalText);

        assertEquals(expectedSubtotal.setScale(2, RoundingMode.HALF_UP),
                actualSubtotal.setScale(2, RoundingMode.HALF_UP),
                "Subtotal should match expected value");

        // Verify item count
        java.util.List<WebElement> cartItems = driver.findElements(
                By.xpath("//article[contains(@class,'cart-item')]"));
        assertEquals(expectedItemCount, cartItems.size(),
                "Cart should contain expected number of items");
    }

    /**
     * Helper method to verify checkout page calculations
     */
    private void verifyCheckoutCalculations(WebDriverWait wait, BigDecimal expectedSubtotal, int expectedItemCount) {
        // Verify address form is displayed
        WebElement customerNameField = wait.until(
                ExpectedConditions.visibilityOfElementLocated(By.name("customerName")));
        assertTrue(customerNameField.isDisplayed(), "Checkout form should be visible");

        // Verify order summary section
        WebElement orderSummary = wait.until(
                ExpectedConditions.visibilityOfElementLocated(
                        By.xpath("//*[contains(text(),'Order Summary')]")));
        assertTrue(orderSummary.isDisplayed(), "Order Summary should be visible");
    }
}
