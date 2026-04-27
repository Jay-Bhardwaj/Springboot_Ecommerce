import React from "react";

const features = [
  {
    title: "Role-based entry",
    description: "Start with admin login, while customer login and register stay clean and separate.",
  },
  {
    title: "Inventory workflow",
    description: "Admins can create, edit, and delete products from the dashboard.",
  },
  {
    title: "Storefront feel",
    description: "Customers get a product-focused experience instead of a plain auth page.",
  },
];

function FeatureHighlights() {
  return (
    <div className="feature-strip">
      {features.map((feature) => (
        <div className="feature-card" key={feature.title}>
          <strong>{feature.title}</strong>
          <span>{feature.description}</span>
        </div>
      ))}
    </div>
  );
}

export default FeatureHighlights;
