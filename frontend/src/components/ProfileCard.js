import React from "react";

function ProfileCard({ profile }) {
  return (
    <article className="panel profile-panel">
      <div className="panel-heading">
        <span className="status-dot" />
        <div>
          <h2>{profile.name}</h2>
          <p>{profile.role}</p>
        </div>
      </div>
      <div className="profile-meta">
        <div>
          <span>Email</span>
          <strong>{profile.email || "Connected after first login"}</strong>
        </div>
        <div>
          <span>Status</span>
          <strong>{profile.status}</strong>
        </div>
      </div>
      <p className="support-copy">{profile.welcomeMessage}</p>
    </article>
  );
}

export default ProfileCard;
