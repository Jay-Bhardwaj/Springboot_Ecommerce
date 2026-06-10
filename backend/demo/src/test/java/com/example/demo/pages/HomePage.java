package com.example.demo.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;

public class HomePage extends BasePage {
    private static final String FRONTEND_URL = System.getProperty("frontend.url", "http://localhost:3000");

    public HomePage(WebDriver driver) {
        super(driver);
    }

    /**
     * Open the application
     */
    public void openApp() {
        driver.get(FRONTEND_URL);
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.tagName("main")));
        pauseForDemo();
    }

    /**
     * Click on Customer Register button
     */
    public RegisterPage clickCustomerRegister() {
        clickButton("Customer Register");
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//h2[normalize-space()='Create customer account']"));
        pauseForDemo();
        return new RegisterPage(driver);
    }

    /**
     * Click on Customer Login button
     */
    public LoginPage clickCustomerLogin() {
        clickButton("Customer Login");
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//h2[normalize-space()='Customer sign in']"));
        pauseForDemo();
        return new LoginPage(driver);
    }
}
