import { useState, useEffect } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { propertyService } from '../services/propertyService';
import { apiClient } from '../services/api';
import PropertyCard from '../components/PropertyCard';

export default function HomePage() {
  const navigate = useNavigate();
  const [featuredProperties, setFeaturedProperties] = useState([]);
  const [cityInput, setCityInput] = useState('');
  const [health, setHealth] = useState(null);
  const [healthError, setHealthError] = useState('');
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    const fetchData = async () => {
      // 1. Fetch featured properties
      try {
        const propData = await propertyService.getProperties({ size: 6, status: 'AVAILABLE' });
        setFeaturedProperties(propData.content || []);
      } catch (err) {
        // Fallback gracefully
      }

      // 2. Fetch backend health
      try {
        const healthRes = await apiClient.get('/api/health');
        setHealth(healthRes.data);
      } catch (err) {
        setHealthError('Backend unavailable. Verify that Spring Boot is running on port 8080.');
      } finally {
        setLoading(false);
      }
    };

    fetchData();
  }, []);

  const handleCitySearch = (e) => {
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
        <p className="eyebrow">HomeLens • Transparent Rental Marketplace</p>
        <h1>Find verified rental homes across India.</h1>
        <p className="subtitle">
          Search 1, 2, 3+ BHK homes in Gated Communities and Individual Houses with transparent electricity and water charges, verified water availability, and nearby facility distances.
        </p>

        {/* Hero Search Bar */}
        <form onSubmit={handleCitySearch} className="hero-search-box">
          <input
            type="text"
            placeholder="Enter city (e.g. Bengaluru, Hyderabad, Mumbai, Pune, Chennai)..."
            value={cityInput}
            onChange={(e) => setCityInput(e.target.value)}
            className="hero-search-input"
          />
          <button type="submit" className="primary-button hero-search-btn">
            Explore Rentals →
          </button>
        </form>

        {/* Quick Filter Badges */}
        <div className="quick-tags-row">
          <span className="quick-tag-label">Popular Searches:</span>
          <Link to="/properties?bhkType=2+BHK" className="quick-tag">2 BHK</Link>
          <Link to="/properties?bhkType=3+BHK" className="quick-tag">3 BHK</Link>
          <Link to="/properties?propertyType=GATED_COMMUNITY" className="quick-tag">🛡️ Gated Community</Link>
          <Link to="/properties?propertyType=INDIVIDUAL_HOUSE" className="quick-tag">🏡 Individual House</Link>
          <Link to="/properties?waterAvailability=true" className="quick-tag">💧 Water Supply: Yes</Link>
          <Link to="/properties?familyAllowed=true" className="quick-tag">👨‍👩‍👧 Family Allowed</Link>
        </div>
      </section>

      {/* Featured Properties */}
      <section className="featured-section">
        <div className="section-head">
          <div>
            <h2>Featured Rental Listings</h2>
            <p className="section-subtitle">Browse recently listed homes with complete breakdown of monthly charges.</p>
          </div>
          <Link to="/properties" className="secondary-button view-all-link">
            View All ({featuredProperties.length > 0 ? `${featuredProperties.length}+` : 'All'}) →
          </Link>
        </div>

        {loading ? (
          <div className="loading-state">
            <div className="spinner"></div>
            <p>Loading featured properties…</p>
          </div>
        ) : featuredProperties.length > 0 ? (
          <div className="properties-grid">
            {featuredProperties.map((prop) => (
              <PropertyCard key={prop.id} property={prop} />
            ))}
          </div>
        ) : (
          <div className="empty-state-card">
            <p>No listings currently available in database.</p>
            <Link to="/properties" className="primary-button">
              Explore Property Catalog
            </Link>
          </div>
        )}
      </section>

      {/* Backend API Health Status Indicator */}
      <section className="status-card">
        <div className="status-header">
          <h3>⚡ System Status & API Connectivity</h3>
          <span className={`status-pill ${health ? 'available' : 'rented'}`}>
            {health ? '● Operational' : '● Offline'}
          </span>
        </div>
        {healthError ? (
          <p className="error">{healthError}</p>
        ) : health ? (
          <div className="health-details-grid">
            <div><strong>Status:</strong> {health.status}</div>
            <div><strong>Backend Service:</strong> {health.service}</div>
            <div><strong>Timestamp:</strong> {new Date(health.timestamp).toLocaleString()}</div>
          </div>
        ) : (
          <p>Checking backend health…</p>
        )}
      </section>
    </main>
  );
}
