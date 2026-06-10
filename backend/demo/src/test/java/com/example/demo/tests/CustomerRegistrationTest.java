package com.example.demo.tests;

import org.junit.jupiter.api.Test;

import com.example.demo.pages.HomePage;
import com.example.demo.utils.TestCustomer;

class CustomerRegistrationTest extends BaseTest {

    @Test
    void testCustomerRegistration() {
        // Setup: Create test customer
        TestCustomer customer = newCustomer("Register");

        // Execute: Navigate to registration
        HomePage homePage = new HomePage(driver);
        homePage.openApp();
        var registerPage = homePage.clickCustomerRegister();

        // Execute: Register via UI
        var dashboardPage = registerPage.registerCustomer(customer);

        // Verify: Dashboard is visible after registration
        dashboardPage.assertDashboardVisible();

        System.out.println("Customer registration test passed for: " + customer.email());
    }
}
