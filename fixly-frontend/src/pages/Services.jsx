import { useEffect, useState } from "react";
import { useNavigate, useSearchParams } from "react-router-dom";
import { apiRequest } from "../api/api";
import {
  ServiceArt,
  Icon,
  ErrorNotice,
  Loading,
  Empty,
} from "../components/UI";
import { categories, categoryLabel, money } from "../utils/display";
export default function Services() {
  const [services, setServices] = useState([]),
    [error, setError] = useState(""),
    [loading, setLoading] = useState(true),
    [search, setSearch] = useState("");
  const [params, setParams] = useSearchParams();
  const category = params.get("category") || "ALL";
  const navigate = useNavigate();
  async function loadServices() {
    setLoading(true);
    setError("");
    try {
      setServices(await apiRequest("/services"));
    } catch {
      setError("We couldn’t load the services. Please try again.");
    } finally {
      setLoading(false);
    }
  }
  useEffect(() => {
    let active = true;
    apiRequest("/services")
      .then((data) => {
        if (active) setServices(data);
      })
      .catch(() => {
        if (active)
          setError("We couldn’t load the services. Please try again.");
      })
      .finally(() => {
        if (active) setLoading(false);
      });
    return () => {
      active = false;
    };
  }, []);
  const filtered = services.filter(
    (s) =>
      (category === "ALL" || s.category === category) &&
      `${s.name} ${s.description} ${categoryLabel(s.category)}`
        .toLowerCase()
        .includes(search.toLowerCase()),
  );
  return (
    <main id="main-content" className="section services-page">
      <div className="catalogue-heading">
        <div>
          <p className="eyebrow">A LITTLE EXPERT HELP</p>
          <h1>
            Your home’s next
            <br />
            <span>fresh start.</span>
          </h1>
          <p>
            Practical help for the everyday and the overdue.
            <br />
            Explore our services and find your next fix.
          </p>
        </div>
        <div className="catalogue-illustration">
          <ServiceArt category="CLEANING" />
        </div>
      </div>
      <div className="catalogue-tools">
        <label className="search-field">
          <Icon name="search" size={20} />
          <input
            type="search"
            aria-label="Search services"
            placeholder="What needs taking care of?"
            value={search}
            onChange={(e) => setSearch(e.target.value)}
          />
        </label>
        <span>Find your fix. Make yourself at home.</span>
      </div>
      <div className="filter-tabs" aria-label="Filter services by category">
        {["ALL", ...categories].map((c) => (
          <button
            key={c}
            className={category === c ? "selected" : ""}
            aria-pressed={category === c}
            onClick={() => setParams(c === "ALL" ? {} : { category: c })}
          >
            {c === "ALL" ? "All services" : categoryLabel(c)}
          </button>
        ))}
      </div>
      <ErrorNotice message={error} retry={loadServices} />
      {loading ? (
        <Loading label="Finding services for your home…" />
      ) : (
        !error && (
          <>
            <div className="results-heading">
              <h2>
                {category === "ALL"
                  ? "Explore our services"
                  : categoryLabel(category)}
              </h2>
              <span>
                {filtered.length}{" "}
                {filtered.length === 1 ? "service" : "services"} available
              </span>
            </div>
            {filtered.length ? (
              <div className="service-grid">
                {filtered.map((s) => (
                  <article className="service-card" key={s.id}>
                    <div className="service-image">
                      <ServiceArt category={s.category} />
                      <span className="category">
                        {categoryLabel(s.category)}
                      </span>
                    </div>
                    <div className="service-body">
                      <h3>{s.name}</h3>
                      <p>{s.description}</p>
                      <div className="service-footer">
                        <div>
                          <small>Service price</small>
                          <strong>{money(s.price)}</strong>
                        </div>
                        <button
                          className="primary-button"
                          onClick={() =>
                            navigate(
                              localStorage.getItem("token")
                                ? `/book/${s.id}`
                                : "/login",
                            )
                          }
                        >
                          Book service
                          <Icon name="arrow" size={16} />
                        </button>
                      </div>
                    </div>
                  </article>
                ))}
              </div>
            ) : (
              <Empty
                title="No services found"
                text="Try another search or choose a different category."
                action={null}
              />
            )}
          </>
        )
      )}
      <div className="catalogue-note">
        <Icon name="calendar" />
        <div>
          <strong>Booking around your day</strong>
          <p>
            Choose your preferred date and time when you book. You can follow
            confirmation in My bookings.
          </p>
        </div>
      </div>
    </main>
  );
}
