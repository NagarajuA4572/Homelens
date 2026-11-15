import { Link } from 'react-router-dom';

export default function PropertyCard({ property }) {
  const apiUrl = import.meta.env.VITE_API_URL || 'http://localhost:8080';

  // Get primary image or first available image
  let imageUrl = null;
  if (property.images && property.images.length > 0) {
    const primary = property.images.find((img) => img.isPrimary) || property.images[0];
    imageUrl = primary.imageUrl;
    if (imageUrl && imageUrl.startsWith('/uploads/')) {
      imageUrl = `${apiUrl}${imageUrl}`;
    }
  }

  // Format currency in Indian numbering (INR ₹)
  const formatINR = (val) => {
    if (!val && val !== 0) return '₹0';
    return new Intl.NumberFormat('en-IN', {
      style: 'currency',
      currency: 'INR',
      maximumFractionDigits: 0,
    }).format(val);
  };

  const isAvailable = property.status === 'AVAILABLE';

  return (
    <article className="property-card">
      <div className="property-card-media">
        {imageUrl ? (
          <img
            src={imageUrl}
            alt={property.title}
            className="property-card-img"
            loading="lazy"
            onError={(e) => {
              e.target.style.display = 'none';
              e.target.nextSibling.style.display = 'flex';
            }}
          />
        ) : null}
        <div
          className="property-placeholder-img"
          style={{ display: imageUrl ? 'none' : 'flex' }}
        >
          <span>🏠 HomeLens</span>
        </div>

        <div className="card-top-badges">
          <span className={`status-pill ${isAvailable ? 'available' : 'rented'}`}>
            {isAvailable ? '● Available' : '● Rented'}
          </span>
          {property.bhkType && (
            <span className="bhk-pill">{property.bhkType}</span>
          )}
        </div>

        {property.propertyType && (
          <div className="card-bottom-badge">
            <span className="type-pill">
              {property.propertyType === 'GATED_COMMUNITY'
                ? '🛡️ Gated Community'
                : property.propertyType === 'INDIVIDUAL_HOUSE'
                ? '🏡 Individual House'
                : property.propertyType}
            </span>
          </div>
        )}
      </div>

      <div className="property-card-body">
        <div className="card-pricing">
          <div className="rent-amount">
            {formatINR(property.monthlyRent || property.price)}
            <span className="per-month">/ month</span>
          </div>
          {property.totalMonthlyCharges && property.totalMonthlyCharges > property.monthlyRent && (
            <div className="total-charges-hint" title="Includes base rent, electricity & water charges">
              Total: {formatINR(property.totalMonthlyCharges)}/mo
            </div>
          )}
        </div>

        <h3 className="property-title">
          <Link to={`/properties/${property.id}`}>{property.title}</Link>
        </h3>

        <p className="property-location">
          📍 {property.address}, {property.city}
        </p>

        <div className="property-specs-grid">
          {property.bedrooms > 0 && <span>🛏️ {property.bedrooms} Beds</span>}
          {property.bathrooms > 0 && <span>🚿 {property.bathrooms} Baths</span>}
          {property.areaSqft > 0 && <span>📐 {property.areaSqft} sqft</span>}
        </div>

        <div className="occupancy-amenity-tags">
          {property.waterAvailability && (
            <span className="tag water-tag" title="Water supply guaranteed">
              💧 Water Supply: Yes
            </span>
          )}
          {property.familyAllowed && (
            <span className="tag family-tag" title="Family occupancy allowed">
              👨‍👩‍👧 Family
            </span>
          )}
          {property.singleAllowed && (
            <span className="tag single-tag" title="Single / Bachelor occupancy allowed">
              👤 Single
            </span>
          )}
        </div>

        {/* Nearby key distance highlights if present */}
        {(property.distanceMetroKm || property.distanceHospitalKm || property.distanceBusStopKm) && (
          <div className="card-distances">
            {property.distanceMetroKm && (
              <span className="dist-item">🚇 {property.distanceMetroKm} km Metro</span>
            )}
            {property.distanceHospitalKm && (
              <span className="dist-item">🏥 {property.distanceHospitalKm} km Hospital</span>
            )}
            {property.distanceBusStopKm && (
              <span className="dist-item">🚌 {property.distanceBusStopKm} km Bus Stop</span>
            )}
          </div>
        )}

        <div className="card-footer">
          <Link to={`/properties/${property.id}`} className="view-details-btn">
            View Details →
          </Link>
        </div>
      </div>
    </article>
  );
}
