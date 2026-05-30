import React, { useEffect, useState } from "react";

function CustomerProfilePage({ isUpdatingProfile, onBack, onUpdateProfile, profileSection, savedAddress, session }) {
  const sectionTitle = profileSection === "address" ? "Saved address" : "About profile";
  const [profileForm, setProfileForm] = useState({ name: session.name || "", email: session.email || "" });

  useEffect(() => {
    setProfileForm({ name: session.name || "", email: session.email || "" });
  }, [session.email, session.name]);

  const handleChange = (event) => {
    const { name, value } = event.target;
    setProfileForm((current) => ({ ...current, [name]: value }));
  };

  const handleSubmit = async (event) => {
    event.preventDefault();
    await onUpdateProfile(profileForm);
  };

  return (
    <main className="customer-page-shell">
      <section className="panel customer-page-hero">
        <div>
          <span className="section-kicker">Customer profile</span>
          <h1>{sectionTitle}</h1>
          <p>Review your account details and saved delivery information in one place.</p>
        </div>
        <button className="ghost-button" onClick={onBack} type="button">
          Back to Shopping
        </button>
      </section>

      <section className="customer-page-grid">
        <article className="panel">
          <div className="section-title">
            <div>
              <span className="section-kicker">Account</span>
              <h3>About profile</h3>
            </div>
          </div>

          <form className="profile-edit-form" onSubmit={handleSubmit}>
            <label>
              <span>Full name</span>
              <input name="name" onChange={handleChange} type="text" value={profileForm.name} />
            </label>

            <label>
              <span>Email</span>
              <input name="email" onChange={handleChange} type="email" value={profileForm.email} />
            </label>

            <button className="primary-button" disabled={isUpdatingProfile} type="submit">
              {isUpdatingProfile ? "Saving..." : "Edit Profile"}
            </button>
          </form>

          <div className="profile-details-grid">
            <div>
              <span>Name</span>
              <strong>{session.name || "Customer"}</strong>
            </div>
            <div>
              <span>Email</span>
              <strong>{session.email}</strong>
            </div>
            <div>
              <span>Role</span>
              <strong>{session.role}</strong>
            </div>
            <div>
              <span>Account type</span>
              <strong>Loyal shopper</strong>
            </div>
          </div>
        </article>

        <article className="panel">
          <div className="section-title">
            <div>
              <span className="section-kicker">Delivery</span>
              <h3>Saved address</h3>
            </div>
          </div>

          {savedAddress.addressLine1 ? (
            <div className="saved-address-card">
              <strong>{savedAddress.customerName || session.name || "Customer"}</strong>
              <p>{savedAddress.phoneNumber || "Phone number not saved yet"}</p>
              <p>
                {savedAddress.addressLine1}
                {savedAddress.addressLine2 ? `, ${savedAddress.addressLine2}` : ""}
              </p>
              <p>
                {savedAddress.city}, {savedAddress.state} {savedAddress.postalCode}
              </p>
              <span>This address was saved from your latest checkout.</span>
            </div>
          ) : (
            <p className="empty-state">
              No saved address yet. Place an order once and your latest delivery address will appear here.
            </p>
          )}
        </article>
      </section>
    </main>
  );
}

export default CustomerProfilePage;
