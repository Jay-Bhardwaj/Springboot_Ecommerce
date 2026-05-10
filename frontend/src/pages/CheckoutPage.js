import React from "react";
import "../styles/checkout.css";

function CheckoutPage({
  cartDetails,
  checkoutForm,
  deliveryCharge,
  estimatedArrivalLabel,
  freeDeliveryThreshold,
  gstAmount,
  gstRate,
  isPlacingOrder,
  onBack,
  onChange,
  onPlaceOrder,
  placedOrder,
  session,
  subtotal,
  totalBill,
}) {
  if (placedOrder) {
    return (
      <main className="checkout-shell">
        <section className="checkout-success panel">
          <span className="section-kicker">Order confirmed</span>
          <h1>Order placed successfully</h1>
          <p>
            Your order <strong>{placedOrder.orderNumber}</strong> has been placed for{" "}
            <strong>Rs. {Number(placedOrder.totalAmount || 0).toFixed(2)}</strong>.
          </p>

          <div className="success-grid">
            <div>
              <span>Customer</span>
              <strong>{placedOrder.customerName}</strong>
              <p>{placedOrder.customerEmail}</p>
            </div>
            <div>
              <span>Estimated delivery</span>
              <strong>{new Date(placedOrder.estimatedDeliveryDate).toLocaleDateString("en-IN", { day: "numeric", month: "long", year: "numeric" })}</strong>
              <p>{placedOrder.totalItems} items are now in processing.</p>
            </div>
          </div>

          <div className="success-address">
            <span>Delivery address</span>
            <p>
              {placedOrder.addressLine1}
              {placedOrder.addressLine2 ? `, ${placedOrder.addressLine2}` : ""}
              {`, ${placedOrder.city}, ${placedOrder.state} ${placedOrder.postalCode}`}
            </p>
          </div>

          <button className="primary-button" onClick={onBack} type="button">
            Continue Shopping
          </button>
        </section>
      </main>
    );
  }

  return (
    <main className="checkout-shell">
      <div className="checkout-header panel">
        <div>
          <span className="section-kicker">Secure checkout</span>
          <h1>Review your order before placing it</h1>
          <p>
            Shipping to {session.name || session.email}. Estimated arrival by{" "}
            <strong>{estimatedArrivalLabel}</strong>.
          </p>
        </div>
        <button className="ghost-button" onClick={onBack} type="button">
          Back to Cart
        </button>
      </div>

      <section className="checkout-layout">
        <article className="panel checkout-form-panel">
          <div className="section-title">
            <div>
              <span className="section-kicker">Delivery details</span>
              <h3>Customer address</h3>
            </div>
          </div>

          <form className="checkout-form" onSubmit={onPlaceOrder}>
            <label>
              <span>Full name</span>
              <input name="customerName" onChange={onChange} required type="text" value={checkoutForm.customerName} />
            </label>

            <label>
              <span>Email</span>
              <input disabled type="email" value={session.email} />
            </label>

            <label>
              <span>Phone number</span>
              <input name="phoneNumber" onChange={onChange} required type="tel" value={checkoutForm.phoneNumber} />
            </label>

            <label>
              <span>Address line 1</span>
              <input name="addressLine1" onChange={onChange} required type="text" value={checkoutForm.addressLine1} />
            </label>

            <label>
              <span>Address line 2</span>
              <input name="addressLine2" onChange={onChange} type="text" value={checkoutForm.addressLine2} />
            </label>

            <label>
              <span>City</span>
              <input name="city" onChange={onChange} required type="text" value={checkoutForm.city} />
            </label>

            <label>
              <span>State</span>
              <input name="state" onChange={onChange} required type="text" value={checkoutForm.state} />
            </label>

            <label>
              <span>Postal code</span>
              <input name="postalCode" onChange={onChange} required type="text" value={checkoutForm.postalCode} />
            </label>

            <div className="checkout-estimate">
              <span>Estimated arrival</span>
              <strong>{estimatedArrivalLabel}</strong>
              <p>Orders are usually processed right away after payment confirmation.</p>
            </div>

            <button className="primary-button checkout-submit" disabled={isPlacingOrder || cartDetails.length === 0} type="submit">
              {isPlacingOrder ? "Placing Order..." : "Place Order"}
            </button>
          </form>
        </article>

        <aside className="checkout-summary-column">
          <article className="panel order-items-panel">
            <div className="section-title">
              <div>
                <span className="section-kicker">Order items</span>
                <h3>Products in this order</h3>
              </div>
            </div>

            <div className="checkout-item-list">
              {cartDetails.map((item) => (
                <article className="checkout-item-card" key={item.productId}>
                  <div className="checkout-item-media">
                    {item.imageUrl ? (
                      <img alt={item.name} src={item.imageUrl} />
                    ) : (
                      <div className="checkout-item-placeholder">{item.category}</div>
                    )}
                  </div>

                  <div className="checkout-item-copy">
                    <h4>{item.name}</h4>
                    <p>{item.category}</p>
                    <div className="checkout-item-meta">
                      <span>Qty: {item.quantity}</span>
                      <strong>Rs. {Number(item.lineTotal || 0).toFixed(2)}</strong>
                    </div>
                  </div>
                </article>
              ))}
            </div>
          </article>

          <article className="panel order-bill-panel">
            <div className="section-title">
              <div>
                <span className="section-kicker">Bill summary</span>
                <h3>Payment snapshot</h3>
              </div>
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
                <span>Total payable</span>
                <strong>Rs. {totalBill.toFixed(2)}</strong>
              </div>
            </div>
          </article>
        </aside>
      </section>
    </main>
  );
}

export default CheckoutPage;
