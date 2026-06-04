package com.example.demo;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class CustomerInputValidationTest extends SeleniumCustomerTestBase {

    @Test
    void registrationRequiredFieldsShowBrowserValidation() {
        openApp();
        openCustomerRegister();

        clickButton("Register Customer");

        assertFalse(isInputValid("Full Name"), "Full Name should be required.");
        assertFalse(validationMessageFor("Full Name").isBlank(), "Full Name should show a validation message.");

        System.out.println("Registration required field validation verified.");
    }

    @Test
    void registrationRejectsInvalidEmailFormatBeforeSubmit() {
        openApp();
        openCustomerRegister();

        typeByLabel("Full Name", "Invalid Email User");
        typeByLabel("Email Address", "invalid-email");
        typeByLabel("Password", CUSTOMER_PASSWORD);
        clickButton("Register Customer");

        assertFalse(isInputValid("Email Address"), "Invalid email format should be blocked by browser validation.");
        assertFalse(validationMessageFor("Email Address").isBlank(), "Email Address should show a validation message.");

        System.out.println("Registration email format validation verified.");
    }

    @Test
    void registrationWeakPasswordShowsToastMessage() {
        TestCustomer customer = newCustomer("WeakPassword");

        openApp();
        openCustomerRegister();
        typeByLabel("Full Name", customer.name());
        typeByLabel("Email Address", customer.email());
        typeByLabel("Password", "passwordonly");
        clickButton("Register Customer");

        String toastText = toastMessage().getText();
        assertTrue(toastText.contains("Registration failed") || toastText.contains("Password"),
                "Weak password should show a registration/password validation toast.");

        System.out.println("Weak password validation toast verified: " + toastText);
    }
}
