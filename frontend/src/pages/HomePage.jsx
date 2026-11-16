import { useState, useEffect } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { propertyService } from '../services/propertyService';
import PropertyCard from '../components/PropertyCard';

export default function HomePage() {
  const navigate = useNavigate();
  const [featuredProperties, setFeaturedProperties] = useState([]);
  const [cityInput, setCityInput] = useState('');
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    const fetchFeatured = async () => {
      try {
        const propData = await propertyService.getProperties({ size: 6, status: 'AVAILABLE' });
        setFeaturedProperties(propData.content || []);
      } catch (err) {
        // Fallback gracefully
      } finally {
        setLoading(false);
      }
    };

    fetchFeatured();
  }, []);

  const handleHeroSearch = (e) => {
    e.preventDefault();
    if (cityInput.trim()) {
      navigate(`/properties?city=${encodeURIComponent(cityInput.trim())}`);
    } else {
      navigate('/properties');
    }
  };

  return (
    <main className="container home-container">
      {/* Hero Section */}
      <section className="hero">
        <div className="hero-content">
          <span className="badge hero-badge">Verified Rental Marketplace</span>
          <h1 className="hero-title">
            Discover Homes with <span className="accent-text">Total Clarity</span>
          </h1>
          <p className="hero-subtitle">
            Transparent rental pricing, verified BHK configurations, separate electricity and water charges, and precise nearby distances to hospitals, colleges, schools, and metro stations.
          </p>

          <form onSubmit={handleHeroSearch} className="hero-search-box">
            <div className="search-field">
              <span className="search-icon">📍</span>
              <input
                type="text"
                placeholder="Enter city (e.g. Bengaluru, Mumbai, Pune, Delhi)..."
                value={cityInput}
                onChange={(e) => setCityInput(e.target.value)}
                className="hero-input"
              />
            </div>
            <button type="submit" className="primary-button hero-btn">
              Search Rentals
            </button>
          </form>

          {/* Quick Filters */}
          <div className="quick-tags">
            <span className="quick-tag-label">Popular BHKs:</span>
            <Link to="/properties?bhkType=1%20BHK" className="quick-chip">1 BHK</Link>
            <Link to="/properties?bhkType=2%20BHK" className="quick-chip">2 BHK</Link>
            <Link to="/properties?bhkType=3%20BHK" className="quick-chip">3 BHK</Link>
            <Link to="/properties?propertyType=GATED_COMMUNITY" className="quick-chip">🛡️ Gated Community</Link>
            <Link to="/properties?waterAvailability=true" className="quick-chip">💧 24/7 Water</Link>
          </div>
        </div>
      </section>

      {/* Featured Properties Grid */}
      <section className="featured-section">
        <div className="section-header">
          <div>
            <h2>Featured Rental Listings</h2>
            <p className="section-subtext">Verified properties with full price breakdown and facility distances</p>
          </div>
          <Link to="/properties" className="secondary-button view-all-btn">
            View All Rentals →
          </Link>
        </div>

        {loading ? (
          <div className="loading-state">
            <div className="spinner"></div>
            <p>Loading featured properties…</p>
          </div>
        ) : featuredProperties.length > 0 ? (
          <div className="properties-grid home-properties-grid">
            {featuredProperties.map((prop) => (
              <PropertyCard key={prop.id} property={prop} />
            ))}
          </div>
        ) : (
          <div className="empty-state-card">
            <div className="empty-icon">🏡</div>
            <h3>No Active Listings Available Right Now</h3>
            <p>Check back shortly or browse our entire catalog.</p>
            <Link to="/properties" className="primary-button">
              Explore Property Catalog
            </Link>
          </div>
        )}
      </section>
    </main>
  );
}
