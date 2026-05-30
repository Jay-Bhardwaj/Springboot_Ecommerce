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
  onProceedToCheckout,
  onRemoveCartItem,
  onResetCustomerFilters,
  onSelectProduct,
  onUpdateCartQuantity,
  products,
  selectedProduct,
  subtotal,
  totalBill,
}) {
  const curatedHighlights = [
    {
      title: "Fast moving deals",
      value: `${products.length} live`,
      description: "Popular products are easier to scan with larger imagery and clearer pricing.",
    },
    {
      title: "Category coverage",
      value: `${availableCategories.length} zones`,
      description: "Fashion, electronics, home and more can now feel like separate shopping lanes.",
    },
    {
      title: "Checkout ready",
      value: `${cartItemCount} in bag`,
      description: "Your cart remains visible so customers always know what they are about to buy.",
    },
  ];

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
      <article className="panel spotlight-panel spotlight-banner">
        <div className="spotlight-copy">
          <span className="section-kicker">Weekend spotlight</span>
          <h2>Style the homepage like a real shopping destination</h2>
          <p>
            This storefront now leans into marketplace patterns: promotional hero space, category-led discovery,
            stronger cards, and a cart that stays visible while browsing.
          </p>
        </div>
        <div className="spotlight-badges">
          <span>Free delivery over Rs. {freeDeliveryThreshold}</span>
          <span>Trending categories</span>
          <span>Fast checkout flow</span>
        </div>
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

      <article className="panel metric-panel">
        <span>Subtotal</span>
        <strong>Rs. {subtotal.toFixed(0)}</strong>
        <p>GST and delivery are shown clearly before the customer reaches checkout.</p>
      </article>

      <article className="panel deals-strip-panel full-width">
        <div className="deals-strip-header">
          <div>
            <span className="section-kicker">Why it feels better</span>
            <h3>Storefront highlights</h3>
          </div>
        </div>
        <div className="deals-strip">
          {curatedHighlights.map((item) => (
            <article className="deal-tile" key={item.title}>
              <span>{item.title}</span>
              <strong>{item.value}</strong>
              <p>{item.description}</p>
            </article>
          ))}
        </div>
      </article>

      <article className="panel customer-filter-panel">
        <div className="section-title">
          <div>
            <span className="section-kicker">Find products faster</span>
            <h3>Search and filter</h3>
          </div>
        </div>

        <div className="category-pill-row">
          <button
            className={`category-pill ${customerFilters.category === "all" ? "active" : ""}`}
            onClick={() => onCustomerFilterChange({ target: { name: "category", value: "all" } })}
            type="button"
          >
            All
          </button>
          {availableCategories.map((category) => (
            <button
              className={`category-pill ${customerFilters.category === category ? "active" : ""}`}
              key={category}
              onClick={() => onCustomerFilterChange({ target: { name: "category", value: category } })}
              type="button"
            >
              {category}
            </button>
          ))}
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
        cartItemCount={cartItemCount}
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
        onProceedToCheckout={onProceedToCheckout}
        onRemoveCartItem={onRemoveCartItem}
        onUpdateCartQuantity={onUpdateCartQuantity}
        subtotal={subtotal}
        totalBill={totalBill}
      />
    </section>
  );
}

export default CustomerDashboard;
