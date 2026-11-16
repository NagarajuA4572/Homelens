import { useState, useEffect } from 'react';
import { useNavigate, Link } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';
import { inquiryService } from '../services/inquiryService';
import { resolveImageUrl, FALLBACK_PROPERTY_IMAGE } from '../services/api';

export default function SeekerInquiriesPage() {
  const { user, isAuthenticated, isOwner } = useAuth();
  const navigate = useNavigate();

  const [inquiries, setInquiries] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');

  useEffect(() => {
    if (!isAuthenticated) {
      navigate('/');
      return;
    }

    // If an owner visits this page, they can still view their sent requests or switch to dashboard
    loadSentInquiries();
  }, [isAuthenticated]);

  const loadSentInquiries = async () => {
    setLoading(true);
    setError('');
    try {
      const data = await inquiryService.getMySentInquiries(0, 50);
      setInquiries(data.content || []);
    } catch (err) {
      setError(
        'Unable to load your requests. Please check your network connection or try again in a few moments.'
      );
    } finally {
      setLoading(false);
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

  const getStatusBadge = (status) => {
    switch (status) {
      case 'ACCEPTED':
      case 'RESOLVED':
        return { label: '🎉 Request Accepted — Contact Unlocked', className: 'status-resolved' };
      case 'DENIED':
        return { label: '❌ Request Declined by Landlord', className: 'status-closed' };
      case 'CONTACTED':
        return { label: '📞 Owner Followed Up', className: 'status-contacted' };
      case 'CLOSED':
        return { label: '● Closed', className: 'status-closed' };
      case 'NEW':
      case 'PENDING':
      default:
        return { label: '⏳ Pending Landlord Review', className: 'status-new' };
    }
  };

  return (
    <main className="container dashboard-container seeker-dashboard">
      <div className="dashboard-header">
        <div>
          <h1>My Rental Requests & Inquiries</h1>
          <p className="subtitle">
            Track your pending tour requests, view landlord responses, and check booking updates in real time.
          </p>
        </div>
        <div className="header-actions">
          <Link to="/properties" className="primary-button">
            🔍 Browse More Rentals
          </Link>
        </div>
      </div>

      {error && <div className="error-banner">{error}</div>}

      {loading ? (
        <div className="loading-state">
          <div className="spinner"></div>
          <p>Loading your requests…</p>
        </div>
      ) : inquiries.length === 0 ? (
        <div className="empty-state-card">
          <div className="empty-icon">📬</div>
          <h3>No Rental Inquiries Sent Yet</h3>
          <p>
            When you find a house you love, click "Send Inquiry / Tour Request" on the property details page to connect directly with the owner.
          </p>
          <Link to="/properties" className="primary-button">
            Explore Property Catalog
          </Link>
        </div>
      ) : (
        <div className="seeker-inquiries-grid">
          {inquiries.map((inq) => {
            const badge = getStatusBadge(inq.status);
            const resolvedImg = resolveImageUrl(inq.propertyImageUrl) || FALLBACK_PROPERTY_IMAGE;

            return (
              <div key={inq.id} className="seeker-inquiry-card">
                <div className="seeker-card-media">
                  <img
                    src={resolvedImg}
                    alt={inq.propertyTitle || 'Property'}
                    className="seeker-card-thumb"
                    onError={(e) => {
                      e.target.src = FALLBACK_PROPERTY_IMAGE;
                    }}
                  />
                  <span className={`inquiry-status-badge ${badge.className}`}>
                    {badge.label}
                  </span>
                </div>

                <div className="seeker-card-body">
                  <div className="seeker-card-top">
                    <div>
                      <h3 className="seeker-prop-title">
                        <Link to={`/properties/${inq.propertyId}`}>
                          {inq.propertyTitle || `Property #${inq.propertyId}`}
                        </Link>
                      </h3>
                      {inq.propertyCity && (
                        <span className="seeker-prop-city">📍 {inq.propertyCity}</span>
                      )}
                    </div>

                    {inq.propertyRent && (
                      <div className="seeker-prop-rent">
                        <strong>{formatINR(inq.propertyRent)}</strong>
                        <span className="sub-unit">/mo</span>
                      </div>
                    )}
                  </div>

                  <div className="seeker-inquiry-quote">
                    <span className="quote-label">Your message to owner:</span>
                    <p className="quote-text">"{inq.message}"</p>
                  </div>

                  <div className="seeker-card-footer">
                    <span className="seeker-date">
                      Submitted on{' '}
                      {new Date(inq.createdAt).toLocaleDateString('en-IN', {
                        day: 'numeric',
                        month: 'short',
                        year: 'numeric',
                      })}
                    </span>

                    {inq.ownerName && (
                      <div className="seeker-owner-contact">
                        <span>Landlord: <strong>{inq.ownerName}</strong></span>
                        {(inq.status === 'ACCEPTED' || inq.status === 'RESOLVED') ? (
                          <div className="unlocked-contact-pills">
                            {inq.ownerPhone && (
                              <a href={`tel:${inq.ownerPhone}`} className="contact-chip highlight-chip">
                                📞 Call: {inq.ownerPhone}
                              </a>
                            )}
                            {inq.ownerEmail && (
                              <a href={`mailto:${inq.ownerEmail}`} className="contact-chip highlight-chip">
                                ✉️ Email: {inq.ownerEmail}
                              </a>
                            )}
                          </div>
                        ) : inq.status === 'DENIED' ? (
                          <span className="privacy-locked-text">❌ Landlord declined this request</span>
                        ) : (
                          <span className="privacy-locked-text">🔒 Contact details unlock when landlord accepts</span>
                        )}
                      </div>
                    )}
                  </div>
                </div>
              </div>
            );
          })}
        </div>
      )}
    </main>
  );
}
