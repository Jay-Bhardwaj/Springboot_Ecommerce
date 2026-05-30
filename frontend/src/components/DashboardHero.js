import React from "react";
import ProfileMenu from "./ProfileMenu";

function DashboardHero({
  allProductsCount,
  availableCategories,
  cartItemCount,
  isAdmin,
  onAddressView,
  onLogout,
  onOrdersView,
  onProfileView,
  onRefreshProducts,
  session,
  storeMessage,
}) {
  const heroStats = isAdmin
    ? [
        { label: "Active Listings", value: allProductsCount },
        { label: "Categories", value: availableCategories.length },
        { label: "Workspace", value: "Live" },
      ]
    : [
        { label: "Products", value: allProductsCount },
        { label: "Categories", value: availableCategories.length },
        { label: "Cart", value: cartItemCount },
      ];

  return (
    <section className={`dashboard-hero ${isAdmin ? "admin-hero" : "storefront-hero"}`}>
      <div className="hero-copy">
        <span className="eyebrow">{isAdmin ? "Admin Console" : "Daily Fashion, Tech & Home Deals"}</span>
        <h1>{isAdmin ? "Manage your store" : "A brighter storefront for browsing, deals, and quick checkout"}</h1>
        <p>{storeMessage}</p>
        <div className="hero-meta">
          <span>{session.name || session.email}</span>
          <span>{session.role}</span>
          {!isAdmin ? <span>Fast delivery available</span> : null}
        </div>
        <div className="hero-stat-grid">
          {heroStats.map((stat) => (
            <article className="hero-stat-card" key={stat.label}>
              <strong>{stat.value}</strong>
              <span>{stat.label}</span>
            </article>
          ))}
        </div>
      </div>

      <div className="hero-actions">
        {!isAdmin ? (
          <div className="hero-deal-card">
            <span className="deal-badge">Today&apos;s picks</span>
            <h3>Extra savings on curated products</h3>
            <p>Browse a more marketplace-style catalog with quick discovery, stronger visuals, and a simpler path to checkout.</p>
          </div>
        ) : null}
        <button className="primary-button" onClick={() => onRefreshProducts()} type="button">
          Refresh Products
        </button>
        {isAdmin ? (
          <button className="ghost-button" onClick={onLogout} type="button">
            Logout
          </button>
        ) : (
          <ProfileMenu
            onAddress={onAddressView}
            onLogout={onLogout}
            onOrders={onOrdersView}
            onProfile={onProfileView}
            session={session}
          />
        )}
      </div>
    </section>
  );
}

export default DashboardHero;
