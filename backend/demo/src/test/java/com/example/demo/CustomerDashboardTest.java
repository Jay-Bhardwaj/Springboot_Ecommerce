package com.example.demo;

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class CustomerDashboardTest extends SeleniumCustomerTestBase {

    @Test
    void customerDashboardShowsStoreContentAfterLogin() {
        TestCustomer customer = newCustomer("Dashboard");
        createCustomerByApi(customer);

        openApp();
        openCustomerLogin();
        loginCustomerFromUi(customer);
        assertCustomerDashboardVisible();

        String pageText = driver.findElement(org.openqa.selenium.By.tagName("body")).getText();
        assertTrue(pageText.contains("Logged in as CUSTOMER"), "Dashboard should show customer role.");
        assertTrue(pageText.contains("Recommended products") || pageText.contains("Cart Summary"),
                "Dashboard should show customer shopping content.");

        System.out.println("Customer dashboard test passed for: " + customer.email());
    }
}
