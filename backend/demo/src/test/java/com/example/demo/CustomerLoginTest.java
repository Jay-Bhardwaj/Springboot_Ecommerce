package com.example.demo;

import org.junit.jupiter.api.Test;

class CustomerLoginTest extends SeleniumCustomerTestBase {

    @Test
    void customerCanLoginWithValidCredentials() {
        TestCustomer customer = newCustomer("Login");
        createCustomerByApi(customer);

        openApp();
        openCustomerLogin();
        loginCustomerFromUi(customer);
        assertCustomerDashboardVisible();

        System.out.println("Customer login test passed for: " + customer.email());
    }
}
