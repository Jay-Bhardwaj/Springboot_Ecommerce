package com.example.demo.tests;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.example.demo.pages.ProductListingPage;

@DisplayName("Product Search and Filter Tests")
class ProductSearchAndFilterTest extends BaseTest {

    private ProductListingPage getProductListingPage() {
        return new ProductListingPage(driver);
    }

    private String getStableSearchTerm(ProductListingPage productListingPage) {
        String productName = productListingPage.getProductNameByIndex(0).trim();
        return productName.isEmpty() ? productListingPage.getProductCategoryByIndex(0) : productName;
    }

    @BeforeEach
    void resetCatalogFilters() {
        getProductListingPage().clearCustomerFilters();
    }

    @Test
    @DisplayName("1. Search Returns Matching Products")
    void testSearchReturnsMatchingProducts() {
        ProductListingPage productListingPage = getProductListingPage();
        String searchTerm = getStableSearchTerm(productListingPage);

        productListingPage.searchProduct(searchTerm);

        List<String> visibleNames = productListingPage.getAllProductNames();
        assertFalse(visibleNames.isEmpty(), "Search should show at least one matching product");
        assertTrue(
                visibleNames.stream().allMatch(name -> name.toLowerCase().contains(searchTerm.toLowerCase())),
                "Every visible product name should match the search term");
    }

    @Test
    @DisplayName("2. Category Filter Limits Results")
    void testCategoryFilterLimitsResults() {
        ProductListingPage productListingPage = getProductListingPage();
        String category = productListingPage.getProductCategoryByIndex(0);

        productListingPage.filterByCategory(category);

        List<String> visibleCategories = productListingPage.getAllProductCategories();
        assertFalse(visibleCategories.isEmpty(), "Category filter should show matching products");
        assertTrue(
                visibleCategories.stream().allMatch(visibleCategory -> visibleCategory.equals(category)),
                "All visible products should belong to the selected category");
    }

    @Test
    @DisplayName("3. Clear Filters Restores Full Catalog")
    void testClearFiltersRestoresCatalog() {
        ProductListingPage productListingPage = getProductListingPage();
        int baselineCount = productListingPage.getTotalProductsCount();
        String searchTerm = getStableSearchTerm(productListingPage);
        String category = productListingPage.getProductCategoryByIndex(0);

        productListingPage.searchProduct(searchTerm);
        productListingPage.filterByCategory(category);

        assertTrue(productListingPage.getTotalProductsCount() > 0, "Filtered catalog should still show products");

        productListingPage.clearCustomerFilters();

        assertEquals(baselineCount, productListingPage.getTotalProductsCount(),
                "Clearing filters should restore the full product catalog");
    }
}
