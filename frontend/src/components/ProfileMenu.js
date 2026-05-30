import React, { useEffect, useRef, useState } from "react";

function ProfileMenu({ onAddress, onLogout, onOrders, onProfile, session }) {
  const [isOpen, setIsOpen] = useState(false);
  const menuRef = useRef(null);

  useEffect(() => {
    const handleClickOutside = (event) => {
      if (menuRef.current && !menuRef.current.contains(event.target)) {
        setIsOpen(false);
      }
    };

    document.addEventListener("mousedown", handleClickOutside);
    return () => document.removeEventListener("mousedown", handleClickOutside);
  }, []);

  const initials = (session.name || session.email || "C")
    .split(" ")
    .map((part) => part[0])
    .filter(Boolean)
    .slice(0, 2)
    .join("")
    .toUpperCase();

  const handleAction = (callback) => {
    setIsOpen(false);
    callback();
  };

  return (
    <div className={isOpen ? "profile-menu is-open" : "profile-menu"} ref={menuRef}>
      <button
        aria-expanded={isOpen}
        className="profile-trigger"
        onClick={() => setIsOpen((current) => !current)}
        type="button"
      >
        <span className="profile-avatar">{initials}</span>
        <span className="profile-trigger-copy">
          <strong>Profile</strong>
          <small>{session.name || session.email}</small>
        </span>
      </button>

      {isOpen ? (
        <div className="profile-dropdown panel">
          <button className="profile-option" onClick={() => handleAction(onProfile)} type="button">
            <strong>About Profile</strong>
            <span>View customer details and account summary.</span>
          </button>

          <button className="profile-option" onClick={() => handleAction(onAddress)} type="button">
            <strong>Address</strong>
            <span>Open your saved delivery address details.</span>
          </button>

          <button className="profile-option" onClick={() => handleAction(onOrders)} type="button">
            <strong>My Orders</strong>
            <span>Track placed orders, ETA, and payment state.</span>
          </button>

          <button className="profile-option danger" onClick={() => handleAction(onLogout)} type="button">
            <strong>Logout</strong>
            <span>Sign out from the customer workspace.</span>
          </button>
        </div>
      ) : null}
    </div>
  );
}

export default ProfileMenu;
