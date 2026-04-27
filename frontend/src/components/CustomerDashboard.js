import React from "react";
import ProductCatalog from "./ProductCatalog";

function CustomerDashboard({ isLoadingProducts, products }) {
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
        <span>Available Products</span>
        <strong>{products.length}</strong>
        <p>Fresh items visible in your customer catalog.</p>
      </article>

      <ProductCatalog isLoadingProducts={isLoadingProducts} products={products} />
    </section>
  );
}

export default CustomerDashboard;
