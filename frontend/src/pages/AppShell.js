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

function AppShell() {
  const [authView, setAuthView] = useState("admin-login");
  const [session, setSession] = useState({ token: "", name: "", email: "", role: "" });
  const [userForm, setUserForm] = useState(EMPTY_USER_FORM);
  const [productForm, setProductForm] = useState(EMPTY_PRODUCT_FORM);
  const [products, setProducts] = useState([]);
  const [editingProductId, setEditingProductId] = useState(null);
  const [isSubmittingAuth, setIsSubmittingAuth] = useState(false);
  const [isLoadingProducts, setIsLoadingProducts] = useState(false);
  const [isSavingProduct, setIsSavingProduct] = useState(false);
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
    };

    restoreWorkspace();
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, []);

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
    setAuthView("admin-login");
    toast.info("Logged out.");
  };

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
          products={products}
          session={session}
          storeMessage={storeMessage}
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
