package com.example.demo.pages;

import com.example.demo.utils.TestCustomer;
import org.openqa.selenium.WebDriver;

public class LoginPage extends BasePage {

    public LoginPage(WebDriver driver) {
        super(driver);
    }

    /**
     * Login a customer using UI form
     */
    public DashboardPage loginCustomer(TestCustomer customer) {
        typeByLabel("Email Address", customer.email());
        typeByLabel("Password", customer.password());
        clickButton("Login as Customer");
        return new DashboardPage(driver);
    }

    /**
     * Enter email address
     */
    public void enterEmail(String email) {
        typeByLabel("Email Address", email);
    }

    /**
     * Enter password
     */
    public void enterPassword(String password) {
        typeByLabel("Password", password);
    }

    /**
     * Click login button
     */
    public void clickLoginButton() {
        clickButton("Login as Customer");
    }
}
