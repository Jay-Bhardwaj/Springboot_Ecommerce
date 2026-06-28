package com.example.demo.tests;

import static org.junit.jupiter.api.Assertions.fail;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.time.Duration;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

import com.example.demo.utils.TestCustomer;

public class BaseTest {

    protected record AuthSession(String token, String name, String email, String role) {
    }

    protected record SeededProduct(long id, String name) {
    }

    protected static final String FRONTEND_URL =
            System.getProperty("frontend.url", "http://localhost:3000");

    protected static final String BACKEND_URL =
            System.getProperty("backend.url", "http://localhost:8080");

    protected static final String CUSTOMER_PASSWORD = "Test@123";

    protected static final long CLOSE_WAIT_MS =
            Long.getLong("selenium.close.wait.ms", 1000L);

    protected static final long DEMO_WAIT_MS =
            Long.getLong("selenium.step.wait.ms", 500L);

    private static final Pattern JSON_TOKEN_PATTERN =
            Pattern.compile("\"token\"\\s*:\\s*\"([^\"]+)\"");
    private static final Pattern JSON_NAME_PATTERN =
            Pattern.compile("\"name\"\\s*:\\s*\"([^\"]*)\"");
    private static final Pattern JSON_EMAIL_PATTERN =
            Pattern.compile("\"email\"\\s*:\\s*\"([^\"]*)\"");
    private static final Pattern JSON_ROLE_PATTERN =
            Pattern.compile("\"role\"\\s*:\\s*\"([^\"]+)\"");
    private static final Pattern JSON_ID_PATTERN =
            Pattern.compile("\"id\"\\s*:\\s*(\\d+)");

    protected WebDriver driver;

    /**
     * Runs before every test
     */
    @BeforeEach
    public void setUp() {
        setUpBrowser();
    }

    /**
     * Runs after every test
     */
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
        options.addArguments("--disable-gpu");
        options.addArguments("--disable-dev-shm-usage");
        options.addArguments("--no-sandbox");
        options.addArguments("--disable-blink-features=AutomationControlled");
        options.addArguments("--disable-extensions");
        options.addArguments("--disable-plugins");

        driver = new ChromeDriver(options);

        driver.manage().timeouts()
                .implicitlyWait(Duration.ofSeconds(10));

        driver.manage().timeouts()
                .pageLoadTimeout(Duration.ofSeconds(30));

        System.out.println("Chrome browser started");
    }

    /**
     * Close browser
     */
    protected void closeBrowser() {

        pauseForDemo(CLOSE_WAIT_MS);

        if (driver != null) {
            try {
                driver.quit();
                System.out.println("Chrome browser closed");
            } catch (Exception e) {
                System.out.println(
                        "Error closing browser: "
                                + e.getMessage());
            }
        }
    }

    /**
     * Create unique customer
     */
    protected TestCustomer newCustomer(String prefix) {

        String uniqueId =
                String.valueOf(System.currentTimeMillis());

        return new TestCustomer(
                prefix + " Test User",
                prefix.toLowerCase()
                        .replaceAll("[^a-z0-9]", ".")
                        + "."
                        + uniqueId
                        + "@gmail.com",
                CUSTOMER_PASSWORD);
    }

    /**
     * Create customer through API
     */
    protected void createCustomerByApi(TestCustomer customer) {

        String requestBody = """
                {
                  "name": "%s",
                  "email": "%s",
                  "password": "%s"
                }
                """.formatted(
                        escapeJson(customer.name()),
                        escapeJson(customer.email()),
                        escapeJson(customer.password()));

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(
                        BACKEND_URL + "/user/register"))
                .timeout(Duration.ofSeconds(20))
                .header("Content-Type",
                        "application/json")
                .POST(HttpRequest.BodyPublishers
                        .ofString(requestBody))
                .build();

        try {

            HttpResponse<String> response =
                    HttpClient.newHttpClient()
                            .send(
                                    request,
                                    HttpResponse.BodyHandlers
                                            .ofString());

            if (response.statusCode() >= 400) {

                fail(
                        "Could not create customer by API. Status: "
                                + response.statusCode()
                                + ", body: "
                                + response.body());
            }

        } catch (IOException exception) {

            fail(
                    "Could not connect to backend at "
                            + BACKEND_URL
                            + ": "
                            + exception.getMessage());

        } catch (InterruptedException exception) {

            Thread.currentThread().interrupt();

            fail(
                    "Customer API setup was interrupted.");
        }
    }

    /**
     * Login a user through the API and return the session payload.
     */
    protected AuthSession loginByApi(String email, String password) {

        String requestBody = """
                {
                  "email": "%s",
                  "password": "%s"
                }
                """.formatted(
                        escapeJson(email),
                        escapeJson(password));

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BACKEND_URL + "/user/login"))
                .timeout(Duration.ofSeconds(20))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                .build();

        try {
            HttpResponse<String> response = HttpClient.newHttpClient()
                    .send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() >= 400) {
                fail("Could not login through API. Status: " + response.statusCode() + ", body: " + response.body());
            }

            return new AuthSession(
                    extractJsonValue(JSON_TOKEN_PATTERN, response.body()),
                    extractJsonValue(JSON_NAME_PATTERN, response.body()),
                    extractJsonValue(JSON_EMAIL_PATTERN, response.body()),
                    extractJsonValue(JSON_ROLE_PATTERN, response.body()));
        } catch (IOException exception) {
            fail("Could not connect to backend at " + BACKEND_URL + ": " + exception.getMessage());
            return null;
        } catch (InterruptedException exception) {
            Thread.currentThread().interrupt();
            fail("API login was interrupted.");
            return null;
        }
    }

    /**
     * Create a product through the API using an authenticated admin session.
     */
    protected SeededProduct createProductByApi(
            AuthSession session,
            String name,
            String category,
            String price,
            String stockQuantity,
            String imageUrl,
            String description) {

        String requestBody = """
                {
                  "name": "%s",
                  "description": "%s",
                  "price": %s,
                  "category": "%s",
                  "imageUrl": "%s",
                  "stockQuantity": %s
                }
                """.formatted(
                        escapeJson(name),
                        escapeJson(description),
                        price,
                        escapeJson(category),
                        escapeJson(imageUrl),
                        stockQuantity);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BACKEND_URL + "/products"))
                .timeout(Duration.ofSeconds(20))
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer " + session.token())
                .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                .build();

        try {
            HttpResponse<String> response = HttpClient.newHttpClient()
                    .send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() >= 400) {
                fail("Could not create product through API. Status: " + response.statusCode() + ", body: " + response.body());
            }

            return new SeededProduct(
                    Long.parseLong(extractJsonValue(JSON_ID_PATTERN, response.body())),
                    name);
        } catch (IOException exception) {
            fail("Could not connect to backend at " + BACKEND_URL + ": " + exception.getMessage());
            return null;
        } catch (InterruptedException exception) {
            Thread.currentThread().interrupt();
            fail("Product API setup was interrupted.");
            return null;
        }
    }

    /**
     * Delete a product through the API using an authenticated admin session.
     */
    protected void deleteProductByApi(AuthSession session, long productId) {

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BACKEND_URL + "/products/" + productId))
                .timeout(Duration.ofSeconds(20))
                .header("Authorization", "Bearer " + session.token())
                .DELETE()
                .build();

        try {
            HttpResponse<String> response = HttpClient.newHttpClient()
                    .send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() >= 400) {
                fail("Could not delete product through API. Status: " + response.statusCode() + ", body: " + response.body());
            }
        } catch (IOException exception) {
            fail("Could not connect to backend at " + BACKEND_URL + ": " + exception.getMessage());
        } catch (InterruptedException exception) {
            Thread.currentThread().interrupt();
            fail("Product API cleanup was interrupted.");
        }
    }

    protected String escapeJson(String value) {
        return value.replace("\\", "\\\\").replace("\"", "\\\"");
    }

    protected String extractJsonValue(Pattern pattern, String body) {
        Matcher matcher = pattern.matcher(body);
        if (matcher.find()) {
            return matcher.group(1);
        }
        fail("Could not extract expected JSON value from response: " + body);
        return "";
    }

    /**
     * Pause helper
     */
    protected void pauseForDemo(long milliseconds) {

        try {
            Thread.sleep(milliseconds);
        } catch (InterruptedException exception) {
            Thread.currentThread().interrupt();
        }
    }

    /**
     * Default pause
     */
    protected void pauseForDemo() {
        pauseForDemo(DEMO_WAIT_MS);
    }
}
