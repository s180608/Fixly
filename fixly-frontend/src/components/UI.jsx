import { Link } from "react-router-dom";
import { categories, categoryLabel } from "../utils/display";
export function Icon({ name = "home", size = 22, ...props }) {
  const paths = {
    home: "m3 10 9-7 9 7M5 9v12h14V9M9 21v-8h6v8",
    arrow: "M4 12h16m-6-6 6 6-6 6",
    check: "m5 12 4 4L19 6",
    clock: "M12 8v5l3 2M21 12a9 9 0 1 1-18 0 9 9 0 0 1 18 0",
    search: "m21 21-5-5M18 10a8 8 0 1 1-16 0 8 8 0 0 1 16 0",
    calendar: "M4 5h16v16H4zM8 3v4m8-4v4M4 10h16",
    shield: "m12 3 8 3v6c0 5-8 9-8 9s-8-4-8-9V6z m-4 9 3 3 5-5",
    tool: "m14 6 4 4 3-3c1 5-3 8-7 6l-7 8-4-4 8-7c-2-4 1-8 6-7z",
    menu: "M4 6h16M4 12h16M4 18h16",
    close: "m6 6 12 12M6 18 18 6",
    users:
      "M16 21v-3a4 4 0 0 0-4-4H6a4 4 0 0 0-4 4v3M13 6a4 4 0 1 1-8 0 4 4 0 0 1 8 0M17 3a4 4 0 0 1 0 8m1 3a4 4 0 0 1 4 4v3",
    pin: "M19 10c0 5-7 11-7 11S5 15 5 10a7 7 0 1 1 14 0M14 10a2 2 0 1 1-4 0 2 2 0 0 1 4 0",
  };
  return (
    <svg
      width={size}
      height={size}
      viewBox="0 0 24 24"
      fill="none"
      stroke="currentColor"
      strokeWidth="1.7"
      strokeLinecap="round"
      strokeLinejoin="round"
      aria-hidden="true"
      {...props}
    >
      <path d={paths[name] || paths.tool} />
    </svg>
  );
}
export function Status({ status }) {
  return (
    <span className={`status status-${(status || "").toLowerCase()}`}>
      <span aria-hidden="true" className="status-dot" />
      {categoryLabel(status)}
    </span>
  );
}
export function ErrorNotice({ message, retry }) {
  return message ? (
    <div className="error" role="alert">
      <span>{message}</span>
      {retry && (
        <button className="text-button" onClick={retry}>
          Try again
        </button>
      )}
    </div>
  ) : null;
}
export function Loading({ label = "Loading…" }) {
  return (
    <div className="loading" role="status">
      <span className="spinner" />
      {label}
    </div>
  );
}
export function Empty({
  title,
  text,
  action = "Explore services",
  to = "/services",
}) {
  return (
    <div className="empty-state">
      <span className="empty-icon">
        <Icon name="calendar" size={30} />
      </span>
      <h2>{title}</h2>
      <p>{text}</p>
      {action && (
        <Link className="primary-button" to={to}>
          {action}
          <Icon name="arrow" size={18} />
        </Link>
      )}
    </div>
  );
}
export function ServiceArt({ category, className = "", ...props }) {
  return (
    <img
      className={`service-art ${className}`}
      src={`/art/${categories.includes(category) ? category.toLowerCase() : "plumbing"}.svg`}
      alt=""
      loading="lazy"
      {...props}
    />
  );
}
export function Footer() {
  return (
    <footer className="site-footer">
      <div>
        <Link className="logo" to="/">
          <span className="brand-mark">
            <Icon size={20} />
          </span>
          fixly<span className="brand-dot">.</span>
        </Link>
        <p>A little help. A happier home.</p>
      </div>
      <div className="footer-links">
        <Link to="/services">Explore services</Link>
        <Link to="/my-bookings">My bookings</Link>
      </div>
      <span className="footer-note">
        Home services, made simple.
        <br />
        Built for everyday life in the UK.
      </span>
    </footer>
  );
}
