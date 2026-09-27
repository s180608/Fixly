import { useState } from "react";
import { Link, useLocation, useNavigate } from "react-router-dom";
import { apiRequest } from "../api/api";
import { ErrorNotice, Icon } from "./UI";
export default function AuthPage({ register = false }) {
  const navigate = useNavigate(),
    location = useLocation();
  const [form, setForm] = useState(
    register
      ? { name: "", email: "", phone: "", password: "" }
      : { email: "", password: "" },
  );
  const [error, setError] = useState(""),
    [busy, setBusy] = useState(false),
    [showPassword, setShowPassword] = useState(false);
  async function submit(e) {
    e.preventDefault();
    if (busy) return;
    setBusy(true);
    setError("");
    try {
      const data = await apiRequest(register ? "/users" : "/auth/login", {
        method: "POST",
        body: JSON.stringify(form),
      });
      if (register) navigate("/login", { state: { registered: true } });
      else {
        ["token", "userId", "name", "email"].forEach((key) =>
          localStorage.setItem(key, data[key]),
        );
        navigate("/services");
      }
    } catch (err) {
      setError(
        register
          ? err.message
          : "We couldn’t log you in. Check your email and password, and try again.",
      );
    } finally {
      setBusy(false);
    }
  }
  return (
    <main id="main-content" className="auth-page">
      <aside className="auth-story">
        <p className="eyebrow">WELCOME TO A LITTLE LESS TO DO</p>
        <h2>
          Your home.
          <br />
          Your happy place.
        </h2>
        <p>
          Keep the small jobs from becoming big ones.
          <br />A helping hand is just a booking away.
        </p>
        <img
          src="/art/home.svg"
          alt="A welcoming home with a teal door and leafy garden"
        />
        <div>
          <Icon name="check" size={18} />
          Simple booking <span>·</span> Clear prices <span>·</span> One place
        </div>
      </aside>
      <div className="auth-form-wrap">
        <form className="form-card" onSubmit={submit}>
          <span className="form-symbol">
            <Icon name={register ? "home" : "shield"} size={26} />
          </span>
          <p className="eyebrow">
            {register ? "MAKE YOURSELF AT HOME" : "GOOD TO SEE YOU AGAIN"}
          </p>
          <h1>{register ? "A fresh start for your home." : "Welcome back."}</h1>
          <p>
            {register
              ? "Create your account. We’ll help with the to-do list."
              : "Log in to book a service or see how things are going."}
          </p>
          {!register && location.state?.registered && (
            <div className="success" role="status">
              Your account is ready. Log in to get started.
            </div>
          )}
          <ErrorNotice message={error} />
          {Object.keys(form).map((name) => (
            <div className="field" key={name}>
              <label htmlFor={name}>
                {
                  {
                    name: "Full name",
                    email: "Email address",
                    phone: "Phone number",
                    password: "Password",
                  }[name]
                }
              </label>
              <div className={name === "password" ? "password-field" : ""}>
                <input
                  id={name}
                  name={name}
                  type={
                    name === "password"
                      ? showPassword
                        ? "text"
                        : "password"
                      : name === "email"
                        ? "email"
                        : name === "phone"
                          ? "tel"
                          : "text"
                  }
                  autoComplete={
                    {
                      name: "name",
                      email: "email",
                      phone: "tel",
                      password: register ? "new-password" : "current-password",
                    }[name]
                  }
                  placeholder={
                    {
                      name: "Alex Taylor",
                      email: "you@example.com",
                      phone: "Your contact number",
                      password: register
                        ? "Choose a password"
                        : "Enter your password",
                    }[name]
                  }
                  value={form[name]}
                  onChange={(e) => setForm({ ...form, [name]: e.target.value })}
                  required
                />
                {name === "password" && (
                  <button
                    type="button"
                    className="password-toggle"
                    aria-label={
                      showPassword ? "Hide password" : "Show password"
                    }
                    onClick={() => setShowPassword(!showPassword)}
                  >
                    {showPassword ? "Hide" : "Show"}
                  </button>
                )}
              </div>
            </div>
          ))}
          <button
            disabled={busy}
            className="primary-button full-width"
            type="submit"
          >
            {busy
              ? register
                ? "Creating account…"
                : "Logging in…"
              : register
                ? "Create account"
                : "Log in"}
            <Icon name="arrow" size={18} />
          </button>
          <p className="auth-switch">
            {register ? "Already feel at home?" : "New to Fixly?"}{" "}
            <Link to={register ? "/login" : "/register"}>
              {register ? "Log in" : "Create an account"}
            </Link>
          </p>
        </form>
      </div>
    </main>
  );
}
