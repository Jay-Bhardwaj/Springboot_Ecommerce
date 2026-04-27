import React from "react";
import ProductForm from "./ProductForm";
import ProductTable from "./ProductTable";

function AdminDashboard({ editingProductId, isLoadingProducts, isSavingProduct, onDeleteProduct, onEditProduct, onProductInputChange, onResetProductForm, onSaveProduct, productForm, products }) {
  const totalInventory = products.reduce((sum, product) => sum + Number(product.stockQuantity || 0), 0);

  return (
    <section className="dashboard-grid">
      <article className="panel metric-panel">
        <span>Total Products</span>
        <strong>{products.length}</strong>
        <p>Manage catalog count across your store.</p>
      </article>

      <article className="panel metric-panel">
        <span>Total Units</span>
        <strong>{totalInventory}</strong>
        <p>Live stock from your managed products.</p>
      </article>

      <article className="panel metric-panel">
        <span>Workspace</span>
        <strong>Admin</strong>
        <p>Create, edit, and delete inventory securely.</p>
      </article>

      <ProductForm
        editingProductId={editingProductId}
        isSavingProduct={isSavingProduct}
        onChange={onProductInputChange}
        onReset={onResetProductForm}
        onSubmit={onSaveProduct}
        productForm={productForm}
      />

      <ProductTable
        isLoadingProducts={isLoadingProducts}
        onDelete={onDeleteProduct}
        onEdit={onEditProduct}
        products={products}
      />
    </section>
  );
}

export default AdminDashboard;
