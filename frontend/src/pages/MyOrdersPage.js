import React from "react";

function MyOrdersPage({ isLoadingOrders, onBack, orders }) {
  const formatDate = (value) =>
    new Date(value).toLocaleDateString("en-IN", {
      day: "numeric",
      month: "long",
      year: "numeric",
    });

  const getStatusSteps = (status) => {
    const steps = ["PLACED", "SHIPPED", "IN_TRANSIT", "DELIVERED"];
    const currentIndex = steps.indexOf(status);
    return steps.map((step, index) => ({
      label: step.replaceAll("_", " "),
      state: index < currentIndex ? "done" : index === currentIndex ? "current" : "upcoming",
    }));
  };

  return (
    <main className="customer-page-shell">
      <section className="panel customer-page-hero">
        <div>
          <span className="section-kicker">Order history</span>
          <h1>My orders</h1>
          <p>Track every order with live-looking shipping milestones, ETA, items, and payment details.</p>
        </div>
        <button className="ghost-button" onClick={onBack} type="button">
          Back to Shopping
        </button>
      </section>

      {isLoadingOrders ? (
        <section className="panel">
          <p className="empty-state">Loading your orders...</p>
        </section>
      ) : orders.length === 0 ? (
        <section className="panel">
          <p className="empty-state">No orders yet. Place your first order from checkout to see it here.</p>
        </section>
      ) : (
        <section className="orders-list">
          {orders.map((order) => (
            <article className="panel order-history-card" key={order.orderId}>
              <div className="order-history-header">
                <div>
                  <span className="section-kicker">Order {order.orderNumber}</span>
                  <h3>{order.items.length} products in this order</h3>
                  <p>Ordered on {formatDate(order.placedAt)}</p>
                </div>

                <div className="order-history-meta">
                  <span className={`status-chip ${order.orderStatus.toLowerCase()}`}>
                    {order.orderStatus.replaceAll("_", " ")}
                  </span>
                  <strong>Rs. {Number(order.totalAmount || 0).toFixed(2)}</strong>
                  <small>
                    {order.paymentMethod === "ONLINE"
                      ? `Paid online • ${order.paymentStatus}`
                      : `Cash on Delivery • ${order.paymentStatus}`}
                  </small>
                </div>
              </div>

              <div className="order-progress">
                {getStatusSteps(order.orderStatus).map((step) => (
                  <div className={`order-step ${step.state}`} key={`${order.orderId}-${step.label}`}>
                    <span className="step-dot" />
                    <strong>{step.label}</strong>
                  </div>
                ))}
              </div>

              <div className="order-summary-grid">
                <div className="order-address-card">
                  <span>Delivery address</span>
                  <p>
                    {order.addressLine1}
                    {order.addressLine2 ? `, ${order.addressLine2}` : ""}
                  </p>
                  <p>
                    {order.city}, {order.state} {order.postalCode}
                  </p>
                </div>

                <div className="order-address-card">
                  <span>Estimated delivery</span>
                  <p>{formatDate(order.estimatedDeliveryDate)}</p>
                  <p>
                    {order.orderStatus === "DELIVERED"
                      ? "Delivered successfully"
                      : "Your package is moving through the delivery journey."}
                  </p>
                </div>
              </div>

              <div className="checkout-item-list">
                {order.items.map((item) => (
                  <article className="checkout-item-card" key={`${order.orderId}-${item.productId}`}>
                    <div className="checkout-item-media">
                      {item.imageUrl ? (
                        <img alt={item.productName} src={item.imageUrl} />
                      ) : (
                        <div className="checkout-item-placeholder">{item.category}</div>
                      )}
                    </div>

                    <div className="checkout-item-copy">
                      <h4>{item.productName}</h4>
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
          ))}
        </section>
      )}
    </main>
  );
}

export default MyOrdersPage;
