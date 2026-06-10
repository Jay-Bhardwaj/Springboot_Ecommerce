package com.example.demo.tests;

import org.junit.jupiter.api.Test;

import com.example.demo.pages.HomePage;

class smoketesting extends BaseTest {

    @Test
    void testAppLoads() {
        // Execute: Open the application
        HomePage homePage = new HomePage(driver);
        homePage.openApp();

        // Verify: App loaded (if no exception is thrown, test passes)
        System.out.println("Application loaded successfully");
    }
}
