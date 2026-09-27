import { useState } from "react";
import { Link, NavLink, useNavigate, useLocation } from "react-router-dom";
import { isAdmin } from "../utils/auth";
import { Icon } from "./UI";
export default function Navbar() {
  const [menuPath, setMenuPath] = useState(null);
  const { pathname } = useLocation();
  const open = menuPath === pathname;
  const navigate = useNavigate();
  const token = localStorage.getItem("token");
  const name = localStorage.getItem("name");
  function logout() {
    ["token", "userId", "name", "email"].forEach((key) =>
      localStorage.removeItem(key),
    );
    setMenuPath(null);
    navigate("/login");
  }
  return (
    <>
      <a className="skip-link" href="#main-content">
        Skip to content
      </a>
      <header className="site-header">
        <nav className="navbar" aria-label="Main navigation">
          <Link className="logo" to="/" onClick={() => setMenuPath(null)}>
            <span className="brand-mark">
              <Icon />
            </span>
            fixly<span className="brand-dot">.</span>
          </Link>
          <button
            className="menu-toggle"
            aria-label={open ? "Close menu" : "Open menu"}
            aria-expanded={open}
            aria-controls="main-navigation"
            onClick={() => setMenuPath(open ? null : pathname)}
          >
            <Icon name={open ? "close" : "menu"} />
          </button>
          <div
            className={`nav-links ${open ? "is-open" : ""}`}
            id="main-navigation"
            onClick={(e) => {
              if (e.target.closest("a")) setMenuPath(null);
            }}
          >
            <NavLink to="/" end>
              Home
            </NavLink>
            <NavLink to="/services">Services</NavLink>
            {token ? (
              <>
                <NavLink to="/my-bookings">My bookings</NavLink>
                {isAdmin() && <NavLink to="/admin">Dashboard</NavLink>}
                <span className="nav-user">
                  <span className="avatar">{name?.charAt(0) || "F"}</span>
                  {name?.split(" ")[0] || "Account"}
                </span>
                <button className="secondary-button" onClick={logout}>
                  Log out
                </button>
              </>
            ) : (
              <>
                <NavLink to="/login" className="nav-login">
                  Log in
                </NavLink>
                <Link to="/register" className="primary-button">
                  Get started
                  <Icon name="arrow" size={16} />
                </Link>
              </>
            )}
          </div>
        </nav>
      </header>
    </>
  );
}
