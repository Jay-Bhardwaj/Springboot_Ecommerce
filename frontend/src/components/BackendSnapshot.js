import React from "react";

function BackendSnapshot({ dashboardMessage }) {
  return (
    <article className="panel data-panel">
      <div className="section-title">
        <h3>Backend Snapshot</h3>
        <span>JWT secured route</span>
      </div>
      <div className="message-card">
        <p>{dashboardMessage}</p>
      </div>
    </article>
  );
}

export default BackendSnapshot;
