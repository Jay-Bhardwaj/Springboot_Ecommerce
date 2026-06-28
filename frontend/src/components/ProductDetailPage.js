import React, { useEffect, useMemo, useState } from "react";

const LOW_STOCK_THRESHOLD = 5;

function formatPrice(value) {
  return `₹${Number(value || 0).toLocaleString("en-IN")}`;
}

function formatCountdown(ms) {
  const safeMs = Math.max(ms, 0);
  const totalMinutes = Math.floor(safeMs / 60000);
  const hours = Math.floor(totalMinutes / 60);
  const minutes = totalMinutes % 60;

  return `${hours} hrs ${minutes} mins`;
}

function addDays(date, days) {
  const nextDate = new Date(date);
  nextDate.setDate(nextDate.getDate() + days);
  return nextDate;
}

function ProductDetailPage({ onAddToCart, onBack, onBuyNow, onProceedToCheckout, product }) {
  const [quantity, setQuantity] = useState(1);
  const [activeImage, setActiveImage] = useState("");

  useEffect(() => {
    setQuantity(1);
    setActiveImage(product.imageUrl || "");
  }, [product.id, product.imageUrl]);

  const availableStock = Number(product.stockQuantity || 0);
  const brandName = product.brand || "Mercato";
  const categoryName = product.category || "Featured";
  const basePrice = Number(product.price || 0);
  const fallbackDiscount = availableStock <= LOW_STOCK_THRESHOLD ? 15 : 12;
  const originalPrice = Number(product.originalPrice || product.mrp || Math.round(basePrice / (1 - fallbackDiscount / 100)));
  const discountPercent = originalPrice > basePrice
    ? Math.max(1, Math.round(((originalPrice - basePrice) / originalPrice) * 100))
    : fallbackDiscount;
  const averageRating = Number(product.rating || product.averageRating || 4.2);
  const ratingCount = Number(product.ratingCount || 344);

  const deliveryDate = useMemo(() => addDays(new Date(), 2), []);
  const deliveryLabel = deliveryDate.toLocaleDateString("en-IN", {
    weekday: "long",
    day: "numeric",
    month: "long",
  });

  const orderCutoff = useMemo(() => {
    const cutoff = new Date();
    cutoff.setHours(23, 15, 0, 0);

    if (cutoff.getTime() < Date.now()) {
      cutoff.setDate(cutoff.getDate() + 1);
    }

    return cutoff;
  }, []);

  const orderCountdown = formatCountdown(orderCutoff.getTime() - Date.now());
  const lowStockMessage =
    availableStock <= 0
      ? "Currently unavailable"
      : availableStock <= LOW_STOCK_THRESHOLD
        ? `Only ${availableStock} left in stock - order soon`
        : `${availableStock} in stock`;

  const galleryImages = (product.galleryImages && product.galleryImages.length > 0
    ? product.galleryImages
    : product.imageUrl
      ? [product.imageUrl, product.imageUrl, product.imageUrl, product.imageUrl]
      : []
  ).slice(0, 4);

  const showPlaceholder = galleryImages.length === 0;

  const serviceBadges = [
    "10 days Service Centre",
    "Free Delivery",
    "1 Year Warranty",
    "Top Brand",
  ];

  const offerCards = [
    {
      title: "Bank Offer",
      description: "Up to ₹3,000 discount on select credit cards.",
      meta: "Tap for bank terms",
    },
    {
      title: "No Cost EMI",
      description: "Pay in easy monthly instalments with eligible cards.",
      meta: "Available at checkout",
    },
    {
      title: "Cashback",
      description: "Partner offers and wallet cashback on selected payments.",
      meta: "Limited time rewards",
    },
  ];

  const handleQuantityChange = (event) => {
    const nextValue = Number(event.target.value || 1);
    const normalizedValue = Number.isNaN(nextValue) ? 1 : nextValue;
    setQuantity(Math.min(Math.max(normalizedValue, 1), Math.max(availableStock, 1)));
  };

  const handleAddToCart = async () => {
    if (availableStock <= 0) {
      return;
    }

    await onAddToCart?.(product.id, quantity);
  };

  const handleBuyNow = async () => {
    if (availableStock <= 0) {
      return;
    }

    await onAddToCart?.(product.id, quantity);
    await new Promise((resolve) => setTimeout(resolve, 0));

    if (typeof onBuyNow === "function") {
      onBuyNow();
      return;
    }

    if (typeof onProceedToCheckout === "function") {
      onProceedToCheckout();
    }
  };

  return (
    <main className="mx-auto min-h-screen max-w-7xl px-4 py-5 sm:px-6 lg:px-8">
      <button
        className="mb-4 inline-flex items-center gap-2 rounded-full border border-emerald-900/10 bg-white/90 px-4 py-2 text-sm font-semibold text-[#006653] shadow-sm transition hover:-translate-y-0.5 hover:shadow-md"
        onClick={onBack}
        type="button"
      >
        <span aria-hidden="true">←</span>
        Back to catalog
      </button>

      <div className="grid gap-6 xl:grid-cols-[minmax(0,1.4fr)_390px]">
        <section className="grid gap-6">
          <article className="rounded-[28px] border border-black/5 bg-white p-4 shadow-[0_18px_50px_rgba(17,24,39,0.08)] sm:p-5">
            <nav aria-label="Breadcrumb" className="mb-4 flex flex-wrap items-center gap-2 text-xs font-medium text-slate-500">
              <button className="transition hover:text-[#006653]" onClick={onBack} type="button">
                Home
              </button>
              <span>/</span>
              <button className="transition hover:text-[#006653]" onClick={onBack} type="button">
                Products
              </button>
              <span>/</span>
              <span className="text-slate-700">{categoryName}</span>
            </nav>

            <div className="grid gap-4 lg:grid-cols-[92px_minmax(0,1fr)]">
              <div className="order-2 flex gap-3 overflow-x-auto pb-1 lg:order-1 lg:flex-col lg:overflow-visible">
                {(showPlaceholder ? [0, 1, 2, 3] : galleryImages).map((item, index) => {
                  const thumbnailSrc = showPlaceholder ? "" : item;
                  const isSelected = showPlaceholder
                    ? index === 0
                    : activeImage === item || (!activeImage && index === 0);

                  return (
                    <button
                      key={`${thumbnailSrc || "placeholder"}-${index}`}
                      className={`flex h-20 w-20 shrink-0 items-center justify-center overflow-hidden rounded-2xl border-2 bg-slate-50 transition ${
                        isSelected ? "border-[#006653] ring-4 ring-emerald-100" : "border-transparent hover:border-emerald-200"
                      }`}
                      onClick={() => setActiveImage(thumbnailSrc)}
                      type="button"
                    >
                      {thumbnailSrc ? (
                        <img alt={`${product.name} preview ${index + 1}`} className="h-full w-full object-cover" src={thumbnailSrc} />
                      ) : (
                        <div className="grid h-full w-full place-items-center bg-gradient-to-br from-emerald-50 to-emerald-100 text-sm font-semibold text-[#006653]">
                          {categoryName}
                        </div>
                      )}
                    </button>
                  );
                })}
              </div>

              <div className="order-1 overflow-hidden rounded-[26px] bg-gradient-to-br from-emerald-50 via-white to-amber-50 p-3 lg:order-2">
                <div className="overflow-hidden rounded-[22px] bg-white shadow-inner">
                  {activeImage ? (
                    <img
                      alt={product.name}
                      className="aspect-square w-full object-contain p-6 sm:p-8"
                      src={activeImage}
                    />
                  ) : (
                    <div className="grid aspect-square place-items-center p-6 text-center">
                      <div className="rounded-[30px] border border-dashed border-emerald-200 bg-white px-8 py-14">
                        <p className="text-sm font-semibold uppercase tracking-[0.24em] text-[#006653]">
                          {categoryName}
                        </p>
                        <h3 className="mt-3 text-2xl font-black text-slate-900">{product.name}</h3>
                        <p className="mt-2 max-w-sm text-sm text-slate-500">{product.description}</p>
                      </div>
                    </div>
                  )}
                </div>
              </div>
            </div>

            <div className="mt-4 flex flex-wrap items-center gap-3 text-sm text-slate-600">
              <span className="rounded-full bg-[#006653]/10 px-3 py-1 font-semibold text-[#006653]">
                {lowStockMessage}
              </span>
              <span className="rounded-full bg-amber-100 px-3 py-1 font-semibold text-amber-700">
                Amazon-style inventory alert
              </span>
            </div>
          </article>

          <article className="rounded-[28px] border border-black/5 bg-white p-5 shadow-[0_18px_50px_rgba(17,24,39,0.08)] sm:p-6">
            <div className="flex flex-wrap items-center gap-2 text-sm font-medium text-[#006653]">
              <span className="rounded-full bg-emerald-50 px-3 py-1">Visit the {brandName} Store</span>
              <span className="text-slate-400">•</span>
              <span className="text-slate-500">{categoryName}</span>
            </div>

            <h1 className="mt-4 max-w-4xl text-3xl font-black tracking-tight text-slate-950 sm:text-4xl">
              {product.name}
            </h1>

            <div className="mt-3 flex flex-wrap items-center gap-3">
              <div className="flex items-center gap-1 text-amber-400">
                {Array.from({ length: 5 }).map((_, index) => (
                  <span key={index} className={index < Math.round(averageRating) ? "text-amber-400" : "text-slate-300"}>
                    ★
                  </span>
                ))}
              </div>
              <a href="#reviews" className="text-sm font-semibold text-[#006653] hover:underline">
                {averageRating.toFixed(1)} rating ({ratingCount} ratings)
              </a>
              <span className="rounded-full bg-slate-100 px-3 py-1 text-xs font-semibold uppercase tracking-[0.2em] text-slate-600">
                Mercato clean UI
              </span>
            </div>

            <p className="mt-5 max-w-3xl text-base leading-7 text-slate-600">
              {product.description || "No description available for this product yet."}
            </p>

            <div className="mt-6 rounded-[24px] border border-rose-100 bg-rose-50/70 p-4 sm:p-5">
              <div className="flex flex-wrap items-end gap-3">
                <span className="text-base font-semibold text-rose-500">-{discountPercent}%</span>
                <strong className="text-4xl font-black tracking-tight text-slate-950">
                  {formatPrice(basePrice)}
                </strong>
              </div>
              <p className="mt-2 text-sm text-slate-500">
                M.R.P.: <span className="line-through">{formatPrice(originalPrice)}</span>
              </p>
              <p className="mt-3 text-sm font-medium text-slate-600">Inclusive of all taxes</p>
            </div>

            <div className="mt-6">
              <div className="mb-3 flex items-center gap-2">
                <span className="inline-flex h-8 w-8 items-center justify-center rounded-full bg-emerald-50 text-[#006653]">%</span>
                <h2 className="text-lg font-bold text-slate-950">Offers</h2>
              </div>

              <div className="flex gap-3 overflow-x-auto pb-1">
                {offerCards.map((offer) => (
                  <button
                    key={offer.title}
                    className="min-w-[210px] flex-1 rounded-[20px] border border-slate-200 bg-white p-4 text-left shadow-sm transition hover:-translate-y-0.5 hover:border-[#006653]/30 hover:shadow-md"
                    type="button"
                  >
                    <div className="flex items-center justify-between gap-3">
                      <span className="text-sm font-extrabold text-slate-900">{offer.title}</span>
                      <span className="rounded-full bg-emerald-50 px-2.5 py-1 text-[11px] font-bold uppercase tracking-[0.18em] text-[#006653]">
                        Deal
                      </span>
                    </div>
                    <p className="mt-2 text-sm leading-6 text-slate-600">{offer.description}</p>
                    <p className="mt-3 text-sm font-semibold text-[#006653]">{offer.meta}</p>
                  </button>
                ))}
              </div>
            </div>

            <div className="mt-6">
              <div className="mb-3 flex items-center gap-2">
                <span className="inline-flex h-8 w-8 items-center justify-center rounded-full bg-emerald-50 text-[#006653]">✓</span>
                <h2 className="text-lg font-bold text-slate-950">Service highlights</h2>
              </div>

              <div className="grid grid-cols-2 gap-3 sm:grid-cols-4">
                {serviceBadges.map((badge) => (
                  <div
                    className="rounded-[18px] border border-slate-200 bg-slate-50 px-3 py-4 text-center shadow-sm"
                    key={badge}
                  >
                    <div className="mx-auto flex h-10 w-10 items-center justify-center rounded-full bg-white text-lg shadow-sm">
                      ⦿
                    </div>
                    <p className="mt-3 text-sm font-semibold text-slate-700">{badge}</p>
                  </div>
                ))}
              </div>
            </div>
          </article>
        </section>

        <aside className="xl:sticky xl:top-6 xl:self-start">
          <div className="grid gap-4">
            <article className="rounded-[28px] border border-black/5 bg-white p-5 shadow-[0_18px_50px_rgba(17,24,39,0.08)]">
              <div className="flex items-start justify-between gap-4">
                <div>
                  <p className="text-sm font-semibold text-slate-500">Delivery promise</p>
                  <h2 className="mt-1 text-xl font-black text-slate-950">FREE delivery {deliveryLabel}</h2>
                </div>
                <span className="rounded-full bg-emerald-50 px-3 py-1 text-xs font-bold uppercase tracking-[0.18em] text-[#006653]">
                  Prime-like
                </span>
              </div>

              <p className="mt-4 text-sm leading-6 text-slate-600">
                Order within <strong className="text-slate-900">{orderCountdown}</strong>. Details.
              </p>

              <button
                className="mt-4 flex w-full items-center justify-between rounded-2xl border border-slate-200 bg-slate-50 px-4 py-3 text-left transition hover:border-[#006653]/30 hover:bg-emerald-50"
                type="button"
              >
                <span>
                  <span className="block text-xs font-semibold uppercase tracking-[0.2em] text-slate-500">
                    Delivering to
                  </span>
                  <span className="mt-1 block text-sm font-bold text-slate-950">New Delhi 110060</span>
                </span>
                <span className="text-sm font-semibold text-[#006653]">Update location</span>
              </button>

              <div className="mt-4 rounded-2xl border border-emerald-100 bg-emerald-50/70 p-4">
                <p className="text-sm font-semibold text-[#006653]">Stock alert</p>
                <p className="mt-1 text-sm leading-6 text-slate-700">
                  {lowStockMessage === "Currently unavailable"
                    ? "This product is unavailable right now."
                    : lowStockMessage}
                </p>
              </div>
            </article>

            <article className="rounded-[28px] border border-black/5 bg-white p-5 shadow-[0_18px_50px_rgba(17,24,39,0.08)]">
              <div className="flex items-center justify-between gap-3">
                <div>
                  <p className="text-sm font-semibold text-slate-500">Quantity</p>
                  <h3 className="text-base font-bold text-slate-950">Choose how many</h3>
                </div>
                <span className="rounded-full bg-slate-100 px-3 py-1 text-xs font-semibold text-slate-600">
                  Max {Math.max(availableStock, 1)}
                </span>
              </div>

              <div className="mt-4 inline-grid grid-cols-[52px_88px_52px] overflow-hidden rounded-2xl border border-slate-200 bg-white shadow-sm">
                <button
                  className="h-12 text-xl font-bold text-[#006653] transition hover:bg-emerald-50 disabled:cursor-not-allowed disabled:opacity-40"
                  disabled={quantity <= 1}
                  onClick={() => setQuantity((current) => Math.max(current - 1, 1))}
                  type="button"
                >
                  -
                </button>
                <input
                  className="h-12 border-x border-slate-200 text-center text-base font-bold outline-none"
                  max={Math.max(availableStock, 1)}
                  min="1"
                  onChange={handleQuantityChange}
                  type="number"
                  value={quantity}
                />
                <button
                  className="h-12 text-xl font-bold text-[#006653] transition hover:bg-emerald-50 disabled:cursor-not-allowed disabled:opacity-40"
                  disabled={quantity >= availableStock}
                  onClick={() => setQuantity((current) => Math.min(current + 1, Math.max(availableStock, 1)))}
                  type="button"
                >
                  +
                </button>
              </div>

              <div className="mt-5 grid gap-3">
                <button
                  className="inline-flex w-full items-center justify-center rounded-2xl bg-[#006653] px-5 py-4 text-base font-bold text-white shadow-[0_16px_30px_rgba(0,102,83,0.22)] transition hover:-translate-y-0.5 hover:bg-[#005646] disabled:cursor-not-allowed disabled:opacity-50"
                  disabled={availableStock <= 0}
                  onClick={handleAddToCart}
                  type="button"
                >
                  Add to Cart
                </button>
                <button
                  className="inline-flex w-full items-center justify-center rounded-2xl bg-amber-400 px-5 py-4 text-base font-black text-slate-950 shadow-[0_16px_30px_rgba(245,158,11,0.18)] transition hover:-translate-y-0.5 hover:bg-amber-300 disabled:cursor-not-allowed disabled:opacity-50"
                  disabled={availableStock <= 0}
                  onClick={handleBuyNow}
                  type="button"
                >
                  Buy Now
                </button>
              </div>

              <p className="mt-4 text-xs leading-6 text-slate-500">
                Buy Now takes you to checkout with this item ready in your cart.
              </p>
            </article>
          </div>
        </aside>
      </div>

      <article id="reviews" className="mt-6 rounded-[28px] border border-black/5 bg-white p-5 shadow-[0_18px_50px_rgba(17,24,39,0.08)] sm:p-6">
        <div className="flex flex-wrap items-end justify-between gap-4 border-b border-slate-100 pb-5">
          <div>
            <p className="text-sm font-semibold uppercase tracking-[0.2em] text-[#006653]">Product details</p>
            <h2 className="mt-2 text-2xl font-black text-slate-950">Specifications and delivery promise</h2>
          </div>
          <div className="rounded-[18px] bg-emerald-50 px-4 py-3 text-sm font-semibold text-[#006653]">
            {availableStock > 0 ? "Ready to ship" : "Backorder only"}
          </div>
        </div>

        <div className="mt-6 grid gap-4 md:grid-cols-2 xl:grid-cols-4">
          <div className="rounded-[20px] border border-slate-200 bg-slate-50 p-4">
            <p className="text-xs font-bold uppercase tracking-[0.18em] text-slate-500">Brand</p>
            <p className="mt-2 text-base font-bold text-slate-950">{brandName}</p>
          </div>
          <div className="rounded-[20px] border border-slate-200 bg-slate-50 p-4">
            <p className="text-xs font-bold uppercase tracking-[0.18em] text-slate-500">Category</p>
            <p className="mt-2 text-base font-bold text-slate-950">{categoryName}</p>
          </div>
          <div className="rounded-[20px] border border-slate-200 bg-slate-50 p-4">
            <p className="text-xs font-bold uppercase tracking-[0.18em] text-slate-500">Product ID</p>
            <p className="mt-2 text-base font-bold text-slate-950">#{product.id}</p>
          </div>
          <div className="rounded-[20px] border border-slate-200 bg-slate-50 p-4">
            <p className="text-xs font-bold uppercase tracking-[0.18em] text-slate-500">Delivery</p>
            <p className="mt-2 text-base font-bold text-slate-950">Tuesday, 30 June</p>
          </div>
        </div>
      </article>
    </main>
  );
}

export default ProductDetailPage;
