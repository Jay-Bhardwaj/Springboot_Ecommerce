import React from "react";

function DashboardHero({ isAdmin, onLogout, onRefreshProducts, session, storeMessage }) {
  return (
    <section className="dashboard-hero">
      <div>
        <span className="eyebrow">{isAdmin ? "Admin Console" : "Customer Lounge"}</span>
        <h1>{isAdmin ? "Manage your store" : "Discover your next favorite product"}</h1>
        <p>{storeMessage}</p>
        <div className="hero-meta">
          <span>{session.name || session.email}</span>
          <span>{session.role}</span>
        </div>
      </div>

      <div className="hero-actions">
        <button className="primary-button" onClick={onRefreshProducts} type="button">
          Refresh Products
        </button>
        <button className="ghost-button" onClick={onLogout} type="button">
          Logout
        </button>
      </div>
    </section>
  );
}

export default DashboardHero;
