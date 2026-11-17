import { useState } from "react";
import { useNavigate, Link } from "react-router-dom";
import { loginUser } from "../api/authApi";
import { useAuth } from "../context/AuthContext";
import styles from "../styles/auth.module.css";

export default function AdminLoginPage() {
  const navigate = useNavigate();
  const { login } = useAuth();
  const [form, setForm] = useState({ email: "", password: "" });
  const [errors, setErrors] = useState({});
  const [loading, setLoading] = useState(false);

  const handleChange = (event) => {
    setForm({ ...form, [event.target.name]: event.target.value });
    setErrors({ ...errors, [event.target.name]: "" });
  };

  const handleSubmit = async (event) => {
    event.preventDefault();
    setLoading(true);
    try {
      const res = await loginUser(form);
      const { token, ...userData } = res.data;

      if (userData.role !== "ADMIN") {
        setErrors({ general: "This account does not have administrator access." });
        return;
      }

      login(userData, token);
      navigate("/admin/products");
    } catch (err) {
      const data = err.response?.data;
      if (!err.response) {
        setErrors({ general: "Backend server is not running on port 8081" });
      } else if (data && typeof data === "object") {
        setErrors(data.error ? { general: data.error } : data);
      } else {
        setErrors({ general: "Invalid email or password" });
      }
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className={styles.authPage}>
      <section className={styles.brandPanel}>
        <span className={styles.brandMark}>Mercato</span>
        <div>
          <p className={styles.brandKicker}>Administrator access</p>
          <h1 className={styles.brandTitle}>Manage your store from one secure place.</h1>
        </div>
        <div className={styles.brandStats}>
          <div><strong>Products</strong><span>catalogue controls</span></div>
          <div><strong>Users</strong><span>customer overview</span></div>
        </div>
      </section>

      <section className={styles.formPanel}>
        <div className={styles.formHeader}>
          <p className={styles.eyebrow}>Admin sign in</p>
          <h2 className={styles.title}>Welcome, administrator</h2>
          <p className={styles.subtitle}>Use an account that has administrator access.</p>
        </div>
        {errors.general && <p className={styles.errorBanner}>{errors.general}</p>}
        <form onSubmit={handleSubmit} className={styles.form}>
          <div className={styles.field}>
            <label htmlFor="admin-login-email">Email</label>
            <input id="admin-login-email" name="email" type="email" placeholder="admin@example.com" value={form.email} onChange={handleChange} required />
            {errors.email && <span className={styles.error}>{errors.email}</span>}
          </div>
          <div className={styles.field}>
            <label htmlFor="admin-login-password">Password</label>
            <input id="admin-login-password" name="password" type="password" placeholder="Enter password" value={form.password} onChange={handleChange} required />
            {errors.password && <span className={styles.error}>{errors.password}</span>}
          </div>
          <div className={styles.formMeta}>
            <Link to="/forgot-password">Forgot password?</Link>
          </div>
          <button type="submit" className={styles.btn} disabled={loading}>
            {loading ? "Checking..." : "Sign in to Admin Panel"}
          </button>
        </form>
        <p className={styles.switchLink}>
          Not an administrator? <Link to="/login">Customer login</Link>
        </p>
      </section>
    </div>
  );
}
