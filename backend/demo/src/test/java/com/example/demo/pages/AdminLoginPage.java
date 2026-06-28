package com.example.demo.pages;

import org.openqa.selenium.WebDriver;

public class AdminLoginPage extends BasePage {

    public AdminLoginPage(WebDriver driver) {
        super(driver);
    }

    /**
     * Ensure the admin login tab is active.
     */
    public void openAdminLoginTab() {
        clickButton("Admin Login");
    }

    /**
     * Login as an admin user.
     */
    public AdminDashboardPage loginAdmin(String email, String password) {
        openAdminLoginTab();
        typeByLabel("Email Address", email);
        typeByLabel("Password", password);
        clickButton("Login as Admin");
        return new AdminDashboardPage(driver);
    }
}
