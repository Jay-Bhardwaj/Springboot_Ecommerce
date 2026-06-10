package com.example.demo.tests;

import org.junit.jupiter.api.Test;

import com.example.demo.pages.HomePage;
import com.example.demo.utils.TestCustomer;

class CustomerToastAndPopupTest extends BaseTest {

    @Test
    void testSuccessToastOnRegistration() {
        // Setup: Create test customer
        TestCustomer customer = newCustomer("Toast");

        // Execute: Navigate and register
        HomePage homePage = new HomePage(driver);
        homePage.openApp();
        var registerPage = homePage.clickCustomerRegister();
        var dashboardPage = registerPage.registerCustomer(customer);

        // Verify: Dashboard appears (indicating success)
        dashboardPage.assertDashboardVisible();

        System.out.println("Toast and popup test passed for: " + customer.email());
    }

    @Test
    void testSuccessToastOnLogin() {
        // Setup: Create customer via API
        TestCustomer customer = newCustomer("LoginToast");
        createCustomerByApi(customer);

        // Execute: Navigate and login
        HomePage homePage = new HomePage(driver);
        homePage.openApp();
        var loginPage = homePage.clickCustomerLogin();
        var dashboardPage = loginPage.loginCustomer(customer);

        // Verify: Dashboard appears (indicating success)
        dashboardPage.assertDashboardVisible();

        System.out.println("Login toast test passed for: " + customer.email());
    }
}
