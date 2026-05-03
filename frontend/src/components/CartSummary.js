import React from "react";

function CartSummary({
  cartDetails,
  deliveryCharge,
  freeDeliveryThreshold,
  gstAmount,
  gstRate,
  onRemoveCartItem,
  onUpdateCartQuantity,
  subtotal,
  totalBill,
}) {
  return (
    <article className="panel cart-summary-panel">
      <div className="section-title">
        <div>
          <span className="section-kicker">Checkout snapshot</span>
          <h3>Cart Summary</h3>
        </div>
      </div>

      {cartDetails.length === 0 ? (
        <p className="empty-state">Your cart is empty. Open a product to add it here.</p>
      ) : (
        <>
          <div className="cart-list">
            {cartDetails.map((item) => (
              <article className="cart-item" key={item.productId}>
                <div>
                  <h4>{item.name}</h4>
                  <p>Rs. {Number(item.unitPrice || 0).toFixed(2)} each</p>
                </div>

                <div className="cart-item-actions">
                  <div className="quantity-picker compact">
                    <button
                      className="ghost-button quantity-button"
                      onClick={() => onUpdateCartQuantity(item.productId, item.quantity - 1)}
                      type="button"
                    >
                      -
                    </button>
                    <span>{item.quantity}</span>
                    <button
                      className="ghost-button quantity-button"
                      onClick={() => onUpdateCartQuantity(item.productId, item.quantity + 1)}
                      type="button"
                    >
                      +
                    </button>
                  </div>

                  <strong>Rs. {Number(item.lineTotal || 0).toFixed(2)}</strong>
                  <button className="table-link danger" onClick={() => onRemoveCartItem(item.productId)} type="button">
                    Remove
                  </button>
                </div>
              </article>
            ))}
          </div>

          <div className="bill-summary">
            <div>
              <span>Subtotal</span>
              <strong>Rs. {subtotal.toFixed(2)}</strong>
            </div>
            <div>
              <span>GST ({Math.round(gstRate * 100)}%)</span>
              <strong>Rs. {gstAmount.toFixed(2)}</strong>
            </div>
            <div>
              <span>Delivery</span>
              <strong>{deliveryCharge === 0 ? "Free" : `Rs. ${deliveryCharge.toFixed(2)}`}</strong>
            </div>
            <p className="delivery-note">
              Delivery is free from Rs. {freeDeliveryThreshold}. Orders below that add Rs. 40.
            </p>
            <div className="bill-total">
              <span>Total Bill</span>
              <strong>Rs. {totalBill.toFixed(2)}</strong>
            </div>
          </div>
        </>
      )}
    </article>
  );
}

export default CartSummary;
