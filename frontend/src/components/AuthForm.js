import React from "react";

function AuthForm({ helper, isSubmitting, onChange, onSubmit, showName, submitLabel, userForm }) {
  return (
    <form className="auth-form" onSubmit={onSubmit}>
      {showName ? (
        <label>
          <span>Full Name</span>
          <input
            name="name"
            onChange={onChange}
            placeholder="Jay Bhardwaj"
            required
            type="text"
            value={userForm.name}
          />
        </label>
      ) : null}

      <label>
        <span>Email Address</span>
        <input
          name="email"
          onChange={onChange}
          placeholder="you@gmail.com"
          required
          type="email"
          value={userForm.email}
        />
      </label>

      <label>
        <span>Password</span>
        <input
          name="password"
          onChange={onChange}
          placeholder="Enter your password"
          required
          type="password"
          value={userForm.password}
        />
      </label>

      <button className="primary-button submit-button" disabled={isSubmitting} type="submit">
        {submitLabel}
      </button>

      <p className="form-helper">{helper}</p>
    </form>
  );
}

export default AuthForm;
