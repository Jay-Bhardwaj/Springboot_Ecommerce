import React from "react";

const modes = [
  { id: "admin-login", label: "Admin Login" },
  { id: "customer-login", label: "Customer Login" },
  { id: "customer-register", label: "Customer Register" },
];

function AuthModeSelector({ authView, onSwitchView }) {
  return (
    <div className="auth-tabs auth-tabs-grid">
      {modes.map((mode) => (
        <button
          key={mode.id}
          className={authView === mode.id ? "tab-button active" : "tab-button"}
          onClick={() => onSwitchView(mode.id)}
          type="button"
        >
          {mode.label}
        </button>
      ))}
    </div>
  );
}

export default AuthModeSelector;
