import React, { useState } from "react";

export default function Navbar({
  isLoggedIn,
  userName,
  cartItemCount,
  session,
  onLogout,
  onOpenAuth,
  onOpenProfile,
  onOpenOrders,
  onOpenCart,
  isAdmin,
}) {
  const [mobileMenuOpen, setMobileMenuOpen] = useState(false);
  const [profileMenuOpen, setProfileMenuOpen] = useState(false);

  return (
    <nav className="bg-white border-b border-neutral-200 sticky top-0 z-50 shadow-sm">
      <div className="container-max">
        <div className="flex items-center justify-between h-16 md:h-20">
          {/* Logo */}
          <div className="flex-shrink-0 flex items-center">
            <div className="text-2xl md:text-3xl font-bold text-primary-600">
              Store
            </div>
          </div>

          {/* Desktop Search Bar - Center */}
          {!isAdmin && isLoggedIn && (
            <div className="hidden md:flex flex-1 mx-8 max-w-2xl">
              <input
                type="text"
                placeholder="Search products..."
                className="form-input w-full"
              />
            </div>
          )}

          {/* Desktop Menu - Right */}
          <div className="hidden md:flex items-center gap-2">
            {isLoggedIn ? (
              <>
                {!isAdmin && (
                  <button
                    onClick={onOpenCart}
                    className="relative p-2 text-neutral-600 hover:text-primary-600 transition-colors"
                  >
                    <svg
                      className="w-6 h-6"
                      fill="none"
                      stroke="currentColor"
                      viewBox="0 0 24 24"
                    >
                      <path
                        strokeLinecap="round"
                        strokeLinejoin="round"
                        strokeWidth={2}
                        d="M3 3h2l.4 2M7 13h10l4-8H5.4M7 13L5.4 5M7 13l-2.293 2.293c-.63.63-.184 1.707.707 1.707H17m0 0a2 2 0 100 4 2 2 0 000-4zm-8 2a2 2 0 11-4 0 2 2 0 014 0z"
                      />
                    </svg>
                    {cartItemCount > 0 && (
                      <span className="absolute top-0 right-0 inline-flex items-center justify-center px-2 py-1 text-xs font-bold leading-none text-white transform translate-x-1/2 -translate-y-1/2 bg-red-600 rounded-full">
                        {cartItemCount}
                      </span>
                    )}
                  </button>
                )}

                {/* Profile Menu */}
                <div className="relative">
                  <button
                    onClick={() => setProfileMenuOpen(!profileMenuOpen)}
                    className="flex items-center gap-2 px-3 py-2 text-neutral-700 hover:text-primary-600 transition-colors"
                  >
                    <svg
                      className="w-6 h-6"
                      fill="none"
                      stroke="currentColor"
                      viewBox="0 0 24 24"
                    >
                      <path
                        strokeLinecap="round"
                        strokeLinejoin="round"
                        strokeWidth={2}
                        d="M16 7a4 4 0 11-8 0 4 4 0 018 0zM12 14a7 7 0 00-7 7h14a7 7 0 00-7-7z"
                      />
                    </svg>
                    <span className="text-sm font-medium">{userName}</span>
                    <svg
                      className={`w-4 h-4 transition-transform ${
                        profileMenuOpen ? "transform rotate-180" : ""
                      }`}
                      fill="none"
                      stroke="currentColor"
                      viewBox="0 0 24 24"
                    >
                      <path
                        strokeLinecap="round"
                        strokeLinejoin="round"
                        strokeWidth={2}
                        d="M19 14l-7 7m0 0l-7-7m7 7V3"
                      />
                    </svg>
                  </button>

                  {profileMenuOpen && (
                    <div className="absolute right-0 mt-2 w-48 bg-white rounded-lg shadow-lg border border-neutral-200 py-2 z-50">
                      {!isAdmin && (
                        <>
                          <button
                            onClick={() => {
                              onOpenProfile();
                              setProfileMenuOpen(false);
                            }}
                            className="block w-full text-left px-4 py-2 text-neutral-700 hover:bg-primary-50 hover:text-primary-600 transition-colors"
                          >
                            My Profile
                          </button>
                          <button
                            onClick={() => {
                              onOpenOrders();
                              setProfileMenuOpen(false);
                            }}
                            className="block w-full text-left px-4 py-2 text-neutral-700 hover:bg-primary-50 hover:text-primary-600 transition-colors"
                          >
                            My Orders
                          </button>
                        </>
                      )}
                      <div className="border-t border-neutral-200 my-2" />
                      <button
                        onClick={() => {
                          onLogout();
                          setProfileMenuOpen(false);
                        }}
                        className="block w-full text-left px-4 py-2 text-red-600 hover:bg-red-50 transition-colors"
                      >
                        Logout
                      </button>
                    </div>
                  )}
                </div>
              </>
            ) : (
              <>
                <button
                  onClick={() => onOpenAuth("customer-login")}
                  className="btn-ghost px-4 py-2"
                >
                  Sign In
                </button>
                <button
                  onClick={() => onOpenAuth("customer-register")}
                  className="btn-primary px-4 py-2"
                >
                  Register
                </button>
              </>
            )}
          </div>

          {/* Mobile Menu Button */}
          <div className="md:hidden flex items-center gap-2">
            {!isAdmin && isLoggedIn && (
              <button
                onClick={onOpenCart}
                className="relative p-2 text-neutral-600 hover:text-primary-600"
              >
                <svg
                  className="w-6 h-6"
                  fill="none"
                  stroke="currentColor"
                  viewBox="0 0 24 24"
                >
                  <path
                    strokeLinecap="round"
                    strokeLinejoin="round"
                    strokeWidth={2}
                    d="M3 3h2l.4 2M7 13h10l4-8H5.4M7 13L5.4 5M7 13l-2.293 2.293c-.63.63-.184 1.707.707 1.707H17m0 0a2 2 0 100 4 2 2 0 000-4zm-8 2a2 2 0 11-4 0 2 2 0 014 0z"
                  />
                </svg>
                {cartItemCount > 0 && (
                  <span className="absolute top-0 right-0 inline-flex items-center justify-center px-2 py-1 text-xs font-bold text-white bg-red-600 rounded-full">
                    {cartItemCount}
                  </span>
                )}
              </button>
            )}

            <button
              onClick={() => setMobileMenuOpen(!mobileMenuOpen)}
              className="p-2 text-neutral-600 hover:text-primary-600"
            >
              <svg
                className="w-6 h-6"
                fill="none"
                stroke="currentColor"
                viewBox="0 0 24 24"
              >
                <path
                  strokeLinecap="round"
                  strokeLinejoin="round"
                  strokeWidth={2}
                  d="M4 6h16M4 12h16M4 18h16"
                />
              </svg>
            </button>
          </div>
        </div>

        {/* Mobile Search Bar */}
        {!isAdmin && isLoggedIn && (
          <div className="md:hidden pb-4 px-0">
            <input
              type="text"
              placeholder="Search products..."
              className="form-input w-full"
            />
          </div>
        )}

        {/* Mobile Menu */}
        {mobileMenuOpen && (
          <div className="md:hidden border-t border-neutral-200 py-4 space-y-2">
            {isLoggedIn ? (
              <>
                {!isAdmin && (
                  <>
                    <button
                      onClick={() => {
                        onOpenProfile();
                        setMobileMenuOpen(false);
                      }}
                      className="block w-full text-left px-4 py-2 text-neutral-700 hover:bg-primary-50 hover:text-primary-600"
                    >
                      My Profile
                    </button>
                    <button
                      onClick={() => {
                        onOpenOrders();
                        setMobileMenuOpen(false);
                      }}
                      className="block w-full text-left px-4 py-2 text-neutral-700 hover:bg-primary-50 hover:text-primary-600"
                    >
                      My Orders
                    </button>
                  </>
                )}
                <button
                  onClick={() => {
                    onLogout();
                    setMobileMenuOpen(false);
                  }}
                  className="block w-full text-left px-4 py-2 text-red-600 hover:bg-red-50"
                >
                  Logout
                </button>
              </>
            ) : (
              <>
                <button
                  onClick={() => {
                    onOpenAuth("customer-login");
                    setMobileMenuOpen(false);
                  }}
                  className="block w-full text-left px-4 py-2 text-primary-600 hover:bg-primary-50"
                >
                  Sign In
                </button>
                <button
                  onClick={() => {
                    onOpenAuth("customer-register");
                    setMobileMenuOpen(false);
                  }}
                  className="block w-full text-left px-4 py-2 text-primary-600 hover:bg-primary-50"
                >
                  Register
                </button>
              </>
            )}
          </div>
        )}
      </div>
    </nav>
  );
}
