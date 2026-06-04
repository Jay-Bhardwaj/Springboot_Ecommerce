package com.example.demo;

import static org.junit.jupiter.api.Assertions.fail;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

abstract class SeleniumCustomerTestBase {

    protected static final String FRONTEND_URL = System.getProperty("frontend.url", "http://localhost:3000");
    protected static final String BACKEND_URL = System.getProperty("backend.url", "http://localhost:8080");
    protected static final String CUSTOMER_PASSWORD = "Test@123";
    protected static final long STEP_WAIT_MS = Long.getLong("selenium.step.wait.ms", 1000L);
    protected static final long CLOSE_WAIT_MS = Long.getLong("selenium.close.wait.ms", 3000L);

    protected WebDriver driver;
    protected WebDriverWait wait;

    @BeforeEach
    void setUpBrowser() {
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--start-maximized");

        driver = new ChromeDriver(options);
        wait = new WebDriverWait(driver, Duration.ofSeconds(20));
    }

    @AfterEach
    void closeBrowser() {
        pauseForDemo(CLOSE_WAIT_MS);

        if (driver != null) {
            driver.quit();
        }
    }

    protected TestCustomer newCustomer(String prefix) {
        String uniqueId = String.valueOf(System.currentTimeMillis());
        return new TestCustomer(
                prefix + " Test User",
                prefix.toLowerCase().replaceAll("[^a-z0-9]", ".") + "." + uniqueId + "@gmail.com",
                CUSTOMER_PASSWORD);
    }

    protected void openApp() {
        driver.get(FRONTEND_URL);
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.tagName("main")));
        pauseForDemo();
    }

    protected void openCustomerRegister() {
        clickButton("Customer Register");
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//h2[normalize-space()='Create customer account']")));
        pauseForDemo();
    }

    protected void openCustomerLogin() {
        clickButton("Customer Login");
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//h2[normalize-space()='Customer sign in']")));
        pauseForDemo();
    }

    protected void registerCustomerFromUi(TestCustomer customer) {
        typeByLabel("Full Name", customer.name());
        typeByLabel("Email Address", customer.email());
        typeByLabel("Password", customer.password());
        clickButton("Register Customer");
    }

    protected void loginCustomerFromUi(TestCustomer customer) {
        typeByLabel("Email Address", customer.email());
        typeByLabel("Password", customer.password());
        clickButton("Login as Customer");
    }

    protected void assertCustomerDashboardVisible() {
        wait.until(ExpectedConditions.or(
                ExpectedConditions.textToBePresentInElementLocated(By.tagName("body"), "Logged in as CUSTOMER"),
                ExpectedConditions.textToBePresentInElementLocated(By.tagName("body"), "Recommended products")));
        pauseForDemo();
    }

    protected void createCustomerByApi(TestCustomer customer) {
        String requestBody = """
                {
                  "name": "%s",
                  "email": "%s",
                  "password": "%s"
                }
                """.formatted(customer.name(), customer.email(), customer.password());

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BACKEND_URL + "/user/register"))
                .timeout(Duration.ofSeconds(20))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                .build();

        try {
            HttpResponse<String> response = HttpClient.newHttpClient()
                    .send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() >= 400) {
                fail("Could not create customer by API. Status: " + response.statusCode()
                        + ", body: " + response.body());
            }
        } catch (IOException exception) {
            fail("Could not connect to backend at " + BACKEND_URL + ": " + exception.getMessage());
        } catch (InterruptedException exception) {
            Thread.currentThread().interrupt();
            fail("Customer API setup was interrupted.");
        }
    }

    protected void clickButton(String text) {
        By locator = By.xpath("//button[normalize-space()='" + text + "']");
        wait.until(ExpectedConditions.elementToBeClickable(locator)).click();
        pauseForDemo();
    }

    protected void typeByLabel(String labelText, String value) {
        WebElement input = inputByLabel(labelText);
        input.clear();
        input.sendKeys(value);
        pauseForDemo();
    }

    protected WebElement inputByLabel(String labelText) {
        By locator = By.xpath("//label[.//span[normalize-space()='" + labelText + "']]//input");
        return wait.until(ExpectedConditions.visibilityOfElementLocated(locator));
    }

    protected WebElement toastMessage() {
        return wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector(".Toastify__toast")));
    }

    protected void assertToastContains(String expectedText) {
        wait.until(ExpectedConditions.textToBePresentInElementLocated(By.cssSelector(".Toastify__toast"), expectedText));
        pauseForDemo();
    }

    protected boolean isInputValid(String labelText) {
        return (Boolean) ((JavascriptExecutor) driver)
                .executeScript("return arguments[0].checkValidity();", inputByLabel(labelText));
    }

    protected String validationMessageFor(String labelText) {
        return (String) ((JavascriptExecutor) driver)
                .executeScript("return arguments[0].validationMessage;", inputByLabel(labelText));
    }

    protected void pauseForDemo() {
        pauseForDemo(STEP_WAIT_MS);
    }

    protected void pauseForDemo(long milliseconds) {
        try {
            Thread.sleep(milliseconds);
        } catch (InterruptedException exception) {
            Thread.currentThread().interrupt();
            fail("Test wait was interrupted.");
        }
    }

    protected record TestCustomer(String name, String email, String password) {
    }
}
