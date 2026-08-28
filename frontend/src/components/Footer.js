import React from "react";

export default function Footer() {
  const currentYear = new Date().getFullYear();

  return (
    <footer className="bg-neutral-900 text-neutral-300 mt-auto">
      {/* Main Footer */}
      <div className="container-max py-12 md:py-16">
        <div className="grid grid-cols-1 md:grid-cols-4 gap-8 md:gap-12">
          {/* Brand */}
          <div>
            <div className="text-2xl font-bold text-white mb-4">Store</div>
            <p className="text-sm text-neutral-400 mb-6">
              Your trusted online shopping destination for quality products at great prices.
            </p>
            <div className="flex gap-4">
              <a href="#" className="text-neutral-400 hover:text-primary-500 transition-colors">
                <svg className="w-5 h-5" fill="currentColor" viewBox="0 0 24 24">
                  <path d="M8.29 20v-7.21H5.73V9.25h2.56V7.69c0-2.54 1.55-3.93 3.83-3.93 1.09 0 2.02.08 2.29.12v2.65h-1.57c-1.24 0-1.48.59-1.48 1.45v1.9h2.95l-.39 3.54h-2.56V20" />
                </svg>
              </a>
              <a href="#" className="text-neutral-400 hover:text-primary-500 transition-colors">
                <svg className="w-5 h-5" fill="currentColor" viewBox="0 0 24 24">
                  <path d="M23 3a10.9 10.9 0 01-3.14 1.53 4.48 4.48 0 00-7.86 3v1A10.66 10.66 0 013 4s-4 9 5 13a11.64 11.64 0 01-7 2s9 5 20 5a9.5 9.5 0 00-9-5.5c4.75 2.25 7-7 7-7" />
                </svg>
              </a>
              <a href="#" className="text-neutral-400 hover:text-primary-500 transition-colors">
                <svg className="w-5 h-5" fill="currentColor" viewBox="0 0 24 24">
                  <rect x="2" y="2" width="20" height="20" rx="5" ry="5" fill="none" stroke="currentColor" strokeWidth="2"/>
                  <path d="M16 11.37A4 4 0 1112.63 8 4 4 0 0116 11.37z" stroke="currentColor" strokeWidth="2" fill="none"/>
                  <circle cx="17.5" cy="6.5" r="1.5" fill="currentColor"/>
                </svg>
              </a>
            </div>
          </div>

          {/* Shop */}
          <div>
            <h3 className="text-white font-semibold mb-4">Shop</h3>
            <ul className="space-y-2 text-sm">
              <li>
                <a href="#" className="text-neutral-400 hover:text-primary-500 transition-colors">
                  All Products
                </a>
              </li>
              <li>
                <a href="#" className="text-neutral-400 hover:text-primary-500 transition-colors">
                  New Arrivals
                </a>
              </li>
              <li>
                <a href="#" className="text-neutral-400 hover:text-primary-500 transition-colors">
                  Best Sellers
                </a>
              </li>
              <li>
                <a href="#" className="text-neutral-400 hover:text-primary-500 transition-colors">
                  Special Offers
                </a>
              </li>
              <li>
                <a href="#" className="text-neutral-400 hover:text-primary-500 transition-colors">
                  Gift Cards
                </a>
              </li>
            </ul>
          </div>

          {/* Support */}
          <div>
            <h3 className="text-white font-semibold mb-4">Support</h3>
            <ul className="space-y-2 text-sm">
              <li>
                <a href="#" className="text-neutral-400 hover:text-primary-500 transition-colors">
                  Contact Us
                </a>
              </li>
              <li>
                <a href="#" className="text-neutral-400 hover:text-primary-500 transition-colors">
                  Shipping Info
                </a>
              </li>
              <li>
                <a href="#" className="text-neutral-400 hover:text-primary-500 transition-colors">
                  Returns
                </a>
              </li>
              <li>
                <a href="#" className="text-neutral-400 hover:text-primary-500 transition-colors">
                  FAQ
                </a>
              </li>
              <li>
                <a href="#" className="text-neutral-400 hover:text-primary-500 transition-colors">
                  Track Order
                </a>
              </li>
            </ul>
          </div>

          {/* Legal */}
          <div>
            <h3 className="text-white font-semibold mb-4">Legal</h3>
            <ul className="space-y-2 text-sm">
              <li>
                <a href="#" className="text-neutral-400 hover:text-primary-500 transition-colors">
                  Privacy Policy
                </a>
              </li>
              <li>
                <a href="#" className="text-neutral-400 hover:text-primary-500 transition-colors">
                  Terms of Service
                </a>
              </li>
              <li>
                <a href="#" className="text-neutral-400 hover:text-primary-500 transition-colors">
                  Cookie Policy
                </a>
              </li>
              <li>
                <a href="#" className="text-neutral-400 hover:text-primary-500 transition-colors">
                  Accessibility
                </a>
              </li>
            </ul>
          </div>
        </div>
      </div>

      {/* Divider */}
      <div className="border-t border-neutral-800"></div>

      {/* Bottom Footer */}
      <div className="container-max py-8">
        <div className="flex flex-col md:flex-row justify-between items-center gap-4 text-sm text-neutral-400">
          <p>
            &copy; {currentYear} Store. All rights reserved.
          </p>
          <div className="flex gap-6">
            <a href="#" className="hover:text-primary-500 transition-colors">
              Secure Shopping
            </a>
            <a href="#" className="hover:text-primary-500 transition-colors">
              Buyer Protection
            </a>
          </div>
        </div>
      </div>
    </footer>
  );
}
