package com.example.demo;

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.support.ui.ExpectedConditions;

class CustomerToastAndPopupTest extends SeleniumCustomerTestBase {

    @Test
    void invalidCustomerLoginShowsErrorToast() {
        TestCustomer customer = newCustomer("Toast");
        createCustomerByApi(customer);

        openApp();
        openCustomerLogin();
        typeByLabel("Email Address", customer.email());
        typeByLabel("Password", "Wrong@123");
        clickButton("Login as Customer");

        assertToastContains("Invalid credentials");

        System.out.println("Invalid login toast message verified.");
    }

    @Test
    void customerProfilePopupOpensAndLogoutShowsToast() {
        TestCustomer customer = newCustomer("Popup");
        createCustomerByApi(customer);

        openApp();
        openCustomerLogin();
        loginCustomerFromUi(customer);
        assertCustomerDashboardVisible();

        wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("//button[contains(., 'Profile') and contains(., '" + customer.name() + "')]"))).click();
        pauseForDemo();

        assertTrue(driver.getPageSource().contains("About Profile"), "Profile popup should show About Profile option.");
        assertTrue(driver.getPageSource().contains("My Orders"), "Profile popup should show My Orders option.");
        assertTrue(driver.getPageSource().contains("Logout"), "Profile popup should show Logout option.");

        wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("//button[contains(., 'Logout') and contains(., 'Sign out')]"))).click();

        assertToastContains("Logged out");

        System.out.println("Profile popup and logout toast verified.");
    }
}
