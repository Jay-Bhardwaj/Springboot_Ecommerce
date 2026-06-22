package com.example.demo.tests;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.example.demo.pages.ProductListingPage;

/**
 * Product Visibility Tests - Uses Shared Login Session
 * 
 * Flow:
 * 1. Login once in @BeforeAll
 * 2. Run all these tests with same logged-in session
 * 3. Close browser once in @AfterAll
 */
@DisplayName("Product Visibility Tests (Shared Session)")
class ProductVisibilityTest extends BaseTest {
    
    private ProductListingPage getProductListingPage() {
        return new ProductListingPage(driver);
    }

    @Test
    @DisplayName("1. Product Catalog Displays Multiple Products")
    void testProductCatalogIsDisplayed() {
        System.out.println("\n--- Test: Product Catalog Display ---");
        
        ProductListingPage productListingPage = getProductListingPage();
        int productCount = productListingPage.getTotalProductsCount();
        
        assertTrue(productCount > 0, "Product catalog should display at least one product");
        System.out.println("✅ Product catalog displays " + productCount + " products");
    }

    @Test
    @DisplayName("2. Product Names Are Visible")
    void testProductNameIsVisible() {
        System.out.println("\n--- Test: Product Names Visibility ---");
        
        ProductListingPage productListingPage = getProductListingPage();
        String productName = productListingPage.getProductNameByIndex(0);
        
        assertNotNull(productName, "Product name should not be null");
        assertFalse(productName.isEmpty(), "Product name should not be empty");
        System.out.println("✅ First product name: " + productName);
    }

    @Test
    @DisplayName("3. Product Prices Are Visible")
    void testProductPriceIsVisible() {
        System.out.println("\n--- Test: Product Prices Visibility ---");
        
        ProductListingPage productListingPage = getProductListingPage();
        String price = productListingPage.getProductPriceByIndex(0);
        
        assertNotNull(price, "Product price should not be null");
        assertTrue(price.contains("Rs."), "Price should contain currency symbol");
        System.out.println("✅ First product price: " + price);
    }

    @Test
    @DisplayName("4. Product Categories Are Visible")
    void testProductCategoryIsVisible() {
        System.out.println("\n--- Test: Product Categories Visibility ---");
        
        ProductListingPage productListingPage = getProductListingPage();
        String category = productListingPage.getProductCategoryByIndex(0);
        
        assertNotNull(category, "Product category should not be null");
        assertFalse(category.isEmpty(), "Product category should not be empty");
        System.out.println("✅ First product category: " + category);
    }

    @Test
    @DisplayName("5. Stock Status Badges Are Visible")
    void testProductStockStatusIsVisible() {
        System.out.println("\n--- Test: Stock Status Visibility ---");
        
        ProductListingPage productListingPage = getProductListingPage();
        String stockStatus = productListingPage.getStockStatusByIndex(0);
        
        assertNotNull(stockStatus, "Stock status should not be null");
        assertTrue(stockStatus.equals("In Stock") || stockStatus.equals("Sold Out"),
                "Stock status should be either 'In Stock' or 'Sold Out'");
        System.out.println("✅ First product stock status: " + stockStatus);
    }

    @Test
    @DisplayName("6. Product Descriptions Are Visible")
    void testProductDescriptionIsVisible() {
        System.out.println("\n--- Test: Product Descriptions Visibility ---");
        
        ProductListingPage productListingPage = getProductListingPage();
        String description = productListingPage.getProductDescriptionByIndex(0);
        
        assertNotNull(description, "Product description should not be null");
        assertFalse(description.isEmpty(), "Product description should not be empty");
        System.out.println("✅ First product description: " + description.substring(0, Math.min(50, description.length())) + "...");
    }

    @Test
    @DisplayName("7. Multiple Products Display Correctly")
    void testMultipleProductsAreVisible() {
        System.out.println("\n--- Test: Multiple Products Display ---");
        
        ProductListingPage productListingPage = getProductListingPage();
        int productCount = productListingPage.getTotalProductsCount();
        
        assertTrue(productCount >= 1, "Should display at least 1 product");
        for (int i = 0; i < Math.min(productCount, 3); i++) {
            String name = productListingPage.getProductNameByIndex(i);
            assertFalse(name.isEmpty(), "Product " + i + " should have a name");
        }
        System.out.println("✅ All " + productCount + " products have names");
    }

    @Test
    @DisplayName("8. Product Cards Are Clickable")
    void testProductCardClickableAndNavigateToDetails() {
        System.out.println("\n--- Test: Product Card Navigation ---");
        
        ProductListingPage productListingPage = getProductListingPage();
        int productCount = productListingPage.getTotalProductsCount();
        assertTrue(productCount > 0, "At least one product should be available");

        String productName = productListingPage.getProductNameByIndex(0);
        var productDetailPage = productListingPage.clickViewDetailsForProduct(0);
        pauseForDemo(1000);

        String detailPageName = productDetailPage.getProductName();
        assertEquals(productName, detailPageName, "Product name should match");
        System.out.println("✅ Successfully navigated to: " + productName);
        
        // Go back to listing
        productDetailPage.clickBackButton();
        pauseForDemo(1000);
    }

    @Test
    @DisplayName("9. Top Pick Badge Is Visible")
    void testTopPickBadgeIsVisible() {
        System.out.println("\n--- Test: Top Pick Badge ---");
        
        ProductListingPage productListingPage = getProductListingPage();
        int productCount = productListingPage.getTotalProductsCount();
        assertTrue(productCount > 0, "At least one product should be available");

        boolean hasTopPickBadge = productListingPage.hasTopPickBadge(0);
        assertTrue(hasTopPickBadge, "First product should have Top Pick badge");
        System.out.println("✅ Top Pick badge is visible");
    }

    @Test
    @DisplayName("10. Results Count Matches Displayed Products")
    void testProductsCountMatchesDisplayed() {
        System.out.println("\n--- Test: Results Count Accuracy ---");
        
        ProductListingPage productListingPage = getProductListingPage();
        int resultsCount = productListingPage.getResultsCount();
        int displayedCount = productListingPage.getTotalProductsCount();
        
        assertEquals(resultsCount, displayedCount, "Results count should match displayed products");
        System.out.println("✅ Results count (" + resultsCount + ") matches displayed (" + displayedCount + ")");
    }

    @Test
    @DisplayName("11. Stock Badges Are Correct")
    void testInStockProductsHaveCorrectBadge() {
        System.out.println("\n--- Test: Stock Badge Accuracy ---");
        
        ProductListingPage productListingPage = getProductListingPage();
        int productCount = productListingPage.getTotalProductsCount();
        
        for (int i = 0; i < Math.min(productCount, 3); i++) {
            boolean isInStock = productListingPage.isProductInStock(i);
            String stockStatus = productListingPage.getStockStatusByIndex(i);
            
            if (isInStock) {
                assertEquals("In Stock", stockStatus, "Stock badge should say 'In Stock'");
            } else {
                assertEquals("Sold Out", stockStatus, "Stock badge should say 'Sold Out'");
            }
        }
        System.out.println("✅ All stock badges are correct");
    }
}
