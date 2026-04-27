import React from "react";
import AdminDashboard from "../components/AdminDashboard";
import CustomerDashboard from "../components/CustomerDashboard";
import DashboardHero from "../components/DashboardHero";
import "../styles/dashboard.css";

function DashboardPage(props) {
  return (
    <main className="dashboard-shell">
      <DashboardHero
        isAdmin={props.isAdmin}
        onLogout={props.onLogout}
        onRefreshProducts={props.onRefreshProducts}
        session={props.session}
        storeMessage={props.storeMessage}
      />

      {props.isAdmin ? <AdminDashboard {...props} /> : <CustomerDashboard {...props} />}
    </main>
  );
}

export default DashboardPage;
