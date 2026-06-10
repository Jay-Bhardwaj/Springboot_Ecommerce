package com.example.demo.tests;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.example.demo.pages.HomePage;
import com.example.demo.pages.RegisterPage;

class UserServiceEmailValidationTest extends BaseTest {
    private RegisterPage registerPage;

    @BeforeEach
    void openRegistrationPage() {
        HomePage homePage = new HomePage(driver);
        homePage.openApp();
        registerPage = homePage.clickCustomerRegister();
    }

    @Test
    void testInvalidEmailFormat() {
        // Test: Invalid email formats
        String[] invalidEmails = {"invalidemail", "test@", "@example.com", "test..test@example.com"};

        for (String email : invalidEmails) {
            registerPage.typeByLabel("Email Address", email);
            assertFalse(registerPage.isEmailInputValid(), "Email should be invalid: " + email);
        }

        System.out.println("Invalid email format test passed");
    }

    @Test
    void testValidEmailFormat() {
        // Test: Valid email format
        registerPage.typeByLabel("Email Address", "valid@example.com");
        assertTrue(registerPage.isEmailInputValid(), "Email should be valid");

        System.out.println("Valid email format test passed");
    }

    @Test
    void testEmailValidationMessage() {
        // Test: Validation message appears
        registerPage.typeByLabel("Email Address", "invalidemail");
        String validationMessage = registerPage.getEmailValidationMessage();
        assertFalse(validationMessage.isEmpty(), "Validation message should not be empty");

        System.out.println("Email validation message test passed. Message: " + validationMessage);
    }
}
