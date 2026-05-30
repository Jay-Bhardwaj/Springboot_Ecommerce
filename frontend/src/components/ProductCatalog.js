import React from "react";

function ProductCatalog({ cartItemCount, hasActiveFilters, isLoadingProducts, onSelectProduct, products, totalProductsCount }) {
  if (isLoadingProducts) {
    return <article className="panel catalog-panel"><p className="empty-state">Loading catalog...</p></article>;
  }

  if (products.length === 0) {
    return (
      <article className="panel catalog-panel">
        <p className="empty-state">
          {totalProductsCount === 0
            ? "No products are available yet. Add some as admin first."
            : hasActiveFilters
              ? "No products match the current search and filters."
              : "No products are available yet. Add some as admin first."}
        </p>
      </article>
    );
  }

  return (
    <section className="catalog-panel catalog-shell">
      <div className="catalog-toolbar">
        <div>
          <span className="section-kicker">Shop the feed</span>
          <h3>Recommended products</h3>
          <p>Designed with bigger cards, clearer offers, and stronger visual hierarchy.</p>
        </div>
        <div className="catalog-toolbar-meta">
          <span>{products.length} results</span>
          <span>{cartItemCount} in cart</span>
        </div>
      </div>

      <div className="catalog-grid">
        {products.map((product) => (
          <article className="catalog-card" key={product.id}>
            <div className="catalog-image-wrap">
              <div className="catalog-card-badges">
                <span className="offer-badge">Top Pick</span>
                <span className={`stock-badge ${Number(product.stockQuantity || 0) > 0 ? "in-stock" : "out-stock"}`}>
                  {Number(product.stockQuantity || 0) > 0 ? "In Stock" : "Sold Out"}
                </span>
              </div>
              {product.imageUrl ? (
                <img alt={product.name} className="catalog-image" src={product.imageUrl} />
              ) : (
                <div className="catalog-image placeholder">{product.category}</div>
              )}
            </div>
            <div className="catalog-content">
              <span className="catalog-category">{product.category}</span>
              <h3>{product.name}</h3>
              <p>{product.description}</p>
              <div className="catalog-rating-row">
                <strong className="catalog-price">Rs. {Number(product.price || 0).toFixed(2)}</strong>
                <span>{(4 + (product.id % 10) / 10).toFixed(1)} rating</span>
              </div>
              <div className="catalog-footer">
                <span>{Number(product.stockQuantity || 0)} units available</span>
                <span>Delivery in 2-4 days</span>
              </div>
              <button className="primary-button catalog-action" onClick={() => onSelectProduct(product.id)} type="button">
                View Details
              </button>
            </div>
          </article>
        ))}
      </div>
    </section>
  );
}

export default ProductCatalog;
