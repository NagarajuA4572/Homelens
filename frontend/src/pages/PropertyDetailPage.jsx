import { useState, useEffect } from 'react';
import { useParams, Link } from 'react-router-dom';
import { propertyService } from '../services/propertyService';
import { inquiryService } from '../services/inquiryService';
import { useAuth } from '../context/AuthContext';
import { resolveImageUrl, FALLBACK_PROPERTY_IMAGE } from '../services/api';

export default function PropertyDetailPage() {
  const { id } = useParams();
  const { user } = useAuth();
  const apiUrl = import.meta.env.VITE_API_URL || 'http://localhost:8080';

  const [property, setProperty] = useState(null);
  const [activeImageIndex, setActiveImageIndex] = useState(0);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');
  const [userInquiry, setUserInquiry] = useState(null);

  // Inquiry form state
  const [inquiryName, setInquiryName] = useState(user?.fullName || '');
  const [inquiryEmail, setInquiryEmail] = useState(user?.email || '');
  const [inquiryPhone, setInquiryPhone] = useState(user?.phone || '');
  const [inquiryMessage, setInquiryMessage] = useState(
    'Hi, I am interested in this rental property. Could we schedule a showing and share contact details?'
  );
  const [inquirySubmitting, setInquirySubmitting] = useState(false);
  const [inquirySuccess, setInquirySuccess] = useState(false);
  const [inquiryError, setInquiryError] = useState('');

  useEffect(() => {
    const fetchProperty = async () => {
      setLoading(true);
      setError('');
      try {
        const data = await propertyService.getPropertyById(id);
        setProperty(data);
      } catch (err) {
        setError('Failed to load property details. The property may have been removed.');
      } finally {
        setLoading(false);
      }
    };

    fetchProperty();
  }, [id]);

  useEffect(() => {
    const fetchUserInquiry = async () => {
      if (user) {
        try {
          const sent = await inquiryService.getMySentInquiries(0, 50);
          const found = (sent.content || []).find((inq) => String(inq.propertyId) === String(id));
          if (found) {
            setUserInquiry(found);
          }
        } catch (e) {
          // Ignore non-critical inquiry check
        }
      }
    };

    fetchUserInquiry();
  }, [id, user]);

  useEffect(() => {
    if (user) {
      if (!inquiryName) setInquiryName(user.fullName || '');
      if (!inquiryEmail) setInquiryEmail(user.email || '');
      if (!inquiryPhone) setInquiryPhone(user.phone || '');
    }
  }, [user]);

  const handleInquirySubmit = async (e) => {
    e.preventDefault();
    setInquirySubmitting(true);
    setInquiryError('');
    setInquirySuccess(false);

    try {
      const created = await inquiryService.submitInquiry(id, {
        name: inquiryName,
        email: inquiryEmail,
        phone: inquiryPhone,
        message: inquiryMessage,
      });
      setUserInquiry(created);
      setInquirySuccess(true);
      setInquiryMessage('');
    } catch (err) {
      setInquiryError(
        err.response?.data?.message || 'Could not send inquiry. Please try again.'
      );
    } finally {
      setInquirySubmitting(false);
    }
  };

  const formatINR = (val) => {
    if (!val && val !== 0) return '₹0';
    return new Intl.NumberFormat('en-IN', {
      style: 'currency',
      currency: 'INR',
      maximumFractionDigits: 0,
    }).format(val);
  };

  const formatKm = (val) => {
    if (val === null || val === undefined || val === '') return 'Not specified';
    const num = parseFloat(val);
    return isNaN(num) ? `${val} km` : `${num.toFixed(1)} km`;
  };

  if (loading) {
    return (
      <main className="container detail-container">
        <div className="loading-state">
          <div className="spinner"></div>
          <p>Loading property details…</p>
        </div>
      </main>
    );
  }

  if (error || !property) {
    return (
      <main className="container detail-container">
        <div className="error-card">
          <h2>Property Not Found</h2>
          <p>{error || 'This listing does not exist.'}</p>
          <Link to="/properties" className="primary-button">
            ← Back to All Rentals
          </Link>
        </div>
      </main>
    );
  }

  const images = property.images && property.images.length > 0 ? property.images : [];
  const currentImage = images[activeImageIndex];
  const mainImageUrl = resolveImageUrl(currentImage?.imageUrl) || FALLBACK_PROPERTY_IMAGE;

  const isAvailable = property.status === 'AVAILABLE';

  return (
    <main className="container detail-container">
      {/* Breadcrumb Navigation */}
      <nav className="breadcrumb">
        <Link to="/properties">← Back to Listings</Link>
        <span>/</span>
        <span>{property.city}</span>
        <span>/</span>
        <span className="current">{property.title}</span>
      </nav>

      {/* Title & Status Header */}
      <section className="detail-header">
        <div className="detail-header-left">
          <div className="detail-badges">
            <span className={`status-pill large ${isAvailable ? 'available' : 'rented'}`}>
              {isAvailable ? '● Available for Rent' : '● Currently Rented'}
            </span>
            <span className="bhk-pill large">{property.bhkType}</span>
            <span className="type-pill large">
              {property.propertyType === 'GATED_COMMUNITY'
                ? '🛡️ Gated Community'
                : property.propertyType === 'INDIVIDUAL_HOUSE'
                ? '🏡 Individual House'
                : property.propertyType}
            </span>
          </div>
          <h1>{property.title}</h1>
          <p className="detail-address">📍 {property.address}, {property.city}, {property.state} {property.postalCode}</p>
        </div>

        <div className="detail-header-right">
          <div className="detail-price-box">
            <span className="price-label">Base Monthly Rent</span>
            <span className="price-value">{formatINR(property.monthlyRent || property.price)}</span>
            <span className="price-sub">/ month</span>
          </div>
        </div>
      </section>

      {/* Photo Gallery */}
      <section className="gallery-section">
        <div className="gallery-main">
          <img
            src={mainImageUrl}
            alt={property.title}
            className="gallery-main-img"
            onError={(e) => {
              e.target.onerror = null;
              e.target.src = FALLBACK_PROPERTY_IMAGE;
            }}
          />
        </div>

        {images.length > 1 && (
          <div className="gallery-thumbnails">
            {images.map((img, idx) => {
              const thumbUrl = resolveImageUrl(img.imageUrl) || FALLBACK_PROPERTY_IMAGE;
              return (
                <button
                  key={img.id || idx}
                  type="button"
                  className={`gallery-thumb-btn ${idx === activeImageIndex ? 'active' : ''}`}
                  onClick={() => setActiveImageIndex(idx)}
                >
                  <img
                    src={thumbUrl}
                    alt={`Thumbnail ${idx + 1}`}
                    onError={(e) => {
                      e.target.onerror = null;
                      e.target.src = FALLBACK_PROPERTY_IMAGE;
                    }}
                  />
                </button>
              );
            })}
          </div>
        )}
      </section>

      {/* Highlighted Proximity & Distance Bar (in Kilometres) */}
      <section className="detail-distances-bar">
        <div className="distances-bar-heading">
          <span className="bar-icon">📍</span>
          <strong>Transit & Nearby Facility Distances (in km):</strong>
        </div>
        <div className="distances-bar-grid">
          <div className="dist-chip-item">
            <span className="dist-chip-icon">🏥</span>
            <div className="dist-chip-text">
              <span className="dist-chip-label">Hospital</span>
              <strong className="dist-chip-val">{formatKm(property.distanceHospitalKm)}</strong>
            </div>
          </div>
          <div className="dist-chip-item">
            <span className="dist-chip-icon">🚇</span>
            <div className="dist-chip-text">
              <span className="dist-chip-label">Metro Station</span>
              <strong className="dist-chip-val">{formatKm(property.distanceMetroKm)}</strong>
            </div>
          </div>
          <div className="dist-chip-item">
            <span className="dist-chip-icon">🚌</span>
            <div className="dist-chip-text">
              <span className="dist-chip-label">Bus Stop</span>
              <strong className="dist-chip-val">{formatKm(property.distanceBusStopKm)}</strong>
            </div>
          </div>
          <div className="dist-chip-item">
            <span className="dist-chip-icon">🏫</span>
            <div className="dist-chip-text">
              <span className="dist-chip-label">School</span>
              <strong className="dist-chip-val">{formatKm(property.distanceSchoolKm)}</strong>
            </div>
          </div>
          <div className="dist-chip-item">
            <span className="dist-chip-icon">🎓</span>
            <div className="dist-chip-text">
              <span className="dist-chip-label">College / Univ</span>
              <strong className="dist-chip-val">{formatKm(property.distanceCollegeKm)}</strong>
            </div>
          </div>
        </div>
      </section>

      {/* Main Grid: Details Left, Cards Right */}
      <div className="detail-grid-layout">
        <div className="detail-main-content">
          {/* Quick Specifications Strip */}
          <div className="specs-strip">
            <div className="spec-item">
              <span className="spec-label">BHK</span>
              <span className="spec-val">{property.bhkType}</span>
            </div>
            <div className="spec-item">
              <span className="spec-label">Bedrooms</span>
              <span className="spec-val">{property.bedrooms} Beds</span>
            </div>
            <div className="spec-item">
              <span className="spec-label">Bathrooms</span>
              <span className="spec-val">{property.bathrooms} Baths</span>
            </div>
            <div className="spec-item">
              <span className="spec-label">Built-up Area</span>
              <span className="spec-val">{property.areaSqft || '—'} sqft</span>
            </div>
            <div className="spec-item">
              <span className="spec-label">Property Type</span>
              <span className="spec-val">
                {property.propertyType === 'GATED_COMMUNITY'
                  ? 'Gated Community'
                  : property.propertyType === 'INDIVIDUAL_HOUSE'
                  ? 'Individual House'
                  : property.propertyType}
              </span>
            </div>
          </div>

          {/* Occupancy & Water Policies */}
          <div className="policy-cards-grid">
            <div className="policy-card">
              <h4>💧 Water Availability</h4>
              <p className={property.waterAvailability ? 'text-success' : 'text-danger'}>
                {property.waterAvailability
                  ? '✅ Yes — Continuous/Guaranteed Water Supply Available'
                  : '⚠️ Borewell/Intermittent Supply'}
              </p>
            </div>

            <div className="policy-card">
              <h4>👥 Occupancy Rules</h4>
              <div className="occupancy-rules-tags">
                <span className={`rule-pill ${property.familyAllowed ? 'allowed' : 'restricted'}`}>
                  {property.familyAllowed ? '✓ Family Allowed' : '✗ No Families'}
                </span>
                <span className={`rule-pill ${property.singleAllowed ? 'allowed' : 'restricted'}`}>
                  {property.singleAllowed ? '✓ Single / Bachelors Allowed' : '✗ No Singles'}
                </span>
              </div>
            </div>
          </div>

          {/* Description */}
          <section className="detail-section">
            <h2>About this Property</h2>
            <p className="detail-description">
              {property.description || 'No detailed description provided by the owner.'}
            </p>
          </section>

          {/* Nearby Facilities & Distances (in km) */}
          <section className="detail-section">
            <h2>Nearby Facilities & Distances</h2>
            <p className="section-hint">Exact driving / walking distances in kilometres provided by property owner:</p>

            <div className="distances-table-grid">
              <div className="distance-box">
                <span className="dist-icon">🏥</span>
                <div className="dist-details">
                  <span className="dist-name">Hospital / Healthcare</span>
                  <span className="dist-value highlight-km">{formatKm(property.distanceHospitalKm)}</span>
                </div>
              </div>

              <div className="distance-box">
                <span className="dist-icon">🚇</span>
                <div className="dist-details">
                  <span className="dist-name">Metro Station</span>
                  <span className="dist-value highlight-km">{formatKm(property.distanceMetroKm)}</span>
                </div>
              </div>

              <div className="distance-box">
                <span className="dist-icon">🚌</span>
                <div className="dist-details">
                  <span className="dist-name">Bus Stop</span>
                  <span className="dist-value highlight-km">{formatKm(property.distanceBusStopKm)}</span>
                </div>
              </div>

              <div className="distance-box">
                <span className="dist-icon">🏫</span>
                <div className="dist-details">
                  <span className="dist-name">School</span>
                  <span className="dist-value highlight-km">{formatKm(property.distanceSchoolKm)}</span>
                </div>
              </div>

              <div className="distance-box">
                <span className="dist-icon">🎓</span>
                <div className="dist-details">
                  <span className="dist-name">College / University</span>
                  <span className="dist-value highlight-km">{formatKm(property.distanceCollegeKm)}</span>
                </div>
              </div>
            </div>
          </section>
        </div>

        {/* Sidebar Cards: Financial Breakdown + Contact Form */}
        <aside className="detail-sidebar">
          {/* Monthly Charges Breakdown Card */}
          <div className="charges-card">
            <h3>💰 Monthly Rent & Charges</h3>
            <div className="charge-row">
              <span>Base Monthly Rent:</span>
              <strong className="rent-highlight">{formatINR(property.monthlyRent || property.price)}</strong>
            </div>
            <div className="charge-row">
              <span>Monthly Electricity Charges:</span>
              <span>{property.electricityCharges > 0 ? formatINR(property.electricityCharges) : 'As per meter'}</span>
            </div>
            <div className="charge-row">
              <span>Monthly Water Charges:</span>
              <span>{property.waterCharges > 0 ? formatINR(property.waterCharges) : 'Included / ₹0'}</span>
            </div>
            <hr className="charge-divider" />
            <div className="charge-row total-row">
              <span>Total Estimated Monthly:</span>
              <span className="total-amount">
                {formatINR(property.totalMonthlyCharges || property.monthlyRent || property.price)}
              </span>
            </div>
          </div>

          {/* Property Owner Card with Privacy Protection */}
          {property.owner && (
            <div className="owner-card">
              <div className="owner-card-header">
                <h4>Listed by Property Owner</h4>
                <span className="verified-badge">✓ Verified Owner</span>
              </div>
              <div className="owner-info">
                <span className="owner-avatar">{property.owner.fullName?.charAt(0) || 'O'}</span>
                <div className="owner-meta">
                  <div className="owner-name">{property.owner.fullName}</div>
                  <div className="owner-role-title">HomeLens Verified Host</div>
                </div>
              </div>

              {/* Security Privacy Gate: Personal email & phone revealed ONLY after owner accepts request */}
              {user && property.owner && String(user.id) === String(property.owner.id) ? (
                <div className="owner-contact-box owner-self-box">
                  <div className="contact-box-title">🏢 You Own This Listing</div>
                  <div className="contact-line">✉️ {user.email}</div>
                  {user.phone && <div className="contact-line">📞 {user.phone}</div>}
                </div>
              ) : userInquiry && (userInquiry.status === 'ACCEPTED' || userInquiry.status === 'RESOLVED') ? (
                <div className="owner-contact-box owner-unlocked-box">
                  <div className="contact-box-badge">✅ Contact Details Unlocked</div>
                  <p className="contact-unlocked-hint">The owner has accepted your request! Connect directly:</p>
                  <div className="unlocked-contact-info">
                    {userInquiry.ownerEmail && (
                      <div className="contact-line">
                        <span>✉️ Email:</span>
                        <a href={`mailto:${userInquiry.ownerEmail}`}>{userInquiry.ownerEmail}</a>
                      </div>
                    )}
                    {userInquiry.ownerPhone && (
                      <div className="contact-line">
                        <span>📞 Phone:</span>
                        <a href={`tel:${userInquiry.ownerPhone}`}>{userInquiry.ownerPhone}</a>
                      </div>
                    )}
                  </div>
                </div>
              ) : userInquiry && (userInquiry.status === 'NEW' || userInquiry.status === 'PENDING') ? (
                <div className="owner-contact-box owner-pending-box">
                  <div className="contact-box-badge">⏳ Request Sent — Awaiting Owner Approval</div>
                  <p className="contact-privacy-note">
                    Your request was sent to {property.owner.fullName}. Once accepted, their verified contact details will be unlocked right here.
                  </p>
                </div>
              ) : userInquiry && userInquiry.status === 'DENIED' ? (
                <div className="owner-contact-box owner-denied-box">
                  <div className="contact-box-badge">❌ Request Declined</div>
                  <p className="contact-privacy-note">
                    The owner declined this request. Contact details remain private.
                  </p>
                </div>
              ) : (
                <div className="owner-contact-box owner-locked-box">
                  <div className="contact-box-badge">🔒 Contact Details Protected</div>
                  <p className="contact-privacy-note">
                    For privacy and safety, the owner's phone number and email are confidential until you submit a request and the owner accepts.
                  </p>
                  <a href="#inquiry-section" className="request-access-cta-btn">
                    📩 Request Contact & Tour
                  </a>
                </div>
              )}
            </div>
          )}

          {/* Lead / Inquiry Submission Form */}
          <div className="inquiry-card" id="inquiry-section">
            <h3>Request Owner Contact Details & Tour</h3>
            <p className="inquiry-subtitle">
              Submit your request to connect with the owner. Once accepted, their direct email and phone number will be unlocked.
            </p>

            {inquirySuccess ? (
              <div className="inquiry-success-box">
                <span className="success-icon">✓</span>
                <h4>Request Submitted Successfully!</h4>
                <p>The owner has received your request. Once they accept, their contact details will be unlocked here and on your "My Requests" page.</p>
                <button
                  type="button"
                  onClick={() => setInquirySuccess(false)}
                  className="secondary-button full-width"
                >
                  Send another message
                </button>
              </div>
            ) : (
              <form onSubmit={handleInquirySubmit} className="inquiry-form">
                {inquiryError && <div className="modal-error-banner">{inquiryError}</div>}

                <div className="form-group">
                  <label htmlFor="inquiry-name">Your Name</label>
                  <input
                    id="inquiry-name"
                    type="text"
                    required
                    placeholder="e.g. Rahul Sharma"
                    value={inquiryName}
                    onChange={(e) => setInquiryName(e.target.value)}
                  />
                </div>

                <div className="form-group">
                  <label htmlFor="inquiry-email">Your Email</label>
                  <input
                    id="inquiry-email"
                    type="email"
                    required
                    placeholder="you@example.com"
                    value={inquiryEmail}
                    onChange={(e) => setInquiryEmail(e.target.value)}
                  />
                </div>

                <div className="form-group">
                  <label htmlFor="inquiry-phone">Your Phone (Optional)</label>
                  <input
                    id="inquiry-phone"
                    type="tel"
                    placeholder="+91 98765 43210"
                    value={inquiryPhone}
                    onChange={(e) => setInquiryPhone(e.target.value)}
                  />
                </div>

                <div className="form-group">
                  <label htmlFor="inquiry-msg">Message</label>
                  <textarea
                    id="inquiry-msg"
                    rows={4}
                    required
                    value={inquiryMessage}
                    onChange={(e) => setInquiryMessage(e.target.value)}
                  />
                </div>

                <button
                  type="submit"
                  className="primary-button full-width"
                  disabled={inquirySubmitting}
                >
                  {inquirySubmitting ? 'Sending Request…' : '📩 Request Owner Contact & Tour'}
                </button>
              </form>
            )}
          </div>
        </aside>
      </div>
    </main>
  );
}
