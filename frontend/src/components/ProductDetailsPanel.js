import React, { useEffect, useState } from "react";

function ProductDetailsPanel({ onAddToCart, onBack, product }) {
  const [quantity, setQuantity] = useState(1);

  useEffect(() => {
    setQuantity(1);
  }, [product.id]);

  const availableStock = Number(product.stockQuantity || 0);

  const incrementQuantity = () => {
    setQuantity((current) => Math.min(current + 1, Math.max(availableStock, 1)));
  };

  const decrementQuantity = () => {
    setQuantity((current) => Math.max(current - 1, 1));
  };

  const handleQuantityInput = (event) => {
    const nextValue = Number(event.target.value || 1);
    setQuantity(Math.min(Math.max(nextValue, 1), Math.max(availableStock, 1)));
  };

  return (
    <article className="panel product-details-panel">
      <button className="ghost-button back-button" onClick={onBack} type="button">
        Back to catalog
      </button>

      <div className="product-details-layout">
        <div className="product-details-media">
          {product.imageUrl ? (
            <img alt={product.name} className="product-details-image" src={product.imageUrl} />
          ) : (
            <div className="product-details-placeholder">{product.category}</div>
          )}
        </div>

        <div className="product-details-copy">
          <span className="catalog-category">{product.category}</span>
          <h2>{product.name}</h2>
          <p>{product.description}</p>

          <div className="product-detail-meta">
            <div>
              <span>Price</span>
              <strong>Rs. {Number(product.price || 0).toFixed(2)}</strong>
            </div>
            
          </div>

          <div className="detail-cart-row">
            <div className="quantity-picker">
              <button className="ghost-button quantity-button" onClick={decrementQuantity} type="button">
                -
              </button>
              <input
                max={Math.max(availableStock, 1)}
                min="1"
                onChange={handleQuantityInput}
                type="number"
                value={quantity}
              />
              <button className="ghost-button quantity-button" onClick={incrementQuantity} type="button">
                +
              </button>
            </div>

            <button
              className="primary-button"
              disabled={availableStock <= 0}
              onClick={() => onAddToCart(product.id, quantity)}
              type="button"
            >
              {availableStock > 0 ? "Add to Cart" : "Out of Stock"}
            </button>
          </div>
        </div>
      </div>
    </article>
  );
}

export default ProductDetailsPanel;
