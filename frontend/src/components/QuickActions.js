import React from "react";

const actions = [
  "Register new team members with role-aware onboarding.",
  "Verify login tokens by calling the protected dashboard API.",
  "Extend this screen with analytics, billing, or user management.",
];

function QuickActions() {
  return (
    <article className="panel activity-panel">
      <div className="section-title">
        <h3>Quick Actions</h3>
        <span>Suggested workflow</span>
      </div>
      <ul>
        {actions.map((action) => (
          <li key={action}>{action}</li>
        ))}
      </ul>
    </article>
  );
}

export default QuickActions;
