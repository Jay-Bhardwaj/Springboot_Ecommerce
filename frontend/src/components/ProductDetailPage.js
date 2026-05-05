import React, { useEffect, useState } from "react";
import "../styles/product-detail-page.css";

function ProductDetailPage({ onAddToCart, onBack, product }) {
  const [quantity, setQuantity] = useState(1);
  const [reviews, setReviews] = useState([]);
  const [newReview, setNewReview] = useState({ rating: 5, comment: "" });
  const [hasReviewed, setHasReviewed] = useState(false);

  useEffect(() => {
    setQuantity(1);
    // Load sample reviews
    setReviews([
      { id: 1, rating: 5, comment: "Excellent product! Highly recommended.", author: "John D.", date: "2 days ago" },
      { id: 2, rating: 4, comment: "Good quality, fast delivery.", author: "Sarah M.", date: "1 week ago" },
      { id: 3, rating: 5, comment: "Best purchase ever!", author: "Mike S.", date: "2 weeks ago" },
    ]);
  }, [product.id]);

  const availableStock = Number(product.stockQuantity || 0);
  const averageRating = reviews.length > 0 ? (reviews.reduce((sum, r) => sum + r.rating, 0) / reviews.length).toFixed(1) : 0;

  const incrementQuantity = () => {
    setQuantity((current) => Math.min(current + 1, Math.max(availableStock, 1)));
  };

  const decrementQuantity = () => {
    setQuantity((current) => Math.max(current - 1, 1));
  };

  const handleQuantityInput = (event) => {
    const nextValue = Number(event.target.value || 1);
    setQuantity(Math.min(Math.max(nextValue, 1), Math.max(availableStock, 1)));
  };

  const handleAddReview = (e) => {
    e.preventDefault();
    if (newReview.comment.trim()) {
      const review = {
        id: reviews.length + 1,
        rating: newReview.rating,
        comment: newReview.comment,
        author: "You",
        date: "Just now",
      };
      setReviews([review, ...reviews]);
      setNewReview({ rating: 5, comment: "" });
      setHasReviewed(true);
      setTimeout(() => setHasReviewed(false), 3000);
    }
  };

  const renderStars = (rating) => {
    return (
      <div className="star-rating">
        {[...Array(5)].map((_, i) => (
          <span key={i} className={i < Math.round(rating) ? "star filled" : "star"}>
            ★
          </span>
        ))}
      </div>
    );
  };

  return (
    <div className="product-detail-page">
      <button className="back-button-detail" onClick={onBack} type="button">
        ← Back to Catalog
      </button>

      <div className="detail-container">
        {/* Product Image Section */}
        <div className="product-image-section">
          <div className="product-image-wrapper">
            {product.imageUrl ? (
              <img alt={product.name} src={product.imageUrl} className="product-image-large" />
            ) : (
              <div className="product-image-placeholder">{product.category}</div>
            )}
          </div>
          <div className="product-badges">
            {availableStock > 0 && <span className="badge in-stock">In Stock</span>}
            {availableStock <= 5 && availableStock > 0 && <span className="badge low-stock">Only {availableStock} left</span>}
            {availableStock === 0 && <span className="badge out-of-stock">Out of Stock</span>}
          </div>
        </div>

        {/* Product Details Section */}
        <div className="product-info-section">
          {/* Header */}
          <div className="product-header">
            <div className="category-badge">{product.category}</div>
            <h1>{product.name}</h1>
            <div className="rating-section">
              {renderStars(averageRating)}
              <span className="rating-text">({reviews.length} reviews)</span>
            </div>
          </div>

          {/* Price Section */}
          <div className="price-section">
            <span className="price-label">Price</span>
            <div className="price-display">
              <strong>Rs. {Number(product.price || 0).toFixed(2)}</strong>
              <span className="price-note">Inclusive of all taxes</span>
            </div>
          </div>

          {/* Description */}
          <div className="description-section">
            <h3>About this product</h3>
            <p>{product.description || "No description available"}</p>
          </div>

          {/* Product Specifications */}
          <div className="specs-section">
            <h3>Product Details</h3>
            <div className="specs-grid">
              <div className="spec-item">
                <span className="spec-label">Category</span>
                <span className="spec-value">{product.category}</span>
              </div>
              <div className="spec-item">
                <span className="spec-label">Product ID</span>
                <span className="spec-value">#{product.id}</span>
              </div>
              <div className="spec-item">
                <span className="spec-label">Availability</span>
                <span className="spec-value">{availableStock > 0 ? "Available" : "Out of Stock"}</span>
              </div>
              <div className="spec-item">
                <span className="spec-label">Seller Rating</span>
                <span className="spec-value">{averageRating} ★</span>
              </div>
            </div>
          </div>

          {/* Add to Cart Section */}
          <div className="purchase-section">
            <div className="quantity-section">
              <label>Quantity:</label>
              <div className="quantity-picker-large">
                <button
                  className="qty-btn"
                  onClick={decrementQuantity}
                  disabled={quantity <= 1}
                  type="button"
                >
                  −
                </button>
                <input
                  className="qty-input"
                  max={Math.max(availableStock, 1)}
                  min="1"
                  onChange={handleQuantityInput}
                  type="number"
                  value={quantity}
                />
                <button
                  className="qty-btn"
                  onClick={incrementQuantity}
                  disabled={quantity >= availableStock}
                  type="button"
                >
                  +
                </button>
              </div>
            </div>

            <button
              className={`add-to-cart-btn ${availableStock <= 0 ? "disabled" : ""}`}
              disabled={availableStock <= 0}
              onClick={() => onAddToCart(product.id, quantity)}
              type="button"
            >
              {availableStock > 0 ? "🛒 Add to Cart" : "Out of Stock"}
            </button>
          </div>

          {/* Delivery Info */}
          <div className="delivery-info">
            <div className="info-item">
              <span className="info-icon">🚚</span>
              <div>
                <p className="info-title">Free Delivery</p>
                <p className="info-desc">On orders above Rs. 500</p>
              </div>
            </div>
            <div className="info-item">
              <span className="info-icon">🔄</span>
              <div>
                <p className="info-title">Easy Returns</p>
                <p className="info-desc">Within 7 days</p>
              </div>
            </div>
            <div className="info-item">
              <span className="info-icon">✓</span>
              <div>
                <p className="info-title">Authentic</p>
                <p className="info-desc">100% genuine products</p>
              </div>
            </div>
          </div>
        </div>
      </div>

      {/* Reviews Section */}
      <div className="reviews-section">
        <div className="reviews-header">
          <h2>Customer Reviews</h2>
          <div className="overall-rating">
            <div className="rating-big">{averageRating}</div>
            {renderStars(averageRating)}
            <p>{reviews.length} verified reviews</p>
          </div>
        </div>

        {/* Add Review Form */}
        <div className="add-review-form">
          <h3>Share your experience</h3>
          <form onSubmit={handleAddReview}>
            <div className="form-group">
              <label htmlFor="rating">Rating:</label>
              <select
                id="rating"
                value={newReview.rating}
                onChange={(e) => setNewReview({ ...newReview, rating: Number(e.target.value) })}
                className="rating-select"
              >
                <option value={5}>⭐⭐⭐⭐⭐ Excellent</option>
                <option value={4}>⭐⭐⭐⭐ Good</option>
                <option value={3}>⭐⭐⭐ Average</option>
                <option value={2}>⭐⭐ Poor</option>
                <option value={1}>⭐ Very Poor</option>
              </select>
            </div>
            <div className="form-group">
              <label htmlFor="comment">Your Review:</label>
              <textarea
                id="comment"
                value={newReview.comment}
                onChange={(e) => setNewReview({ ...newReview, comment: e.target.value })}
                placeholder="Share your thoughts about this product..."
                rows="4"
                className="review-textarea"
              />
            </div>
            <button type="submit" className="submit-review-btn">
              Submit Review
            </button>
          </form>
          {hasReviewed && <p className="success-message">✓ Review posted successfully!</p>}
        </div>

        {/* Reviews List */}
        <div className="reviews-list">
          <h3>All Reviews</h3>
          {reviews.length === 0 ? (
            <p className="no-reviews">No reviews yet. Be the first to review!</p>
          ) : (
            reviews.map((review) => (
              <div key={review.id} className="review-card">
                <div className="review-header">
                  <div className="review-author">{review.author}</div>
                  {renderStars(review.rating)}
                </div>
                <p className="review-date">{review.date}</p>
                <p className="review-text">{review.comment}</p>
              </div>
            ))
          )}
        </div>
      </div>
    </div>
  );
}

export default ProductDetailPage;
