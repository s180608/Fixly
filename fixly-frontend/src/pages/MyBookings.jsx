import { useEffect, useState } from "react";
import { Link, useLocation } from "react-router-dom";
import { apiRequest } from "../api/api";
import { Status, Icon, ErrorNotice, Loading, Empty } from "../components/UI";
import { money, dateLabel } from "../utils/display";
export default function MyBookings() {
  const [bookings, setBookings] = useState([]),
    [error, setError] = useState(""),
    [loading, setLoading] = useState(true),
    [busy, setBusy] = useState(null),
    [filter, setFilter] = useState("ALL");
  const location = useLocation();
  async function loadBookings() {
    setLoading(true);
    setError("");
    try {
      setBookings(
        await apiRequest(`/bookings/user/${localStorage.getItem("userId")}`),
      );
    } catch {
      setError("We couldn’t load your bookings. Please try again.");
    } finally {
      setLoading(false);
    }
  }
  useEffect(() => {
    let active = true;
    apiRequest(`/bookings/user/${localStorage.getItem("userId")}`)
      .then((data) => {
        if (active) setBookings(data);
      })
      .catch(() => {
        if (active)
          setError("We couldn’t load your bookings. Please try again.");
      })
      .finally(() => {
        if (active) setLoading(false);
      });
    return () => {
      active = false;
    };
  }, []);
  async function cancelBooking(id) {
    if (
      busy ||
      !window.confirm("Are you sure you want to cancel this booking?")
    )
      return;
    setBusy(id);
    setError("");
    try {
      await apiRequest(`/bookings/${id}/cancel`, { method: "PUT" });
      await loadBookings();
    } catch (err) {
      setError(err.message);
    } finally {
      setBusy(null);
    }
  }
  const upcoming = bookings.filter((b) =>
    ["PENDING", "CONFIRMED"].includes(b.status),
  ).length;
  const visible = bookings.filter(
    (b) =>
      filter === "ALL" ||
      (filter === "UPCOMING"
        ? ["PENDING", "CONFIRMED"].includes(b.status)
        : ["COMPLETED", "CANCELLED"].includes(b.status)),
  );
  return (
    <main id="main-content" className="section bookings-page">
      <div className="section-heading">
        <div className="page-heading">
          <p className="eyebrow">YOUR HOME, TAKEN CARE OF</p>
          <h1>My bookings</h1>
          <p>All your home’s little jobs, in one place.</p>
        </div>
        <Link className="primary-button" to="/services">
          Book a service
          <Icon name="arrow" size={18} />
        </Link>
      </div>
      {location.state?.booked && (
        <div className="success" role="status">
          Booking requested. You can follow its progress here.
        </div>
      )}
      <div className="booking-overview">
        <Icon name="calendar" size={30} />
        <div>
          <strong>
            {loading
              ? "Checking your bookings…"
              : `${upcoming} upcoming ${upcoming === 1 ? "booking" : "bookings"}`}
          </strong>
          <p>Pending requests and confirmed visits will appear below.</p>
        </div>
      </div>
      <div className="filter-tabs">
        {[
          ["ALL", "All bookings"],
          ["UPCOMING", "Upcoming"],
          ["PAST", "Past bookings"],
        ].map(([value, label]) => (
          <button
            key={value}
            className={filter === value ? "selected" : ""}
            aria-pressed={filter === value}
            onClick={() => setFilter(value)}
          >
            {label}
          </button>
        ))}
      </div>
      <ErrorNotice message={error} retry={loadBookings} />
      {loading ? (
        <Loading label="Loading your bookings…" />
      ) : !error && !visible.length ? (
        <Empty
          title={
            bookings.length
              ? "Nothing here just yet"
              : "Your home’s next chapter starts here"
          }
          text={
            bookings.length
              ? "Bookings in this category will appear here."
              : "Find a service and let’s tick something off your list."
          }
        />
      ) : (
        <div className="booking-list">
          {visible.map((b) => (
            <article className="booking-card" key={b.id}>
              <div className="booking-icon">
                <Icon name="tool" size={26} />
              </div>
              <div className="booking-details">
                <div className="booking-meta">
                  <span>BOOKING #{b.id}</span>
                  <Status status={b.status} />
                </div>
                <h2>{b.serviceName}</h2>
                <p>
                  <Icon name="calendar" size={17} />
                  {dateLabel(b.bookingDate)}
                  <span>·</span>
                  {b.bookingTime?.slice(0, 5)}
                </p>
                <p>
                  <Icon name="pin" size={17} />
                  {b.address}
                </p>
                <small className="booking-hint">
                  {
                    {
                      PENDING: "Awaiting confirmation",
                      CONFIRMED: "Your booking is confirmed",
                      COMPLETED:
                        "All taken care of. Thanks for choosing Fixly.",
                      CANCELLED: "This booking has been cancelled.",
                    }[b.status]
                  }
                </small>
              </div>
              <div className="booking-side">
                <strong>{money(b.servicePrice)}</strong>
                {b.status === "PENDING" && (
                  <button
                    className="danger-button"
                    disabled={busy !== null}
                    onClick={() => cancelBooking(b.id)}
                  >
                    {busy === b.id ? "Cancelling…" : "Cancel booking"}
                  </button>
                )}
              </div>
            </article>
          ))}
        </div>
      )}
    </main>
  );
}
