import { Link } from 'react-router-dom';

export default function AboutPage() {
  return (
    <main className="container about-container">
      <section className="about-hero">
        <p className="eyebrow">About HomeLens</p>
        <h1>A Transparent Rental Experience</h1>
        <p className="subtitle">
          HomeLens eliminates hidden costs and ambiguous rental listings by enforcing clear monthly rental breakdowns, verified water availability, and accurate facility distances.
        </p>
      </section>

      <div className="about-pillars-grid">
        <div className="pillar-card">
          <div className="pillar-icon">💰</div>
          <h3>Transparent Monthly Costs</h3>
          <p>
            No hidden maintenance surprises. Base monthly rent is clearly segregated from electricity and water charges, calculating your exact estimated total upfront.
          </p>
        </div>

        <div className="pillar-card">
          <div className="pillar-icon">💧</div>
          <h3>Verified Water Availability</h3>
          <p>
            Water supply is a critical rental deciding factor. Every HomeLens listing explicitly declares guaranteed continuous water availability vs tanker/borewell dependencies.
          </p>
        </div>

        <div className="pillar-card">
          <div className="pillar-icon">🚇</div>
          <h3>Proximity to Essentials</h3>
          <p>
            View verified driving and walking distances to nearby Hospitals, Colleges, Schools, Metro Stations, and Bus Stops in kilometres.
          </p>
        </div>

        <div className="pillar-card">
          <div className="pillar-icon">🛡️</div>
          <h3>Community & Occupancy Choice</h3>
          <p>
            Filter confidently between secure Gated Communities and private Individual Houses, with clear policies for Family and Single/Bachelor occupancy.
          </p>
        </div>
      </div>

      <section className="about-cta-section">
        <h2>Ready to explore or list your property?</h2>
        <div className="cta-actions">
          <Link to="/properties" className="primary-button">
            Browse Rentals Now
          </Link>
          <Link to="/owner/properties" className="secondary-button">
            List Your Property
          </Link>
        </div>
      </section>
    </main>
  );
}
