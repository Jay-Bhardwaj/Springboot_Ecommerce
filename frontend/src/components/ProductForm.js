import React from "react";

function ProductForm({ editingProductId, isSavingProduct, onChange, onReset, onSubmit, productForm }) {
  return (
    <article className="panel product-form-panel">
      <div className="section-title">
        <div>
          <span className="section-kicker">Product Editor</span>
          <h3>{editingProductId ? "Update product" : "Add a new product"}</h3>
        </div>
      </div>

      <form className="product-form" onSubmit={onSubmit}>
        <label>
          <span>Product Name</span>
          <input name="name" onChange={onChange} placeholder="Premium Sneakers" required type="text" value={productForm.name} />
        </label>

        <label>
          <span>Category</span>
          <input name="category" onChange={onChange} placeholder="Footwear" required type="text" value={productForm.category} />
        </label>

        <label>
          <span>Price</span>
          <input min="0" name="price" onChange={onChange} placeholder="2499" required step="0.01" type="number" value={productForm.price} />
        </label>

        <label>
          <span>Stock Quantity</span>
          <input min="0" name="stockQuantity" onChange={onChange} placeholder="25" required type="number" value={productForm.stockQuantity} />
        </label>

        <label className="full-width">
          <span>Image URL</span>
          <input name="imageUrl" onChange={onChange} placeholder="https://images.unsplash.com/..." type="url" value={productForm.imageUrl} />
        </label>

        <label className="full-width">
          <span>Description</span>
          <textarea name="description" onChange={onChange} placeholder="Describe the product, quality, fit, or features" required rows="4" value={productForm.description} />
        </label>

        <div className="product-form-actions full-width">
          <button className="primary-button" disabled={isSavingProduct} type="submit">
            {isSavingProduct ? "Saving..." : editingProductId ? "Update Product" : "Create Product"}
          </button>
          <button className="ghost-button" onClick={onReset} type="button">
            Reset Form
          </button>
        </div>
      </form>
    </article>
  );
}

export default ProductForm;
