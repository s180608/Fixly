import { useEffect, useState } from "react";
import { Link, useNavigate, useParams } from "react-router-dom";
import { apiRequest } from "../api/api";
import { ServiceArt, Icon, ErrorNotice, Loading } from "../components/UI";
import { categoryLabel, money } from "../utils/display";
export default function BookService() {
  const { serviceId } = useParams();
  const navigate = useNavigate();
  const [service, setService] = useState(null),
    [loading, setLoading] = useState(true),
    [busy, setBusy] = useState(false),
    [error, setError] = useState("");
  const [form, setForm] = useState({
    bookingDate: "",
    bookingTime: "",
    address: "",
  });
  async function loadService() {
    setLoading(true);
    setError("");
    try {
      setService(await apiRequest(`/services/${serviceId}`));
    } catch {
      setService(null);
      setError("We couldn’t load this service. Please try again.");
    } finally {
      setLoading(false);
    }
  }
  useEffect(() => {
    let active = true;
    apiRequest(`/services/${serviceId}`)
      .then((data) => {
        if (active) setService(data);
      })
      .catch(() => {
        if (active)
          setError("We couldn’t load this service. Please try again.");
      })
      .finally(() => {
        if (active) setLoading(false);
      });
    return () => {
      active = false;
    };
  }, [serviceId]);
  async function submit(e) {
    e.preventDefault();
    if (busy || !service) return;
    const userId = localStorage.getItem("userId");
    if (!userId) {
      navigate("/login");
      return;
    }
    setBusy(true);
    setError("");
    try {
      await apiRequest("/bookings", {
        method: "POST",
        body: JSON.stringify({
          ...form,
          user: { id: Number(userId) },
          service: { id: Number(serviceId) },
        }),
      });
      navigate("/my-bookings", { state: { booked: true } });
    } catch (err) {
      setError(err.message);
    } finally {
      setBusy(false);
    }
  }
  const today = new Date();
  const minDate = `${today.getFullYear()}-${String(today.getMonth() + 1).padStart(2, "0")}-${String(today.getDate()).padStart(2, "0")}`;
  return (
    <main id="main-content" className="section booking-page">
      <Link to="/services" className="back-link">
        ← Back to services
      </Link>
      <div className="page-heading">
        <p className="eyebrow">LET’S GET IT SORTED</p>
        <h1>A little help, on your terms.</h1>
        <p>Choose when and where. We’ll keep you updated.</p>
      </div>
      {loading ? (
        <Loading label="Loading your service…" />
      ) : !service ? (
        <ErrorNotice message={error} retry={loadService} />
      ) : (
        <div className="booking-layout">
          <form className="form-card booking-form" onSubmit={submit}>
            <div className="form-section-title">
              <span>01</span>
              <div>
                <h2>Make it fit your day</h2>
                <p>Choose your preferred date and time.</p>
              </div>
            </div>
            <ErrorNotice message={error} />
            <div className="field-row">
              {[
                ["bookingDate", "Preferred date", "date"],
                ["bookingTime", "Preferred time", "time"],
              ].map(([name, label, type]) => (
                <div className="field" key={name}>
                  <label htmlFor={name}>{label}</label>
                  <input
                    id={name}
                    type={type}
                    min={type === "date" ? minDate : undefined}
                    value={form[name]}
                    onChange={(e) =>
                      setForm({ ...form, [name]: e.target.value })
                    }
                    required
                  />
                </div>
              ))}
            </div>
            <div className="form-section-title second">
              <span>02</span>
              <div>
                <h2>Where are we heading?</h2>
                <p>Include your postcode so we can find you.</p>
              </div>
            </div>
            <div className="field">
              <label htmlFor="address">Service address</label>
              <textarea
                id="address"
                autoComplete="street-address"
                rows="3"
                placeholder="House number, street, town and postcode"
                value={form.address}
                onChange={(e) => setForm({ ...form, address: e.target.value })}
                required
              />
            </div>
            <div className="info-note">
              <Icon name="clock" size={20} />
              <p>
                Your booking will start as pending. Check My bookings for
                confirmation.
              </p>
            </div>
            <button className="primary-button full-width" disabled={busy}>
              {busy ? "Submitting booking…" : "Request booking"}
              <Icon name="arrow" size={18} />
            </button>
          </form>
          <aside className="booking-summary">
            <ServiceArt category={service.category} />
            <div>
              <p className="eyebrow">YOUR SELECTED SERVICE</p>
              <span className="category">
                {categoryLabel(service.category)}
              </span>
              <h2>{service.name}</h2>
              <p>{service.description}</p>
              <div className="summary-price">
                <span>Service price</span>
                <strong>{money(service.price)}</strong>
              </div>
              <small>You can cancel a pending booking from My bookings.</small>
            </div>
          </aside>
        </div>
      )}
    </main>
  );
}
