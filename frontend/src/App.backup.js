import React, { useState } from "react";
import { ToastContainer, toast } from "react-toastify";
import "react-toastify/dist/ReactToastify.css";

function App() {

  const [isLogin, setIsLogin] = useState(false);
  const [isLoggedIn, setIsLoggedIn] = useState(false);

  const [user, setUser] = useState({
    name: "",
    email: "",
    password: ""
  });

  const handleChange = (e) => {
    setUser({
      ...user,
      [e.target.name]: e.target.value
    });
  };

  // 🔥 REGISTER
  const handleRegister = async (e) => {
    e.preventDefault();

    const response = await fetch("http://localhost:8080/user/register", {
      method: "POST",
      headers: {
        "Content-Type": "application/json"
      },
      body: JSON.stringify(user)
    });

    if (response.ok) {
      toast.success("✅ Registered successfully!");
      setIsLogin(true);
    } else {
      toast.error("❌ Registration failed");
    }
  };

  // 🔥 LOGIN
const handleLogin = async (e) => {
  e.preventDefault();

  const response = await fetch("http://localhost:8080/user/login", {
    method: "POST",
    headers: {
      "Content-Type": "application/json"
    },
    body: JSON.stringify(user)
  });

  const result = await response.json();

  console.log("LOGIN RESPONSE:", result);

  if (response.ok) {
    localStorage.setItem("token", result.token);
    toast.success("✅ Login successful!");
    setIsLoggedIn(true);
  } else {
    toast.error("❌ Invalid credentials");
  }
};

  // 🎉 DASHBOARD
  if (isLoggedIn) {

    const fetchDashboard = async () => {
      const token = localStorage.getItem("token");

      const response = await fetch("http://localhost:8080/user/dashboard", {
        method: "GET",
        headers: {
          "Authorization": "Bearer " + token
        }
      });

      const data = await response.text();
      toast.info("📦 " + data);
    };

    return (
      <div style={{
        textAlign: "center",
        marginTop: "100px",
        fontFamily: "Arial"
      }}>
        <h1>🎉 Welcome to Dashboard</h1>
        <p>You are successfully logged in!</p>

        <button onClick={fetchDashboard}
          style={{ padding: "10px", margin: "10px" }}>
          Load User Data
        </button>

        <br />

        <button
          onClick={() => {
            localStorage.removeItem("token");
            setIsLoggedIn(false);
            toast.info("👋 Logged out");
          }}
          style={{
            padding: "10px",
            background: "red",
            color: "white",
            border: "none"
          }}
        >
          Logout
        </button>

        <ToastContainer />
      </div>
    );
  }

  return (
    <div style={{
      width: "350px",
      margin: "100px auto",
      padding: "20px",
      background: "white",
      borderRadius: "10px",
      boxShadow: "0 0 10px rgba(0,0,0,0.2)",
      textAlign: "center"
    }}>

      <h2>{isLogin ? "Login" : "Register"}</h2>

      <form onSubmit={isLogin ? handleLogin : handleRegister}>

        {!isLogin && (
          <input
            type="text"
            name="name"
            placeholder="Enter Name"
            onChange={handleChange}
            required
            style={{ width: "100%", padding: "10px", margin: "5px 0" }}
          />
        )}

        <input
          type="email"
          name="email"
          placeholder="Enter Email"
          onChange={handleChange}
          required
          style={{ width: "100%", padding: "10px", margin: "5px 0" }}
        />

        <input
          type="password"
          name="password"
          placeholder="Enter Password"
          onChange={handleChange}
          required
          style={{ width: "100%", padding: "10px", margin: "5px 0" }}
        />

        <button type="submit"
          style={{
            width: "100%",
            padding: "10px",
            marginTop: "10px",
            background: "#667eea",
            color: "white",
            border: "none"
          }}>
          {isLogin ? "Login" : "Register"}
        </button>

      </form>

      <p style={{ marginTop: "10px" }}>
        {isLogin ? "New user?" : "Already registered?"}
        <button
          onClick={() => setIsLogin(!isLogin)}
          style={{ marginLeft: "5px" }}>
          {isLogin ? "Register" : "Login"}
        </button>
      </p>

      <ToastContainer />
    </div>
  );
}

export default App;