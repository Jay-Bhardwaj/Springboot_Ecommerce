import React from "react";
import AuthModeSelector from "../components/AuthModeSelector";
import AuthForm from "../components/AuthForm";
import FeatureHighlights from "../components/FeatureHighlights";
import "../styles/auth.css";

function AuthPage({ authView, isSubmittingAuth, onAdminLogin, onChange, onCustomerLogin, onRegister, onSwitchView, storeMessage, userForm }) {
  const config = {
    "admin-login": {
      title: "Admin access",
      subtitle: "Manage inventory, pricing, and product operations from a dedicated seller workspace.",
      submitLabel: isSubmittingAuth ? "Signing in..." : "Login as Admin",
      helper: "Default seeded admin: admin@shop.com / Admin@123",
      onSubmit: onAdminLogin,
      showName: false,
    },
    "customer-login": {
      title: "Customer sign in",
      subtitle: "Welcome shoppers back to a cleaner ecommerce experience with curated products and saved sessions.",
      submitLabel: isSubmittingAuth ? "Signing in..." : "Login as Customer",
      helper: "Customers can browse products after login.",
      onSubmit: onCustomerLogin,
      showName: false,
    },
    "customer-register": {
      title: "Create customer account",
      subtitle: "Register shoppers with a polished onboarding screen built for your ecommerce project.",
      submitLabel: isSubmittingAuth ? "Creating account..." : "Register Customer",
      helper: "Registration creates a CUSTOMER account automatically.",
      onSubmit: onRegister,
      showName: true,
    },
  }[authView];

  return (
    <main className="auth-shell">
      <section className="brand-panel">
        <div className="brand-copy">
          <span className="eyebrow">JKB Store</span>
          <h1>Sell smarter with an ecommerce-ready sign in flow.</h1>
          <p>
            Admins manage products. Customers browse and shop. The experience now
            feels like an online store, not a generic login demo.
          </p>
        </div>

        <div className="brand-note">
          <strong>Backend status</strong>
          <span>{storeMessage}</span>
        </div>

        <FeatureHighlights />
      </section>

      <section className="auth-panel">
        <div className="auth-card">
          <AuthModeSelector authView={authView} onSwitchView={onSwitchView} />

          <div className="auth-copy">
            <h2>{config.title}</h2>
            <p>{config.subtitle}</p>
          </div>

          <AuthForm
            helper={config.helper}
            isSubmitting={isSubmittingAuth}
            onChange={onChange}
            onSubmit={config.onSubmit}
            showName={config.showName}
            submitLabel={config.submitLabel}
            userForm={userForm}
          />
        </div>
      </section>
    </main>
  );
}

export default AuthPage;
