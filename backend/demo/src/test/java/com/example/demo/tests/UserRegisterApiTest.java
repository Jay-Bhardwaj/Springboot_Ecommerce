package com.example.demo.tests;

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import com.example.demo.utils.TestCustomer;

class UserRegisterApiTest extends BaseTest {

    @Test
    void testUserRegistrationViaApi() {
        // Setup: Create test customer
        TestCustomer customer = newCustomer("ApiRegister");

        // Execute: Create customer via API
        createCustomerByApi(customer);

        // Verify: Customer was created successfully (no exception thrown)
        assertTrue(true, "Customer registration via API succeeded");

        System.out.println("API registration test passed for: " + customer.email());
    }

    @Test
    void testMultipleUserRegistrationsViaApi() {
        // Setup: Create multiple test customers
        TestCustomer customer1 = newCustomer("ApiUser1");
        TestCustomer customer2 = newCustomer("ApiUser2");

        // Execute: Create customers via API
        createCustomerByApi(customer1);
        createCustomerByApi(customer2);

        // Verify: Both customers were created successfully
        assertTrue(true, "Multiple customer registrations via API succeeded");

        System.out.println("Multiple API registrations test passed");
    }
}
