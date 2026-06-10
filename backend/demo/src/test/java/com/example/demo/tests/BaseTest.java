package com.example.demo.tests;

import static org.junit.jupiter.api.Assertions.fail;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.AfterEach;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

import com.example.demo.pages.HomePage;
import com.example.demo.pages.LoginPage;
import com.example.demo.pages.DashboardPage;
import com.example.demo.utils.TestCustomer;

/**
 * Optimized Base Test Class with Shared Session
 * - Logs in ONCE before all tests
 * - Reuses same WebDriver for all tests
 * - Closes browser ONCE after all tests
 * 
 * Backward compatible - works with both:
 * - @BeforeAll (for Product tests - shared session)
 * - @BeforeEach (for other tests - individual sessions)
 */
public class BaseTest {
    protected static final String FRONTEND_URL = System.getProperty("frontend.url", "http://localhost:3000");
    protected static final String BACKEND_URL = System.getProperty("backend.url", "http://localhost:8080");
    protected static final String CUSTOMER_PASSWORD = "Test@123";
    protected static final long CLOSE_WAIT_MS = Long.getLong("selenium.close.wait.ms", 1000L);
    protected static final long DEMO_WAIT_MS = Long.getLong("selenium.step.wait.ms", 500L);

    // Static WebDriver - shared across all test methods for @BeforeAll tests
    protected static WebDriver staticDriver;
    protected static TestCustomer staticTestCustomer;
    protected static boolean sharedSessionActive = false;

    // Instance WebDriver - for individual test methods
    protected WebDriver driver;

    /**
     * Setup: Run ONCE before ALL tests in the class (for shared session)
     * Used by ProductVisibilityTest and ProductFilterTest
     */
    @BeforeAll
    public static void setUpOnce() {
        System.out.println("\n========== SETTING UP SHARED TEST SESSION (Login Once) ==========\n");
        
        // Initialize browser
        setUpBrowserStatic();
        
        // Create test customer
        staticTestCustomer = createUniqueCustomer("SessionTest");
        
        // Register customer via API
        createCustomerByApiStatic(staticTestCustomer);
        
        // Login once - reuse this session for all tests
        loginCustomerStatic(staticTestCustomer);
        
        sharedSessionActive = true;
        System.out.println("✅ Shared test session setup complete.\n");
    }

    /**
     * Teardown: Run ONCE after ALL tests in the class
     * Used by ProductVisibilityTest and ProductFilterTest
     */
    @AfterAll
    public static void tearDownOnce() {
        System.out.println("\n========== CLOSING SHARED TEST SESSION ==========\n");
        closeBrowserStatic();
        sharedSessionActive = false;
        System.out.println("✅ Shared test session closed.\n");
    }

    /**
     * Setup: Run BEFORE EACH test (for individual tests)
     * Used by Customer tests, validation tests, etc.
     */
    @BeforeEach
    public void setUp() {
        // Only initialize if not using shared session
        if (!sharedSessionActive) {
            setUpBrowser();
        } else {
            // Reuse static driver for shared session
            driver = staticDriver;
        }
    }

    /**
     * Teardown: Run AFTER EACH test (for individual tests)
     * Used by Customer tests, validation tests, etc.
     */
    @AfterEach
    public void tearDown() {
        // Only close if not using shared session
        if (!sharedSessionActive) {
            closeBrowser();
        }
    }

    // ==================== STATIC METHODS (For @BeforeAll context) ====================

    /**
     * Initialize Chrome WebDriver (static)
     */
    private static void setUpBrowserStatic() {
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--start-maximized");
        options.addArguments("--disable-gpu");
        options.addArguments("--disable-dev-shm-usage");
        options.addArguments("--no-sandbox");
        options.addArguments("--disable-blink-features=AutomationControlled");
        options.addArguments("--disable-extensions");
        options.addArguments("--disable-plugins");
        
        staticDriver = new ChromeDriver(options);
        staticDriver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));
        staticDriver.manage().timeouts().pageLoadTimeout(Duration.ofSeconds(30));
        
        System.out.println("🔧 Chrome WebDriver initialized (Shared Session)");
    }

    /**
     * Close browser (static)
     */
    private static void closeBrowserStatic() {
        pauseForDemoStatic(CLOSE_WAIT_MS);
        if (staticDriver != null) {
            try {
                staticDriver.quit();
                System.out.println("🔧 Chrome WebDriver closed (Shared Session)");
            } catch (Exception e) {
                System.out.println("⚠️  Error closing browser: " + e.getMessage());
            }
        }
    }

    /**
     * Create a unique test customer (static)
     */
    private static TestCustomer createUniqueCustomer(String prefix) {
        String uniqueId = String.valueOf(System.currentTimeMillis());
        return new TestCustomer(
                prefix + " Test User",
                prefix.toLowerCase().replaceAll("[^a-z0-9]", ".") + "." + uniqueId + "@gmail.com",
                CUSTOMER_PASSWORD);
    }

    /**
     * Create a customer via API (static)
     */
    private static void createCustomerByApiStatic(TestCustomer customer) {
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
            System.out.println("✅ Test customer created: " + customer.email());
        } catch (IOException exception) {
            fail("Could not connect to backend at " + BACKEND_URL + ": " + exception.getMessage());
        } catch (InterruptedException exception) {
            Thread.currentThread().interrupt();
            fail("Customer API setup was interrupted.");
        }
    }

    /**
     * Login customer via UI (static)
     */
    private static void loginCustomerStatic(TestCustomer customer) {
        HomePage homePage = new HomePage(staticDriver);
        homePage.openApp();
        
        LoginPage loginPage = homePage.clickCustomerLogin();
        DashboardPage dashboardPage = loginPage.loginCustomer(customer);
        dashboardPage.assertDashboardVisible();
        
        pauseForDemoStatic(2000);
        System.out.println("✅ Customer logged in: " + customer.email());
    }

    /**
     * Pause (static)
     */
    private static void pauseForDemoStatic(long milliseconds) {
        try {
            Thread.sleep(milliseconds);
        } catch (InterruptedException exception) {
            Thread.currentThread().interrupt();
        }
    }

    // ==================== INSTANCE METHODS (For @BeforeEach context) ====================

    /**
     * Initialize Chrome WebDriver (instance)
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
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));
        driver.manage().timeouts().pageLoadTimeout(Duration.ofSeconds(30));
    }

    /**
     * Close browser (instance)
     */
    protected void closeBrowser() {
        pauseForDemo(CLOSE_WAIT_MS);
        if (driver != null) {
            try {
                driver.quit();
            } catch (Exception e) {
                System.out.println("⚠️  Error closing browser: " + e.getMessage());
            }
        }
    }

    /**
     * Create a new test customer with unique email (instance)
     */
    protected TestCustomer newCustomer(String prefix) {
        return createUniqueCustomer(prefix);
    }

    /**
     * Create a customer via API (instance)
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
     * Pause (instance)
     */
    protected void pauseForDemo(long milliseconds) {
        try {
            Thread.sleep(milliseconds);
        } catch (InterruptedException exception) {
            Thread.currentThread().interrupt();
        }
    }

    /**
     * Pause with default timing
     */
    protected void pauseForDemo() {
        pauseForDemo(DEMO_WAIT_MS);
    }
}
