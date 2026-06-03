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
    }

    protected void openCustomerRegister() {
        clickButton("Customer Register");
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//h2[normalize-space()='Create customer account']")));
    }

    protected void openCustomerLogin() {
        clickButton("Customer Login");
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//h2[normalize-space()='Customer sign in']")));
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
    }

    protected void typeByLabel(String labelText, String value) {
        By locator = By.xpath("//label[.//span[normalize-space()='" + labelText + "']]//input");
        WebElement input = wait.until(ExpectedConditions.visibilityOfElementLocated(locator));
        input.clear();
        input.sendKeys(value);
    }

    protected record TestCustomer(String name, String email, String password) {
    }
}
