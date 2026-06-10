package com.example.demo.tests;

import static org.junit.jupiter.api.Assertions.fail;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

import com.example.demo.utils.TestCustomer;

public class BaseTest {
    protected static final String FRONTEND_URL = System.getProperty("frontend.url", "http://localhost:3000");
    protected static final String BACKEND_URL = System.getProperty("backend.url", "http://localhost:8080");
    protected static final String CUSTOMER_PASSWORD = "Test@123";
    protected static final long CLOSE_WAIT_MS = Long.getLong("selenium.close.wait.ms", 3000L);

    protected WebDriver driver;

    @BeforeEach
    public void setUp() {
        setUpBrowser();
    }

    @AfterEach
    public void tearDown() {
        closeBrowser();
    }

    /**
     * Initialize Chrome WebDriver
     */
    protected void setUpBrowser() {
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--start-maximized");
        driver = new ChromeDriver(options);
    }

    /**
     * Close the browser
     */
    protected void closeBrowser() {
        pauseForDemo(CLOSE_WAIT_MS);
        if (driver != null) {
            driver.quit();
        }
    }

    /**
     * Create a new test customer with unique email
     */
    protected TestCustomer newCustomer(String prefix) {
        String uniqueId = String.valueOf(System.currentTimeMillis());
        return new TestCustomer(
                prefix + " Test User",
                prefix.toLowerCase().replaceAll("[^a-z0-9]", ".") + "." + uniqueId + "@gmail.com",
                CUSTOMER_PASSWORD);
    }

    /**
     * Create a customer via API
     */
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

    /**
     * Pause for demo purposes
     */
    protected void pauseForDemo(long milliseconds) {
        try {
            Thread.sleep(milliseconds);
        } catch (InterruptedException exception) {
            Thread.currentThread().interrupt();
            fail("Test wait was interrupted.");
        }
    }
}
