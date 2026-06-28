/** @type {import('tailwindcss').Config} */
module.exports = {
  content: ["./src/**/*.{js,jsx,ts,tsx}"],
  corePlugins: {
    preflight: false,
  },
  theme: {
    extend: {
      colors: {
        mercato: {
          50: "#ecfffb",
          100: "#d8fff6",
          200: "#b0f7ea",
          300: "#7de9d5",
          400: "#42d7bc",
          500: "#18bd9f",
          600: "#006653",
          700: "#005646",
          800: "#034438",
          900: "#062f28",
        },
      },
      boxShadow: {
        mellow: "0 18px 40px rgba(8, 63, 52, 0.12)",
      },
    },
  },
  plugins: [],
};
