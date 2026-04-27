import React from "react";

function ProductCatalog({ isLoadingProducts, products }) {
  if (isLoadingProducts) {
    return <article className="panel catalog-panel"><p className="empty-state">Loading catalog...</p></article>;
  }

  if (products.length === 0) {
    return (
      <article className="panel catalog-panel">
        <p className="empty-state">No products are available yet. Add some as admin first.</p>
      </article>
    );
  }

  return (
    <section className="catalog-grid catalog-panel">
      {products.map((product) => (
        <article className="catalog-card" key={product.id}>
          <div className="catalog-image-wrap">
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
            <div className="catalog-footer">
              <strong>Rs. {product.price}</strong>
              <span>{product.stockQuantity} in stock</span>
            </div>
          </div>
        </article>
      ))}
    </section>
  );
}

export default ProductCatalog;
