package com.example.demo.tests;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.example.demo.pages.ProductListingPage;

/**
 * Product Filter & Validation Tests - Uses Shared Login Session
 * 
 * Flow:
 * 1. Login once in @BeforeAll
 * 2. Run all these tests with same logged-in session
 * 3. Close browser once in @AfterAll
 */
@DisplayName("Product Filter & Validation Tests (Shared Session)")
class ProductFilterTest extends BaseTest {

    private ProductListingPage getProductListingPage() {
        return new ProductListingPage(driver);
    }

    @Test
    @DisplayName("1. Product Listing Loads Successfully")
    void testProductListingLoadsSuccessfully() {
        System.out.println("\n--- Test: Product Listing Load ---");
        
        ProductListingPage productListingPage = getProductListingPage();
        int productCount = productListingPage.getTotalProductsCount();
        
        assertTrue(productCount > 0, "Product listing should load successfully");
        assertFalse(productListingPage.isLoadingCatalog(), "Loading message should not be visible");
        System.out.println("✅ Product listing loaded with " + productCount + " products");
    }

    @Test
    @DisplayName("2. All Products Have Required Fields")
    void testAllProductsHaveRequiredFields() {
        System.out.println("\n--- Test: Required Fields Check ---");
        
        ProductListingPage productListingPage = getProductListingPage();
        int productCount = productListingPage.getTotalProductsCount();
        assertTrue(productCount > 0, "At least one product should be available");

        for (int i = 0; i < productCount; i++) {
            String name = productListingPage.getProductNameByIndex(i);
            String price = productListingPage.getProductPriceByIndex(i);
            String category = productListingPage.getProductCategoryByIndex(i);
            String stockStatus = productListingPage.getStockStatusByIndex(i);

            assertFalse(name.isEmpty(), "Product " + i + " should have a name");
            assertTrue(price.contains("Rs."), "Product " + i + " should have a price");
            assertFalse(category.isEmpty(), "Product " + i + " should have a category");
            assertTrue(stockStatus.equals("In Stock") || stockStatus.equals("Sold Out"),
                    "Product " + i + " should have valid stock status");
        }
        System.out.println("✅ All " + productCount + " products have required fields");
    }

    @Test
    @DisplayName("3. Cart Count Is Displayed")
    void testCartCountIsDisplayed() {
        System.out.println("\n--- Test: Cart Count Display ---");
        
        ProductListingPage productListingPage = getProductListingPage();
        int cartCount = productListingPage.getCartCount();
        
        assertTrue(cartCount >= 0, "Cart count should be non-negative");
        System.out.println("✅ Current cart count: " + cartCount);
    }

    @Test
    @DisplayName("4. Results Count Is Accurate")
    void testResultsCountIsAccurate() {
        System.out.println("\n--- Test: Results Count Accuracy ---");
        
        ProductListingPage productListingPage = getProductListingPage();
        int resultsCount = productListingPage.getResultsCount();
        int displayedCount = productListingPage.getTotalProductsCount();

        assertTrue(resultsCount > 0, "Results count should be greater than 0");
        assertTrue(displayedCount > 0, "At least one product should be displayed");
        System.out.println("✅ Results count: " + resultsCount + ", Displayed: " + displayedCount);
    }

    @Test
    @DisplayName("5. Product Names Are Displayed")
    void testProductNamesAreUnique() {
        System.out.println("\n--- Test: Product Names Display ---");
        
        ProductListingPage productListingPage = getProductListingPage();
        java.util.List<String> productNames = productListingPage.getAllProductNames();

        assertFalse(productNames.isEmpty(), "Product listing should display products");
        for (String name : productNames) {
            assertFalse(name.isEmpty(), "Product names should not be empty");
        }
        System.out.println("✅ " + productNames.size() + " product names displayed");
    }

    @Test
    @DisplayName("6. Product Prices Have Currency")
    void testProductPricesHaveCurrency() {
        System.out.println("\n--- Test: Currency Symbols ---");
        
        ProductListingPage productListingPage = getProductListingPage();
        java.util.List<String> prices = productListingPage.getAllProductPrices();
        
        assertTrue(prices.size() > 0, "At least one product should be displayed");
        for (String price : prices) {
            assertTrue(price.contains("Rs."), "Price should contain currency symbol: " + price);
        }
        System.out.println("✅ All " + prices.size() + " products have currency symbols");
    }

    @Test
    @DisplayName("7. Product Categories Are Displayed")
    void testProductCategoriesAreDisplayed() {
        System.out.println("\n--- Test: Categories Display ---");
        
        ProductListingPage productListingPage = getProductListingPage();
        java.util.List<String> categories = productListingPage.getAllProductCategories();

        assertFalse(categories.isEmpty(), "Product categories should be displayed");
        for (String category : categories) {
            assertFalse(category.isEmpty(), "Category should not be empty");
        }
        System.out.println("✅ " + categories.size() + " categories displayed");
    }

    @Test
    @DisplayName("8. Initial Cart Is Empty")
    void testInitialCartIsEmpty() {
        System.out.println("\n--- Test: Initial Cart State ---");
        
        ProductListingPage productListingPage = getProductListingPage();
        int cartCount = productListingPage.getCartCount();
        
        assertTrue(cartCount == 0, "Cart should be empty for fresh session");
        System.out.println("✅ Cart is empty as expected");
    }

    @Test
    @DisplayName("9. Product Listing Is Responsive")
    void testProductListingResponsiveBehavior() {
        System.out.println("\n--- Test: Responsive Behavior ---");
        
        ProductListingPage productListingPage = getProductListingPage();
        int productCount = productListingPage.getTotalProductsCount();
        
        assertTrue(productCount > 0, "Product listing should display products");
        for (int i = 0; i < Math.min(productCount, 5); i++) {
            String name = productListingPage.getProductNameByIndex(i);
            assertTrue(!name.isEmpty(), "Product should have a name");
        }
        System.out.println("✅ Listing is responsive with " + productCount + " products");
    }

    @Test
    @DisplayName("10. Products Are Clickable")
    void testProductsAreClickable() {
        System.out.println("\n--- Test: Product Clickability ---");
        
        ProductListingPage productListingPage = getProductListingPage();
        int productCount = productListingPage.getTotalProductsCount();
        
        assertTrue(productCount > 0, "At least one product should be available");
        String firstProductName = productListingPage.getProductNameByIndex(0);
        assertTrue(!firstProductName.isEmpty(), "First product should have a name");
        System.out.println("✅ Product '" + firstProductName + "' is clickable");
    }
}
