# E-commerce Full Stack

This workspace contains the migrated e-commerce application and is designed to be operated entirely from VS Code.

## Projects

- `backend/` — Java 17, Spring Boot, Maven, MySQL, JWT, Razorpay
- `frontend/` — React, Vite, Tailwind CSS

## First-time setup

1. Start MySQL and import the reference database schema and sample data:

   ```powershell
   mysql -u root -p < database/ecommerce_db.sql
   ```

   This script recreates the `ecommerce_db` tables, so use it only for this local development database.

2. Create `backend/application-local.properties` by copying `backend/application-local.example.properties`. Add only your local Razorpay Test Mode values if you want online payment testing.
3. Configure the database with environment variables, or edit `backend/src/main/resources/application.properties` for local-only use:

   ```powershell
   $env:DB_USERNAME = "root"
   $env:DB_PASSWORD = "your_mysql_password"
   $env:JWT_SECRET = "a-long-random-secret-of-at-least-32-characters"
   ```

4. Open `E-commerce_Springboot.code-workspace` in VS Code.
5. Use **Tasks: Run Task** → **Start full stack**.

The backend runs on `http://localhost:8081` and the frontend runs on `http://localhost:5173`.

## Razorpay Test Mode

Online checkout is integrated with Razorpay Test Mode. Add your credentials only to
`backend/application-local.properties` (never to the frontend):

```properties
razorpay.key.id=rzp_test_your_key_id
razorpay.key.secret=your_test_key_secret
```

Restart the backend after saving. Sign in, add an item to the cart, choose **Online
Payment** at checkout, and complete the test payment in the Razorpay Checkout window.
The backend verifies the Razorpay signature before marking the order as paid.

## VS Code commands

- **Backend: run Spring Boot** — start only the API
- **Frontend: run Vite** — start only the React app
- **Start full stack** — start both
- **Debug backend + start frontend** — run backend with the Java debugger and start the UI
