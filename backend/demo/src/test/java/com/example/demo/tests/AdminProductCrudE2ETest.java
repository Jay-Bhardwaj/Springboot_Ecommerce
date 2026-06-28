package com.example.demo.tests;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.example.demo.pages.AdminDashboardPage;
import com.example.demo.pages.AdminLoginPage;

public class AdminProductCrudE2ETest extends BaseTest {

    @Test
    @DisplayName("Should login as admin, create, edit, delete a product, and logout")
    void shouldPerformAdminCrudAndLogout() {
        String runId = String.valueOf(System.currentTimeMillis());
        String originalProductName = "Codex QA Product " + runId;
        String updatedProductName = originalProductName + " Updated";
        String originalCategory = "Testing";
        String updatedCategory = "Regression";

        AdminLoginPage loginPage = new AdminLoginPage(driver);
        driver.get(FRONTEND_URL);

        AdminDashboardPage dashboardPage = loginPage.loginAdmin("admin@shop.com", "Admin@123");
        dashboardPage.assertDashboardVisible();

        dashboardPage.createProduct(
                originalProductName,
                originalCategory,
                "1499",
                "12",
                "https://images.unsplash.com/photo-1523275335684-37898b6baf30",
                "Automated admin test product created by Selenium.");
        dashboardPage.waitForProductRow(originalProductName);

        dashboardPage.clickEditProduct(originalProductName);
        dashboardPage.fillProductForm(
                updatedProductName,
                updatedCategory,
                "1599",
                "9",
                "https://images.unsplash.com/photo-1512436991641-6745cdb1723f",
                "Updated product details from the admin CRUD test.");
        dashboardPage.updateProduct();

        dashboardPage.waitForProductRow(updatedProductName);
        dashboardPage.waitForProductToDisappear(originalProductName);

        dashboardPage.clickDeleteProduct(updatedProductName);
        dashboardPage.waitForProductToDisappear(updatedProductName);

        dashboardPage.logout();
        dashboardPage.assertAdminLoginVisible();
    }
}
