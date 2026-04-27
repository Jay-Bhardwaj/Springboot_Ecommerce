import React from "react";

function ProductTable({ isLoadingProducts, onDelete, onEdit, products }) {
  return (
    <article className="panel product-table-panel">
      <div className="section-title">
        <div>
          <span className="section-kicker">Inventory</span>
          <h3>Product List</h3>
        </div>
      </div>

      {isLoadingProducts ? <p className="empty-state">Loading products...</p> : null}

      {!isLoadingProducts && products.length === 0 ? (
        <p className="empty-state">No products added yet. Create your first ecommerce product.</p>
      ) : null}

      {!isLoadingProducts && products.length > 0 ? (
        <div className="product-table-wrapper">
          <table className="product-table">
            <thead>
              <tr>
                <th>Name</th>
                <th>Category</th>
                <th>Price</th>
                <th>Stock</th>
                <th>Actions</th>
              </tr>
            </thead>
            <tbody>
              {products.map((product) => (
                <tr key={product.id}>
                  <td>{product.name}</td>
                  <td>{product.category}</td>
                  <td>Rs. {product.price}</td>
                  <td>{product.stockQuantity}</td>
                  <td>
                    <div className="table-actions">
                      <button className="table-link" onClick={() => onEdit(product)} type="button">
                        Edit
                      </button>
                      <button className="table-link danger" onClick={() => onDelete(product.id)} type="button">
                        Delete
                      </button>
                    </div>
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      ) : null}
    </article>
  );
}

export default ProductTable;
