import React from "react";

const stats = [
  {
    label: "Auth Health",
    value: "98.4%",
    description: "Successful logins in the last 24 hours",
  },
  {
    label: "Active Sessions",
    value: "128",
    description: "Tracked across your protected endpoints",
  },
  {
    label: "Response Time",
    value: "214ms",
    description: "Average API turnaround this week",
  },
];

function StatsCards() {
  return (
    <article className="panel stats-panel">
      {stats.map((stat) => (
        <div className="mini-stat" key={stat.label}>
          <span>{stat.label}</span>
          <strong>{stat.value}</strong>
          <p>{stat.description}</p>
        </div>
      ))}
    </article>
  );
}

export default StatsCards;
