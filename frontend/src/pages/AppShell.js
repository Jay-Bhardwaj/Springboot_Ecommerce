import React, { useEffect, useState } from "react";
import { ToastContainer, toast } from "react-toastify";
import "react-toastify/dist/ReactToastify.css";
import AuthPage from "./AuthPage";
import DashboardPage from "./DashboardPage";

const API_BASE_URL = "http://localhost:8080";
const EMPTY_USER_FORM = { name: "", email: "", password: "" };
const EMPTY_PRODUCT_FORM = {
  name: "",
  description: "",
  price: "",
  category: "",
  imageUrl: "",
  stockQuantity: "",
};
const EMPTY_CUSTOMER_FILTERS = {
  search: "",
  category: "all",
  minPrice: "",
  maxPrice: "",
};
const GST_RATE = 0.18;
const DELIVERY_CHARGE = 40;
const FREE_DELIVERY_THRESHOLD = 500;

function AppShell() {
  const [authView, setAuthView] = useState("admin-login");
  const [session, setSession] = useState({ token: "", name: "", email: "", role: "" });
  const [userForm, setUserForm] = useState(EMPTY_USER_FORM);
  const [productForm, setProductForm] = useState(EMPTY_PRODUCT_FORM);
  const [products, setProducts] = useState([]);
  const [customerFilters, setCustomerFilters] = useState(EMPTY_CUSTOMER_FILTERS);
  const [cartItems, setCartItems] = useState([]);
  const [selectedProductId, setSelectedProductId] = useState(null);
  const [editingProductId, setEditingProductId] = useState(null);
  const [isSubmittingAuth, setIsSubmittingAuth] = useState(false);
  const [isLoadingProducts, setIsLoadingProducts] = useState(false);
  const [isSavingProduct, setIsSavingProduct] = useState(false);
  const [isLoadingCart, setIsLoadingCart] = useState(false);
  const [storeMessage, setStoreMessage] = useState("Sign in to open your ecommerce workspace.");

  const fetchDashboardGreeting = async (token) => {
    try {
      const response = await fetch(`${API_BASE_URL}/user/dashboard`, {
        headers: { Authorization: `Bearer ${token}` },
      });

      if (!response.ok) {
        return;
      }

      const message = await response.text();
      setStoreMessage(message);
    } catch (error) {
      setStoreMessage("Your session is active. Backend greeting will appear when available.");
    }
  };

  const fetchProducts = async (token = session.token) => {
    if (!token) {
      return;
    }

    setIsLoadingProducts(true);

    try {
      const response = await fetch(`${API_BASE_URL}/products`, {
        headers: { Authorization: `Bearer ${token}` },
      });

      if (!response.ok) {
        throw new Error("Unable to load products");
      }

      const data = await response.json();
      setProducts(data);
    } catch (error) {
      toast.error("Could not load products.");
    } finally {
      setIsLoadingProducts(false);
    }
  };

  // ✅ FETCH CART FROM BACKEND
  const fetchCartFromBackend = async (token = session.token) => {
    if (!token) {
      return;
    }

    setIsLoadingCart(true);

    try {
      const response = await fetch(`${API_BASE_URL}/cart`, {
        headers: { Authorization: `Bearer ${token}` },
      });

      if (!response.ok) {
        throw new Error("Unable to load cart");
      }

      const cartData = await response.json();
      // Convert backend response to frontend format
      const formattedItems = cartData.items.map((item) => ({
        productId: item.productId,
        quantity: item.quantity,
      }));
      setCartItems(formattedItems);
    } catch (error) {
      console.error("Could not load cart:", error);
      // Silently fail on cart load to not interrupt user experience
    } finally {
      setIsLoadingCart(false);
    }
  };

  // ✅ ADD TO CART WITH BACKEND API
  const handleAddToCart = async (productId, quantity = 1) => {
    const product = products.find((item) => item.id === productId);

    if (!product) {
      return;
    }

    const availableStock = Number(product.stockQuantity || 0);

    if (availableStock <= 0) {
      toast.error("This product is currently out of stock.");
      return;
    }

    try {
      const response = await fetch(`${API_BASE_URL}/cart/items`, {
        method: "POST",
        headers: {
          "Content-Type": "application/json",
          Authorization: `Bearer ${session.token}`,
        },
        body: JSON.stringify({ productId, quantity }),
      });

      if (!response.ok) {
        const error = await response.text();
        throw new Error(error || "Failed to add to cart");
      }

      const cartData = await response.json();
      // Update local state with backend response
      const formattedItems = cartData.items.map((item) => ({
        productId: item.productId,
        quantity: item.quantity,
      }));
      setCartItems(formattedItems);
      toast.success(`${product.name} added to cart.`);
    } catch (error) {
      toast.error(error.message || "Could not add item to cart.");
    }
  };

  // ✅ UPDATE CART QUANTITY WITH BACKEND API
  const handleUpdateCartQuantity = async (productId, nextQuantity) => {
    const product = products.find((item) => item.id === productId);

    if (!product) {
      return;
    }

    const availableStock = Number(product.stockQuantity || 0);
    const normalizedQuantity = Math.min(Math.max(nextQuantity, 0), availableStock);

    try {
      if (normalizedQuantity === 0) {
        // If quantity is 0, remove from cart
        await handleRemoveCartItem(productId);
        return;
      }

      const response = await fetch(`${API_BASE_URL}/cart/items/${productId}`, {
        method: "PUT",
        headers: {
          "Content-Type": "application/json",
          Authorization: `Bearer ${session.token}`,
        },
        body: JSON.stringify({ quantity: normalizedQuantity }),
      });

      if (!response.ok) {
        throw new Error("Failed to update cart");
      }

      const cartData = await response.json();
      // Update local state with backend response
      const formattedItems = cartData.items.map((item) => ({
        productId: item.productId,
        quantity: item.quantity,
      }));
      setCartItems(formattedItems);
    } catch (error) {
      toast.error(error.message || "Could not update cart item.");
    }
  };

  // ✅ REMOVE FROM CART WITH BACKEND API
  const handleRemoveCartItem = async (productId) => {
    try {
      const response = await fetch(`${API_BASE_URL}/cart/items/${productId}`, {
        method: "DELETE",
        headers: { Authorization: `Bearer ${session.token}` },
      });

      if (!response.ok) {
        throw new Error("Failed to remove from cart");
      }

      const cartData = await response.json();
      // Update local state with backend response
      const formattedItems = cartData.items.map((item) => ({
        productId: item.productId,
        quantity: item.quantity,
      }));
      setCartItems(formattedItems);
      toast.success("Item removed from cart.");
    } catch (error) {
      toast.error(error.message || "Could not remove item from cart.");
    }
  };

  useEffect(() => {
    const token = localStorage.getItem("token");
    const role = localStorage.getItem("userRole");
    const name = localStorage.getItem("userName");
    const email = localStorage.getItem("userEmail");

    if (!token || !role) {
      return;
    }

    const restoredSession = { token, role, name: name || "", email: email || "" };
    setSession(restoredSession);
    setStoreMessage(role === "ADMIN" ? "Admin workspace restored. Manage your products below." : "Customer session restored. Explore your catalog below.");

    const restoreWorkspace = async () => {
      await fetchProducts(token);
      await fetchDashboardGreeting(token);
      // ✅ FETCH CART ON SESSION RESTORE
      if (role === "CUSTOMER") {
        await fetchCartFromBackend(token);
      }
    };

    restoreWorkspace();
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, []);

  useEffect(() => {
    if (!selectedProductId) {
      return;
    }

    const matchingProduct = products.find((product) => product.id === selectedProductId);

    if (!matchingProduct) {
      setSelectedProductId(null);
      return;
    }
    const availableStock = Number(matchingProduct.stockQuantity || 0);

    setCartItems((current) =>
      current
        .map((item) =>
          item.productId === selectedProductId
            ? { ...item, quantity: Math.min(item.quantity, availableStock) }
            : item
        )
        .filter((item) => item.quantity > 0)
    );
  }, [products, selectedProductId]);

  const isLoggedIn = Boolean(session.token);
  const isAdmin = session.role === "ADMIN";

  const persistSession = (payload) => {
    const nextSession = {
      token: payload.token,
      name: payload.name || "",
      email: payload.email || "",
      role: payload.role || "CUSTOMER",
    };

    setSession(nextSession);
    localStorage.setItem("token", nextSession.token);
    localStorage.setItem("userRole", nextSession.role);
    localStorage.setItem("userName", nextSession.name);
    localStorage.setItem("userEmail", nextSession.email);
  };

  const clearSession = () => {
    localStorage.removeItem("token");
    localStorage.removeItem("userRole");
    localStorage.removeItem("userName");
    localStorage.removeItem("userEmail");
    setSession({ token: "", name: "", email: "", role: "" });
    setProducts([]);
    setCartItems([]);
    setSelectedProductId(null);
    setEditingProductId(null);
    setProductForm(EMPTY_PRODUCT_FORM);
    setStoreMessage("Sign in to open your ecommerce workspace.");
  };

  const handleUserInputChange = (event) => {
    const { name, value } = event.target;
    setUserForm((current) => ({ ...current, [name]: value }));
  };

  const handleProductInputChange = (event) => {
    const { name, value } = event.target;
    setProductForm((current) => ({ ...current, [name]: value }));
  };

  const handleCustomerFilterChange = (event) => {
    const { name, value } = event.target;
    setCustomerFilters((current) => ({ ...current, [name]: value }));
  };

  const resetCustomerFilters = () => {
    setCustomerFilters(EMPTY_CUSTOMER_FILTERS);
  };

  const handleSelectProduct = (productId) => {
    setSelectedProductId(productId);
  };

  const handleCloseProductDetails = () => {
    setSelectedProductId(null);
  };

  const switchAuthView = (nextView) => {
    setAuthView(nextView);
    setUserForm((current) => ({ ...EMPTY_USER_FORM, email: current.email }));
  };

  const handleRegister = async (event) => {
    event.preventDefault();
    setIsSubmittingAuth(true);

    try {
      const response = await fetch(`${API_BASE_URL}/user/register`, {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify(userForm),
      });

      const message = await response.text();

      if (!response.ok || !message.toLowerCase().includes("success")) {
        throw new Error(message || "Registration failed");
      }

      toast.success("Customer account created. Please log in.");
      setStoreMessage("Registration complete. Customer login is ready.");
      setUserForm(EMPTY_USER_FORM);
      setAuthView("customer-login");
    } catch (error) {
      toast.error(error.message || "Registration failed.");
    } finally {
      setIsSubmittingAuth(false);
    }
  };

  const loginWithRole = async (event, expectedRole) => {
    event.preventDefault();
    setIsSubmittingAuth(true);

    try {
      const response = await fetch(`${API_BASE_URL}/user/login`, {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({ email: userForm.email, password: userForm.password }),
      });

      const contentType = response.headers.get("content-type") || "";
      const payload = contentType.includes("application/json") ? await response.json() : { message: await response.text() };

      if (!response.ok) {
        throw new Error(payload.message || "Invalid credentials");
      }

      if (payload.role !== expectedRole) {
        throw new Error(expectedRole === "ADMIN" ? "This login is reserved for admin accounts." : "Please use the admin option for admin accounts.");
      }

      persistSession(payload);
      setUserForm(EMPTY_USER_FORM);
      await fetchProducts(payload.token);
      await fetchDashboardGreeting(payload.token);
      // ✅ FETCH CART AFTER LOGIN
      if (expectedRole === "CUSTOMER") {
        await fetchCartFromBackend(payload.token);
      }
      toast.success(expectedRole === "ADMIN" ? "Admin login successful." : "Customer login successful.");
    } catch (error) {
      toast.error(error.message || "Login failed.");
    } finally {
      setIsSubmittingAuth(false);
    }
  };

  const handleAdminLogin = (event) => loginWithRole(event, "ADMIN");
  const handleCustomerLogin = (event) => loginWithRole(event, "CUSTOMER");

  const resetProductForm = () => {
    setProductForm(EMPTY_PRODUCT_FORM);
    setEditingProductId(null);
  };

  const handleEditProduct = (product) => {
    setEditingProductId(product.id);
    setProductForm({
      name: product.name || "",
      description: product.description || "",
      price: product.price || "",
      category: product.category || "",
      imageUrl: product.imageUrl || "",
      stockQuantity: product.stockQuantity || "",
    });
  };

  const handleSaveProduct = async (event) => {
    event.preventDefault();

    if (!isAdmin) {
      toast.error("Only admins can manage products.");
      return;
    }

    setIsSavingProduct(true);

    const requestBody = {
      ...productForm,
      price: Number(productForm.price),
      stockQuantity: Number(productForm.stockQuantity),
    };

    try {
      const response = await fetch(editingProductId ? `${API_BASE_URL}/products/${editingProductId}` : `${API_BASE_URL}/products`, {
        method: editingProductId ? "PUT" : "POST",
        headers: {
          "Content-Type": "application/json",
          Authorization: `Bearer ${session.token}`,
        },
        body: JSON.stringify(requestBody),
      });

      if (!response.ok) {
        throw new Error("Could not save product");
      }

      toast.success(editingProductId ? "Product updated." : "Product created.");
      resetProductForm();
      fetchProducts();
    } catch (error) {
      toast.error(error.message || "Product save failed.");
    } finally {
      setIsSavingProduct(false);
    }
  };

  const handleDeleteProduct = async (productId) => {
    if (!isAdmin) {
      toast.error("Only admins can delete products.");
      return;
    }

    try {
      const response = await fetch(`${API_BASE_URL}/products/${productId}`, {
        method: "DELETE",
        headers: { Authorization: `Bearer ${session.token}` },
      });

      if (!response.ok) {
        throw new Error("Delete failed");
      }

      toast.success("Product deleted.");
      if (editingProductId === productId) {
        resetProductForm();
      }
      fetchProducts();
    } catch (error) {
      toast.error(error.message || "Could not delete product.");
    }
  };

  const handleLogout = () => {
    clearSession();
    resetCustomerFilters();
    setAuthView("admin-login");
    toast.info("Logged out.");
  };

  const availableCategories = Array.from(
    new Set(
      products
        .map((product) => (product.category || "").trim())
        .filter(Boolean)
    )
  ).sort((first, second) => first.localeCompare(second));

  const searchTerm = customerFilters.search.trim().toLowerCase();
  const minPrice = customerFilters.minPrice === "" ? null : Number(customerFilters.minPrice);
  const maxPrice = customerFilters.maxPrice === "" ? null : Number(customerFilters.maxPrice);

  const filteredProducts = products.filter((product) => {
    const matchesSearch =
      !searchTerm ||
      [product.name, product.description, product.category]
        .filter(Boolean)
        .some((value) => value.toLowerCase().includes(searchTerm));

    const matchesCategory =
      customerFilters.category === "all" || product.category === customerFilters.category;

    const productPrice = Number(product.price);
    const matchesMinPrice = minPrice === null || productPrice >= minPrice;
    const matchesMaxPrice = maxPrice === null || productPrice <= maxPrice;

    return matchesSearch && matchesCategory && matchesMinPrice && matchesMaxPrice;
  });

  const hasActiveCustomerFilters = Object.entries(customerFilters).some(([key, value]) =>
    key === "category" ? value !== "all" : value !== ""
  );

  const selectedProduct = products.find((product) => product.id === selectedProductId) || null;

  const cartDetails = cartItems
    .map((item) => {
      const product = products.find((entry) => entry.id === item.productId);

      if (!product) {
        return null;
      }

      const unitPrice = Number(product.price || 0);

      return {
        ...item,
        name: product.name,
        unitPrice,
        lineTotal: unitPrice * item.quantity,
      };
    })
    .filter(Boolean);

  const cartItemCount = cartDetails.reduce((sum, item) => sum + item.quantity, 0);
  const subtotal = cartDetails.reduce((sum, item) => sum + item.lineTotal, 0);
  const gstAmount = subtotal * GST_RATE;
  const deliveryCharge =
    subtotal > 0 && subtotal < FREE_DELIVERY_THRESHOLD ? DELIVERY_CHARGE : 0;
  const totalBill = subtotal + gstAmount + deliveryCharge;

  return (
    <>
      {isLoggedIn ? (
        <DashboardPage
          editingProductId={editingProductId}
          isAdmin={isAdmin}
          isLoadingProducts={isLoadingProducts}
          isSavingProduct={isSavingProduct}
          onDeleteProduct={handleDeleteProduct}
          onEditProduct={handleEditProduct}
          onLogout={handleLogout}
          onProductInputChange={handleProductInputChange}
          onRefreshProducts={fetchProducts}
          onResetProductForm={resetProductForm}
          onSaveProduct={handleSaveProduct}
          productForm={productForm}
          products={isAdmin ? products : filteredProducts}
          allProductsCount={products.length}
          availableCategories={availableCategories}
          cartItemCount={cartItemCount}
          cartDetails={cartDetails}
          customerFilters={customerFilters}
          deliveryCharge={deliveryCharge}
          freeDeliveryThreshold={FREE_DELIVERY_THRESHOLD}
          gstAmount={gstAmount}
          gstRate={GST_RATE}
          hasActiveCustomerFilters={hasActiveCustomerFilters}
          onAddToCart={handleAddToCart}
          onCustomerFilterChange={handleCustomerFilterChange}
          onCloseProductDetails={handleCloseProductDetails}
          onRemoveCartItem={handleRemoveCartItem}
          onResetCustomerFilters={resetCustomerFilters}
          onSelectProduct={handleSelectProduct}
          onUpdateCartQuantity={handleUpdateCartQuantity}
          selectedProduct={selectedProduct}
          session={session}
          storeMessage={storeMessage}
          subtotal={subtotal}
          totalBill={totalBill}
        />
      ) : (
        <AuthPage
          authView={authView}
          isSubmittingAuth={isSubmittingAuth}
          onAdminLogin={handleAdminLogin}
          onChange={handleUserInputChange}
          onCustomerLogin={handleCustomerLogin}
          onRegister={handleRegister}
          onSwitchView={switchAuthView}
          storeMessage={storeMessage}
          userForm={userForm}
        />
      )}
      <ToastContainer position="top-right" autoClose={2500} />
    </>
  );
}

export default AppShell;
