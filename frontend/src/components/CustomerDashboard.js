import React from "react";
import CartSummary from "./CartSummary";
import ProductDetailPage from "./ProductDetailPage";
import ProductCatalog from "./ProductCatalog";

function CustomerDashboard({
  allProductsCount,
  availableCategories,
  cartDetails,
  cartItemCount,
  customerFilters,
  deliveryCharge,
  freeDeliveryThreshold,
  gstAmount,
  gstRate,
  hasActiveCustomerFilters,
  isLoadingProducts,
  onAddToCart,
  onCustomerFilterChange,
  onCloseProductDetails,
  onRemoveCartItem,
  onResetCustomerFilters,
  onSelectProduct,
  onUpdateCartQuantity,
  products,
  selectedProduct,
  subtotal,
  totalBill,
}) {
  // If a product is selected, show the full detail page
  if (selectedProduct) {
    return (
      <section>
        <ProductDetailPage
          onAddToCart={onAddToCart}
          onBack={onCloseProductDetails}
          product={selectedProduct}
        />
      </section>
    );
  }

  // Otherwise show the catalog view
  return (
    <section className="dashboard-grid customer-grid">
      <article className="panel spotlight-panel">
        <span className="section-kicker">Trending now</span>
        <h2>Curated products for your next order</h2>
        <p>
          Customers land directly into a shopping-style space with product cards,
          category details, and pricing instead of a plain text dashboard.
        </p>
      </article>

      <article className="panel metric-panel">
        <span>Matching Products</span>
        <strong>{products.length}</strong>
        <p>{allProductsCount} products are currently in your catalog.</p>
      </article>

      <article className="panel metric-panel">
        <span>Cart Items</span>
        <strong>{cartItemCount}</strong>
        <p>Total payable is Rs. {totalBill.toFixed(2)} right now.</p>
      </article>

      <article className="panel customer-filter-panel">
        <div className="section-title">
          <div>
            <span className="section-kicker">Find products faster</span>
            <h3>Search and filter</h3>
          </div>
        </div>

        <div className="customer-filters">
          <label className="filter-field filter-search">
            <span>Search</span>
            <input
              name="search"
              onChange={onCustomerFilterChange}
              placeholder="Search by name, description, or category"
              type="search"
              value={customerFilters.search}
            />
          </label>

          <label className="filter-field">
            <span>Category</span>
            <select name="category" onChange={onCustomerFilterChange} value={customerFilters.category}>
              <option value="all">All categories</option>
              {availableCategories.map((category) => (
                <option key={category} value={category}>
                  {category}
                </option>
              ))}
            </select>
          </label>

          <label className="filter-field">
            <span>Min Price</span>
            <input min="0" name="minPrice" onChange={onCustomerFilterChange} placeholder="0" type="number" value={customerFilters.minPrice} />
          </label>

          <label className="filter-field">
            <span>Max Price</span>
            <input min="0" name="maxPrice" onChange={onCustomerFilterChange} placeholder="5000" type="number" value={customerFilters.maxPrice} />
          </label>
        </div>

        <div className="filter-summary">
          <p>
            Showing {products.length} of {allProductsCount} products.
          </p>
          <button className="ghost-button" disabled={!hasActiveCustomerFilters} onClick={onResetCustomerFilters} type="button">
            Clear Filters
          </button>
        </div>
      </article>

      <ProductCatalog
        hasActiveFilters={hasActiveCustomerFilters}
        isLoadingProducts={isLoadingProducts}
        onSelectProduct={onSelectProduct}
        products={products}
        totalProductsCount={allProductsCount}
      />

      <CartSummary
        cartDetails={cartDetails}
        deliveryCharge={deliveryCharge}
        freeDeliveryThreshold={freeDeliveryThreshold}
        gstAmount={gstAmount}
        gstRate={gstRate}
        onRemoveCartItem={onRemoveCartItem}
        onUpdateCartQuantity={onUpdateCartQuantity}
        subtotal={subtotal}
        totalBill={totalBill}
      />
    </section>
  );
}

export default CustomerDashboard;
