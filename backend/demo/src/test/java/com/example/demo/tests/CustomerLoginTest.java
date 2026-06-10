package com.example.demo.tests;

import org.junit.jupiter.api.Test;

import com.example.demo.pages.HomePage;
import com.example.demo.utils.TestCustomer;

class CustomerLoginTest extends BaseTest {

    @Test
    void testCustomerLogin() {
        // Setup: Create customer via API
        TestCustomer customer = newCustomer("Login");
        createCustomerByApi(customer);

        // Execute: Navigate to login
        HomePage homePage = new HomePage(driver);
        homePage.openApp();
        var loginPage = homePage.clickCustomerLogin();

        // Execute: Login via UI
        var dashboardPage = loginPage.loginCustomer(customer);

        // Verify: Dashboard is visible
        dashboardPage.assertDashboardVisible();

        System.out.println("Customer login test passed for: " + customer.email());
    }
}
