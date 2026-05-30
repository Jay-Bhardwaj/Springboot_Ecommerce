import React from "react";
import AdminDashboard from "../components/AdminDashboard";
import CustomerDashboard from "../components/CustomerDashboard";
import DashboardHero from "../components/DashboardHero";
import "../styles/dashboard.css";

function DashboardPage(props) {
  return (
    <main className="dashboard-shell">
      <DashboardHero
        allProductsCount={props.allProductsCount}
        availableCategories={props.availableCategories}
        cartItemCount={props.cartItemCount}
        isAdmin={props.isAdmin}
        onAddressView={props.onAddressView}
        onLogout={props.onLogout}
        onOrdersView={props.onOrdersView}
        onProfileView={props.onProfileView}
        onRefreshProducts={props.onRefreshProducts}
        session={props.session}
        storeMessage={props.storeMessage}
      />

      {props.isAdmin ? <AdminDashboard {...props} /> : <CustomerDashboard {...props} />}
    </main>
  );
}

export default DashboardPage;
