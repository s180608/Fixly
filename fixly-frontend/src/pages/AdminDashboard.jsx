import { useEffect, useState } from "react";
import { apiRequest } from "../api/api";
import { Status, Icon, ErrorNotice, Loading, Empty } from "../components/UI";
import { categories, categoryLabel, money, dateLabel } from "../utils/display";
const blankService = {
  name: "",
  description: "",
  price: "",
  category: "PLUMBING",
};
export default function AdminDashboard() {
  const [bookings, setBookings] = useState([]),
    [users, setUsers] = useState([]),
    [services, setServices] = useState([]),
    [error, setError] = useState(""),
    [loading, setLoading] = useState(true),
    [busy, setBusy] = useState(null),
    [notice, setNotice] = useState(""),
    [tab, setTab] = useState("bookings"),
    [filter, setFilter] = useState("ALL"),
    [search, setSearch] = useState("");
  const [serviceForm, setServiceForm] = useState(blankService);
  async function loadData() {
    setLoading(true);
    setError("");
    try {
      const [b, u, s] = await Promise.all([
        apiRequest("/bookings"),
        apiRequest("/users"),
        apiRequest("/services"),
      ]);
      setBookings(b);
      setUsers(u);
      setServices(s);
    } catch {
      setError("We couldn’t load the dashboard. Please try again.");
    } finally {
      setLoading(false);
    }
  }
  useEffect(() => {
    let active = true;
    Promise.all([
      apiRequest("/bookings"),
      apiRequest("/users"),
      apiRequest("/services"),
    ])
      .then(([b, u, s]) => {
        if (active) {
          setBookings(b);
          setUsers(u);
          setServices(s);
        }
      })
      .catch(() => {
        if (active)
          setError("We couldn’t load the dashboard. Please try again.");
      })
      .finally(() => {
        if (active) setLoading(false);
      });
    return () => {
      active = false;
    };
  }, []);
  async function bookingAction(id, action) {
    if (busy) return;
    setBusy(`${action}-${id}`);
    setError("");
    setNotice("");
    try {
      await apiRequest(`/bookings/${id}/${action}`, { method: "PUT" });
      setNotice(
        `Booking #${id} ${action === "confirm" ? "confirmed" : "completed"}.`,
      );
      await loadData();
    } catch (err) {
      setError(err.message);
    } finally {
      setBusy(null);
    }
  }
  async function createService(e) {
    e.preventDefault();
    if (busy) return;
    setBusy("create");
    setError("");
    setNotice("");
    try {
      await apiRequest("/services", {
        method: "POST",
        body: JSON.stringify({
          ...serviceForm,
          price: Number(serviceForm.price),
        }),
      });
      setServiceForm(blankService);
      setNotice("Service added to the catalogue.");
      await loadData();
    } catch (err) {
      setError(err.message);
    } finally {
      setBusy(null);
    }
  }
  async function deleteService(id) {
    if (busy || !window.confirm("Delete this service?")) return;
    setBusy(`delete-${id}`);
    setError("");
    setNotice("");
    try {
      await apiRequest(`/services/${id}`, { method: "DELETE" });
      setNotice("Service deleted.");
      await loadData();
    } catch (err) {
      setError(err.message);
    } finally {
      setBusy(null);
    }
  }
  const filtered = bookings.filter(
    (b) =>
      (filter === "ALL" || b.status === filter) &&
      `${b.id} ${b.userName} ${b.serviceName}`
        .toLowerCase()
        .includes(search.toLowerCase()),
  );
  return (
    <main id="main-content" className="admin-layout">
      <aside className="admin-sidebar">
        <p className="eyebrow">WORKSPACE</p>
        <h2>
          Fixly admin<span>Home services, organised.</span>
        </h2>
        <nav aria-label="Dashboard sections">
          {[
            ["bookings", "calendar", "Bookings"],
            ["services", "tool", "Services"],
            ["customers", "users", "Customers"],
          ].map(([value, icon, label]) => (
            <button
              key={value}
              className={tab === value ? "active" : ""}
              aria-pressed={tab === value}
              onClick={() => setTab(value)}
            >
              <Icon name={icon} size={20} />
              {label}
              <span>→</span>
            </button>
          ))}
        </nav>
        <div className="sidebar-note">
          <Icon name="home" size={28} />
          <strong>A smooth day starts here.</strong>
          <p>Keep bookings moving and homes running happily.</p>
        </div>
      </aside>
      <div className="admin-content">
        <div className="section-heading">
          <div>
            <p className="eyebrow">YOUR DAILY OVERVIEW</p>
            <h1>Dashboard</h1>
            <p>Welcome back. Let’s get things taken care of.</p>
          </div>
          <button
            className="secondary-button"
            onClick={loadData}
            disabled={loading || !!busy}
          >
            {loading ? "Refreshing…" : "Refresh data"}
          </button>
        </div>
        <ErrorNotice message={error} retry={loadData} />
        {notice && (
          <div className="success" role="status">
            {notice}
          </div>
        )}
        <div className="admin-stats">
          {[
            [
              "Total bookings",
              bookings.length,
              "calendar",
              "All service requests",
            ],
            [
              "Awaiting confirmation",
              bookings.filter((b) => b.status === "PENDING").length,
              "clock",
              "Ready for your attention",
            ],
            [
              "Customers",
              users.filter(
                (u) => u.role === "CUSTOMER" || u.role === "ROLE_CUSTOMER",
              ).length,
              "users",
              "Registered customer accounts",
            ],
            [
              "Active services",
              services.length,
              "tool",
              "In your service catalogue",
            ],
          ].map(([label, value, icon, sub]) => (
            <article className="stat-card" key={label}>
              <div>
                <span>{label}</span>
                <Icon name={icon} size={21} />
              </div>
              <strong>{loading ? "—" : value}</strong>
              <small>{sub}</small>
            </article>
          ))}
        </div>
        {loading ? (
          <Loading label="Loading workspace…" />
        ) : (
          <>
            {tab === "bookings" && (
              <section className="admin-panel">
                <div className="panel-heading">
                  <div>
                    <h2>Bookings</h2>
                    <p>Manage each job from request to completion.</p>
                  </div>
                  <span className="count-label">{bookings.length} total</span>
                </div>
                <div className="table-toolbar">
                  <label className="search-field">
                    <Icon name="search" size={18} />
                    <input
                      type="search"
                      aria-label="Search bookings"
                      placeholder="Search customer, service or booking…"
                      value={search}
                      onChange={(e) => setSearch(e.target.value)}
                    />
                  </label>
                  <select
                    aria-label="Filter booking status"
                    value={filter}
                    onChange={(e) => setFilter(e.target.value)}
                  >
                    {[
                      "ALL",
                      "PENDING",
                      "CONFIRMED",
                      "COMPLETED",
                      "CANCELLED",
                    ].map((s) => (
                      <option key={s} value={s}>
                        {s === "ALL" ? "All statuses" : categoryLabel(s)}
                      </option>
                    ))}
                  </select>
                </div>
                <div
                  className="admin-table-wrapper"
                  tabIndex="0"
                  role="region"
                  aria-label="Bookings table"
                >
                  <table className="admin-table">
                    <thead>
                      <tr>
                        <th>Booking / customer</th>
                        <th>Service</th>
                        <th>Scheduled for</th>
                        <th>Status</th>
                        <th>Next action</th>
                      </tr>
                    </thead>
                    <tbody>
                      {filtered.map((b) => (
                        <tr key={b.id}>
                          <td>
                            <strong>{b.userName}</strong>
                            <small>#{b.id}</small>
                          </td>
                          <td>{b.serviceName}</td>
                          <td>
                            {dateLabel(b.bookingDate)}
                            <small>{b.bookingTime?.slice(0, 5)}</small>
                          </td>
                          <td>
                            <Status status={b.status} />
                          </td>
                          <td>
                            {["PENDING", "CONFIRMED"].includes(b.status) ? (
                              <button
                                disabled={!!busy}
                                className={
                                  b.status === "PENDING"
                                    ? "primary-button compact"
                                    : "secondary-button compact"
                                }
                                onClick={() =>
                                  bookingAction(
                                    b.id,
                                    b.status === "PENDING"
                                      ? "confirm"
                                      : "complete",
                                  )
                                }
                              >
                                {busy?.endsWith(`-${b.id}`)
                                  ? "Updating…"
                                  : b.status === "PENDING"
                                    ? "Confirm"
                                    : "Complete"}
                              </button>
                            ) : (
                              <span className="finished-label">
                                {b.status === "COMPLETED"
                                  ? "✓ Finished"
                                  : "Cancelled"}
                              </span>
                            )}
                          </td>
                        </tr>
                      ))}
                    </tbody>
                  </table>
                </div>
                {!filtered.length && (
                  <Empty
                    title="No bookings to show"
                    text="New requests will appear here. Try another filter if you’re looking for a particular booking."
                    action={null}
                  />
                )}
              </section>
            )}
            {tab === "services" && (
              <>
                <section className="admin-panel">
                  <div className="panel-heading">
                    <div>
                      <h2>Add a service</h2>
                      <p>Give customers another way to care for their home.</p>
                    </div>
                  </div>
                  <form className="admin-service-form" onSubmit={createService}>
                    {[
                      ["name", "Service name"],
                      ["price", "Price (£)"],
                      ["description", "Description"],
                    ].map(([name, label]) => (
                      <div className={`field field-${name}`} key={name}>
                        <label htmlFor={`service-${name}`}>{label}</label>
                        {name === "description" ? (
                          <textarea
                            id={`service-${name}`}
                            name={name}
                            rows="3"
                            value={serviceForm[name]}
                            onChange={(e) =>
                              setServiceForm({
                                ...serviceForm,
                                [name]: e.target.value,
                              })
                            }
                            required
                          />
                        ) : (
                          <input
                            id={`service-${name}`}
                            name={name}
                            type={name === "price" ? "number" : "text"}
                            step={name === "price" ? "0.01" : undefined}
                            min={name === "price" ? "0.01" : undefined}
                            value={serviceForm[name]}
                            onChange={(e) =>
                              setServiceForm({
                                ...serviceForm,
                                [name]: e.target.value,
                              })
                            }
                            required
                          />
                        )}
                      </div>
                    ))}
                    <div className="field">
                      <label htmlFor="service-category">Category</label>
                      <select
                        id="service-category"
                        value={serviceForm.category}
                        onChange={(e) =>
                          setServiceForm({
                            ...serviceForm,
                            category: e.target.value,
                          })
                        }
                      >
                        {categories.map((c) => (
                          <option key={c} value={c}>
                            {categoryLabel(c)}
                          </option>
                        ))}
                      </select>
                    </div>
                    <button className="primary-button" disabled={!!busy}>
                      {busy === "create" ? "Adding service…" : "Add service"}
                      <Icon name="arrow" size={18} />
                    </button>
                  </form>
                </section>
                <section className="admin-panel">
                  <div className="panel-heading">
                    <h2>Service catalogue</h2>
                    <span className="count-label">
                      {services.length} services
                    </span>
                  </div>
                  {services.length ? (
                    <div
                      className="admin-table-wrapper"
                      tabIndex="0"
                      role="region"
                      aria-label="Service catalogue"
                    >
                      <table className="admin-table">
                        <thead>
                          <tr>
                            <th>Service</th>
                            <th>Category</th>
                            <th>Price</th>
                            <th>Action</th>
                          </tr>
                        </thead>
                        <tbody>
                          {services.map((s) => (
                            <tr key={s.id}>
                              <td>
                                <strong>{s.name}</strong>
                                <small className="service-description">
                                  {s.description}
                                </small>
                              </td>
                              <td>{categoryLabel(s.category)}</td>
                              <td>{money(s.price)}</td>
                              <td>
                                <button
                                  className="danger-button compact"
                                  disabled={!!busy}
                                  onClick={() => deleteService(s.id)}
                                >
                                  {busy === `delete-${s.id}`
                                    ? "Deleting…"
                                    : "Delete"}
                                  <span className="sr-only"> {s.name}</span>
                                </button>
                              </td>
                            </tr>
                          ))}
                        </tbody>
                      </table>
                    </div>
                  ) : (
                    <Empty
                      title="Your catalogue is ready for its first service"
                      text="Use the form above to add a service."
                      action={null}
                    />
                  )}
                </section>
              </>
            )}
            {tab === "customers" && (
              <section className="admin-panel">
                <div className="panel-heading">
                  <div>
                    <h2>Customers & accounts</h2>
                    <p>All registered accounts and their roles.</p>
                  </div>
                  <span className="count-label">{users.length} accounts</span>
                </div>
                <div
                  className="admin-table-wrapper"
                  tabIndex="0"
                  role="region"
                  aria-label="Customer accounts"
                >
                  <table className="admin-table">
                    <thead>
                      <tr>
                        <th>Name</th>
                        <th>Email</th>
                        <th>Phone</th>
                        <th>Role</th>
                      </tr>
                    </thead>
                    <tbody>
                      {users.map((u) => (
                        <tr key={u.id}>
                          <td>
                            <strong>{u.name}</strong>
                            <small>#{u.id}</small>
                          </td>
                          <td>{u.email}</td>
                          <td>{u.phone || "—"}</td>
                          <td>
                            <span className="category">
                              {categoryLabel(u.role)}
                            </span>
                          </td>
                        </tr>
                      ))}
                    </tbody>
                  </table>
                </div>
                {!users.length && (
                  <Empty
                    title="No accounts yet"
                    text="Registered accounts will appear here."
                    action={null}
                  />
                )}
              </section>
            )}
          </>
        )}
      </div>
    </main>
  );
}
