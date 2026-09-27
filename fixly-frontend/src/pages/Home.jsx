import { Link } from "react-router-dom";
import { Icon, ServiceArt } from "../components/UI";
import { categories, categoryLabel } from "../utils/display";
export default function Home() {
  return (
    <main id="main-content">
      <section className="hero section">
        <div className="hero-copy">
          <p className="eyebrow">
            <span className="live-dot" /> YOUR HOME. IN GOOD HANDS.
          </p>
          <h1>
            Less to do.
            <br />
            More{" "}
            <span>
              to come
              <br className="desktop-break" /> home to.
            </span>
          </h1>
          <p className="hero-text">
            From a leaking tap to a garden that needs a little love. Find and
            book the help your home needs, all in one place.
          </p>
          <div className="hero-actions">
            <Link className="primary-button" to="/services">
              Find a service
              <Icon name="arrow" size={19} />
            </Link>
            <a className="text-button" href="#how-it-works">
              How it works <span>↗</span>
            </a>
          </div>
          <div className="hero-assurances">
            <span>
              <Icon name="check" size={17} />
              Clear service prices
            </span>
            <span>
              <Icon name="check" size={17} />
              Easy online booking
            </span>
          </div>
        </div>
        <div className="hero-visual">
          <img
            src="/art/home.svg"
            alt="Illustration of a welcoming brick home with a teal front door, leafy garden and warm windows"
            fetchPriority="high"
          />
          <div className="floating-note">
            <span className="note-icon">
              <Icon name="home" />
            </span>
            <div>
              <strong>A happier home starts here</strong>
              <small>The everyday jobs. Taken care of.</small>
            </div>
          </div>
          <span className="visual-caption">A LITTLE HELP GOES A LONG WAY</span>
        </div>
      </section>
      <div className="benefit-strip">
        <span>
          <Icon name="tool" />
          Six ways to care for your home
        </span>
        <span>
          <Icon name="calendar" />
          Choose your date & time
        </span>
        <span>
          <Icon name="shield" />
          Track every booking
        </span>
      </div>
      <section className="section">
        <div className="section-heading">
          <div>
            <p className="eyebrow">SMALL FIXES. BIG DIFFERENCE.</p>
            <h2>What can we help with?</h2>
          </div>
          <Link className="text-button" to="/services">
            Explore all services <Icon name="arrow" size={18} />
          </Link>
        </div>
        <div className="category-grid">
          {categories.map((c) => (
            <Link
              className="category-tile"
              to={`/services?category=${c}`}
              key={c}
            >
              <ServiceArt category={c} />
              <span>
                {categoryLabel(c)}
                <Icon name="arrow" size={18} />
              </span>
            </Link>
          ))}
        </div>
      </section>
      <section className="how-section section" id="how-it-works">
        <div className="section-heading">
          <div>
            <p className="eyebrow">NO COMPLICATED TO-DO LISTS</p>
            <h2>A simpler way to get it sorted.</h2>
          </div>
          <p>
            From finding help to the final fix.
            <br />
            You’re in the loop at every step.
          </p>
        </div>
        <div className="steps">
          {[
            [
              "01",
              "Find your service",
              "Browse home services and see the price before you book.",
              "search",
            ],
            [
              "02",
              "Make it fit your day",
              "Choose your preferred date, time and service address.",
              "calendar",
            ],
            [
              "03",
              "We’ll take it from here",
              "Follow your booking from pending to confirmed to completed.",
              "check",
            ],
          ].map(([n, title, text, icon]) => (
            <article key={n}>
              <div className="step-top">
                <Icon name={icon} size={26} />
                <span>{n}</span>
              </div>
              <h3>{title}</h3>
              <p>{text}</p>
            </article>
          ))}
        </div>
      </section>
      <section className="home-cta section">
        <div>
          <p className="eyebrow">LET’S MAKE HOME FEEL LIKE HOME</p>
          <h2>One less thing on your list.</h2>
          <p>Start with the job you’ve been meaning to get around to.</p>
        </div>
        <Link className="primary-button light-button" to="/services">
          Explore services
          <Icon name="arrow" size={18} />
        </Link>
      </section>
    </main>
  );
}
