package com.example.demo.tests;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.example.demo.pages.HomePage;
import com.example.demo.pages.RegisterPage;

class CustomerInputValidationTest extends BaseTest {
    private RegisterPage registerPage;

    @BeforeEach
    void openRegistrationPage() {
        HomePage homePage = new HomePage(driver);
        homePage.openApp();
        registerPage = homePage.clickCustomerRegister();
    }

    @Test
    void testEmailValidation() {
        // Test invalid email
        registerPage.typeByLabel("Email Address", "invalidemail");
        assertFalse(registerPage.isEmailInputValid(), "Email should be invalid");
        assertTrue(registerPage.getEmailValidationMessage().length() > 0, "Should show validation message");

        System.out.println("Email validation test passed");
    }

    @Test
    void testPasswordValidation() {
        // Test empty password
        registerPage.typeByLabel("Password", "");
        assertFalse(registerPage.isPasswordInputValid(), "Password should be invalid");
        assertTrue(registerPage.getPasswordValidationMessage().length() > 0, "Should show validation message");

        System.out.println("Password validation test passed");
    }

    @Test
    void testFullNameValidation() {
        // Test empty name
        registerPage.typeByLabel("Full Name", "");
        assertFalse(registerPage.isFullNameInputValid(), "Full Name should be invalid");
        assertTrue(registerPage.getFullNameValidationMessage().length() > 0, "Should show validation message");

        System.out.println("Full Name validation test passed");
    }
}
