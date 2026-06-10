package com.example.demo.pages;

import com.example.demo.utils.TestCustomer;
import org.openqa.selenium.WebDriver;

public class RegisterPage extends BasePage {

    public RegisterPage(WebDriver driver) {
        super(driver);
    }

    /**
     * Register a customer using UI form
     */
    public DashboardPage registerCustomer(TestCustomer customer) {
        typeByLabel("Full Name", customer.name());
        typeByLabel("Email Address", customer.email());
        typeByLabel("Password", customer.password());
        clickButton("Register Customer");
        return new DashboardPage(driver);
    }

    /**
     * Type text into an input field by its label (exposed for tests)
     */
    public void typeByLabel(String labelText, String value) {
        super.typeByLabel(labelText, value);
    }

    /**
     * Check if email input is valid
     */
    public boolean isEmailInputValid() {
        return isInputValid("Email Address");
    }

    /**
     * Get validation message for email
     */
    public String getEmailValidationMessage() {
        return validationMessageFor("Email Address");
    }

    /**
     * Get validation message for password
     */
    public String getPasswordValidationMessage() {
        return validationMessageFor("Password");
    }

    /**
     * Check if full name input is valid
     */
    public boolean isFullNameInputValid() {
        return isInputValid("Full Name");
    }

    /**
     * Check if password input is valid
     */
    public boolean isPasswordInputValid() {
        return isInputValid("Password");
    }

    /**
     * Get full name validation message
     */
    public String getFullNameValidationMessage() {
        return validationMessageFor("Full Name");
    }
}
