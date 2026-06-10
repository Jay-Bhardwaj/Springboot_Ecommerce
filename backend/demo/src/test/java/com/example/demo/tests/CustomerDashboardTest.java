package com.example.demo.tests;

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import com.example.demo.pages.HomePage;
import com.example.demo.utils.TestCustomer;

class CustomerDashboardTest extends BaseTest {

    @Test
    void customerDashboardShowsStoreContentAfterLogin() {
        // Setup: Create customer via API
        TestCustomer customer = newCustomer("Dashboard");
        createCustomerByApi(customer);

        // Execute: Open app and navigate to login
        HomePage homePage = new HomePage(driver);
        homePage.openApp();
        var loginPage = homePage.clickCustomerLogin();

        // Execute: Login via UI
        var dashboardPage = loginPage.loginCustomer(customer);

        // Verify: Dashboard is visible
        dashboardPage.assertDashboardVisible();
        assertTrue(dashboardPage.isCustomerRoleDisplayed(), "Dashboard should show customer role.");
        assertTrue(dashboardPage.areRecommendedProductsDisplayed() || dashboardPage.isCartSummaryDisplayed(),
                "Dashboard should show customer shopping content.");

        System.out.println("Customer dashboard test passed for: " + customer.email());
    }
}
