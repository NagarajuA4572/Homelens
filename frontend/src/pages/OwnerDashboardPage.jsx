import { useState, useEffect, useRef } from 'react';
import { useNavigate, Link } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';
import { propertyService } from '../services/propertyService';
import { inquiryService } from '../services/inquiryService';
import { resolveImageUrl, FALLBACK_PROPERTY_IMAGE } from '../services/api';

export default function OwnerDashboardPage() {
  const { user, isAuthenticated, isOwner } = useAuth();
  const navigate = useNavigate();
  const apiUrl = import.meta.env.VITE_API_URL || 'http://localhost:8080';

  const [activeTab, setActiveTab] = useState('my-properties');
  const [properties, setProperties] = useState([]);
  const [inquiries, setInquiries] = useState([]);
  const [loading, setLoading] = useState(false);
  const [msg, setMsg] = useState('');
  const [error, setError] = useState('');

  // New property form state
  const initialForm = {
    title: '',
    description: '',
    monthlyRent: '',
    electricityCharges: '0',
    waterCharges: '0',
    bhkType: '2 BHK',
    propertyType: 'GATED_COMMUNITY',
    listingType: 'FOR_RENT',
    status: 'AVAILABLE',
    bedrooms: 2,
    bathrooms: 2,
    areaSqft: '',
    familyAllowed: true,
    singleAllowed: true,
    waterAvailability: true,
    distanceHospitalKm: '',
    distanceCollegeKm: '',
    distanceSchoolKm: '',
    distanceMetroKm: '',
    distanceBusStopKm: '',
    address: '',
    city: '',
    state: '',
    postalCode: '',
  };

  const [formData, setFormData] = useState(initialForm);

  // Edit property modal state
  const [editingProperty, setEditingProperty] = useState(null);
  const [editFormData, setEditFormData] = useState(null);
  const [updatingProperty, setUpdatingProperty] = useState(false);

  // Photo management state
  const [managingPhotosProperty, setManagingPhotosProperty] = useState(null);
  const [selectedFile, setSelectedFile] = useState(null);
  const [isPrimaryUpload, setIsPrimaryUpload] = useState(false);
  const [uploadingPhoto, setUploadingPhoto] = useState(false);
  const fileInputRef = useRef(null);

  useEffect(() => {
    if (!isAuthenticated) {
      navigate('/');
      return;
    }

    // Protect Owner Portal: Seekers should not see property registration or owner dashboard
    if (!isOwner) {
      navigate('/seeker/inquiries', { replace: true });
      return;
    }

    // Always fetch both properties and inquiries immediately upon opening dashboard
    loadAllDashboardData();
  }, [isAuthenticated, isOwner]);

  // When switching tabs, ensure active tab data is fresh
  useEffect(() => {
    if (isAuthenticated && isOwner) {
      if (activeTab === 'my-properties') {
        loadMyProperties();
      } else if (activeTab === 'inquiries') {
        loadMyInquiries();
      }
    }
  }, [activeTab]);

  const loadAllDashboardData = async () => {
    setLoading(true);
    setError('');
    try {
      const [propsData, inqsData] = await Promise.all([
        propertyService.getMyProperties(0, 50),
        inquiryService.getMyInquiries(0, 50),
      ]);
      setProperties(propsData.content || []);
      setInquiries(inqsData.content || []);
    } catch (err) {
      setError('Failed to refresh dashboard. Please check your network connection.');
    } finally {
      setLoading(false);
    }
  };

  const loadMyProperties = async () => {
    try {
      const data = await propertyService.getMyProperties(0, 50);
      setProperties(data.content || []);
    } catch (err) {
      setError('Failed to load your properties. Please check your network connection.');
    }
  };

  const loadMyInquiries = async () => {
    try {
      const data = await inquiryService.getMyInquiries(0, 50);
      setInquiries(data.content || []);
    } catch (err) {
      setError('Failed to load inquiries.');
    }
  };

  const handleToggleStatus = async (propId, currentStatus) => {
    const newStatus = currentStatus === 'AVAILABLE' ? 'RENTED' : 'AVAILABLE';
    const statusNote = newStatus === 'AVAILABLE' 
      ? 'Made Online: Property is now published in public listings.' 
      : 'Made Offline: Property is now hidden from public listings.';
    try {
      await propertyService.updatePropertyStatus(propId, newStatus);
      setMsg(statusNote);
      await loadMyProperties();
    } catch (err) {
      setError('Could not update property status.');
    }
  };

  const handleDeleteProperty = async (propId) => {
    if (!window.confirm('Are you sure you want to delete this property listing? This will also remove all attached photographs.')) {
      return;
    }
    try {
      await propertyService.deleteProperty(propId);
      setMsg('Property successfully deleted.');
      if (managingPhotosProperty?.id === propId) {
        setManagingPhotosProperty(null);
      }
      loadMyProperties();
    } catch (err) {
      setError('Could not delete property.');
    }
  };

  const handleCreateSubmit = async (e) => {
    e.preventDefault();
    setLoading(true);
    setError('');
    setMsg('');

    try {
      const payload = {
        ...formData,
        monthlyRent: parseFloat(formData.monthlyRent),
        electricityCharges: parseFloat(formData.electricityCharges || '0'),
        waterCharges: parseFloat(formData.waterCharges || '0'),
        bedrooms: parseInt(formData.bedrooms, 10),
        bathrooms: parseInt(formData.bathrooms, 10),
        areaSqft: formData.areaSqft ? parseFloat(formData.areaSqft) : null,
        distanceHospitalKm: formData.distanceHospitalKm ? parseFloat(formData.distanceHospitalKm) : null,
        distanceCollegeKm: formData.distanceCollegeKm ? parseFloat(formData.distanceCollegeKm) : null,
        distanceSchoolKm: formData.distanceSchoolKm ? parseFloat(formData.distanceSchoolKm) : null,
        distanceMetroKm: formData.distanceMetroKm ? parseFloat(formData.distanceMetroKm) : null,
        distanceBusStopKm: formData.distanceBusStopKm ? parseFloat(formData.distanceBusStopKm) : null,
      };

      const created = await propertyService.createProperty(payload);
      setMsg(`Property "${created.title}" successfully registered!`);
      setFormData(initialForm);
      setActiveTab('my-properties');
    } catch (err) {
      setError(err.response?.data?.message || 'Could not register property.');
    } finally {
      setLoading(false);
    }
  };

  // Open Edit Modal
  const handleOpenEdit = (prop) => {
    setEditingProperty(prop);
    setEditFormData({
      title: prop.title || '',
      description: prop.description || '',
      monthlyRent: prop.monthlyRent || '',
      electricityCharges: prop.electricityCharges || 0,
      waterCharges: prop.waterCharges || 0,
      bhkType: prop.bhkType || '2 BHK',
      propertyType: prop.propertyType || 'GATED_COMMUNITY',
      listingType: prop.listingType || 'FOR_RENT',
      status: prop.status || 'AVAILABLE',
      bedrooms: prop.bedrooms || 2,
      bathrooms: prop.bathrooms || 2,
      areaSqft: prop.areaSqft || '',
      familyAllowed: prop.familyAllowed ?? true,
      singleAllowed: prop.singleAllowed ?? true,
      waterAvailability: prop.waterAvailability ?? true,
      distanceHospitalKm: prop.distanceHospitalKm ?? '',
      distanceCollegeKm: prop.distanceCollegeKm ?? '',
      distanceSchoolKm: prop.distanceSchoolKm ?? '',
      distanceMetroKm: prop.distanceMetroKm ?? '',
      distanceBusStopKm: prop.distanceBusStopKm ?? '',
      address: prop.address || '',
      city: prop.city || '',
      state: prop.state || '',
      postalCode: prop.postalCode || '',
    });
  };

  const handleEditSubmit = async (e) => {
    e.preventDefault();
    if (!editingProperty || !editFormData) return;

    setUpdatingProperty(true);
    setError('');
    try {
      const payload = {
        ...editFormData,
        monthlyRent: parseFloat(editFormData.monthlyRent),
        electricityCharges: parseFloat(editFormData.electricityCharges || '0'),
        waterCharges: parseFloat(editFormData.waterCharges || '0'),
        bedrooms: parseInt(editFormData.bedrooms, 10),
        bathrooms: parseInt(editFormData.bathrooms, 10),
        areaSqft: editFormData.areaSqft ? parseFloat(editFormData.areaSqft) : null,
        distanceHospitalKm: editFormData.distanceHospitalKm ? parseFloat(editFormData.distanceHospitalKm) : null,
        distanceCollegeKm: editFormData.distanceCollegeKm ? parseFloat(editFormData.distanceCollegeKm) : null,
        distanceSchoolKm: editFormData.distanceSchoolKm ? parseFloat(editFormData.distanceSchoolKm) : null,
        distanceMetroKm: editFormData.distanceMetroKm ? parseFloat(editFormData.distanceMetroKm) : null,
        distanceBusStopKm: editFormData.distanceBusStopKm ? parseFloat(editFormData.distanceBusStopKm) : null,
      };

      await propertyService.updateProperty(editingProperty.id, payload);
      setMsg(`Property "${payload.title}" updated successfully!`);
      setEditingProperty(null);
      setEditFormData(null);
      loadMyProperties();
    } catch (err) {
      setError(err.response?.data?.message || 'Failed to update property details.');
    } finally {
      setUpdatingProperty(false);
    }
  };

  // Photo Management
  const handleOpenPhotoManager = (prop) => {
    setManagingPhotosProperty(prop);
    setSelectedFile(null);
    setIsPrimaryUpload(false);
  };

  const handleUploadPhoto = async (e) => {
    e.preventDefault();
    if (!managingPhotosProperty || !selectedFile) return;

    setUploadingPhoto(true);
    setError('');
    try {
      const updatedProp = await propertyService.uploadPropertyImage(
        managingPhotosProperty.id,
        selectedFile,
        isPrimaryUpload
      );
      setMsg('Photograph uploaded and saved successfully!');
      setSelectedFile(null);
      setIsPrimaryUpload(false);
      if (fileInputRef.current) {
        fileInputRef.current.value = '';
      }
      setManagingPhotosProperty(updatedProp);
      await loadMyProperties();
    } catch (err) {
      setError('Failed to upload image. Supported formats: JPG, PNG, WEBP.');
    } finally {
      setUploadingPhoto(false);
    }
  };

  const handleDeletePhoto = async (imageId) => {
    if (!managingPhotosProperty) return;
    if (!window.confirm('Delete this photograph?')) return;

    try {
      const updatedProp = await propertyService.deletePropertyImage(managingPhotosProperty.id, imageId);
      setMsg('Photograph deleted.');
      setManagingPhotosProperty(updatedProp);
      await loadMyProperties();
    } catch (err) {
      setError('Failed to delete photograph.');
    }
  };

  // Inquiry Management
  const handleUpdateInquiryStatus = async (inquiryId, newStatus) => {
    try {
      await inquiryService.updateInquiryStatus(inquiryId, newStatus);
      if (newStatus === 'ACCEPTED' || newStatus === 'RESOLVED') {
        setMsg('🎉 Request accepted! Seeker is now granted access to your contact details, and this property has been automatically taken offline from public listings.');
        await Promise.all([loadMyProperties(), loadMyInquiries()]);
      } else if (newStatus === 'DENIED') {
        setMsg('Request denied. The property remains in its current listing state.');
        await loadMyInquiries();
      } else {
        setMsg(`Inquiry status updated to ${newStatus}.`);
        await loadMyInquiries();
      }
    } catch (err) {
      setError('Failed to update inquiry status.');
    }
  };

  const handleDeleteInquiry = async (inquiryId) => {
    if (!window.confirm('Dismiss this inquiry?')) return;
    try {
      await inquiryService.deleteInquiry(inquiryId);
      setMsg('Inquiry dismissed.');
      loadMyInquiries();
    } catch (err) {
      setError('Failed to dismiss inquiry.');
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

  return (
    <main className="container dashboard-container">
      <div className="dashboard-header">
        <div>
          <h1>Property Owner Portal</h1>
          <p className="subtitle">
            Manage your registered rentals, update charges, toggle availability, manage photographs, and track tenant inquiries.
          </p>
        </div>
      </div>

      {msg && <div className="success-banner">{msg}</div>}
      {error && <div className="error-banner">{error}</div>}

      {/* Tabs */}
      <div className="dashboard-tabs">
        <button
          type="button"
          className={`tab-btn ${activeTab === 'my-properties' ? 'active' : ''}`}
          onClick={() => setActiveTab('my-properties')}
        >
          📋 My Properties ({properties.length})
        </button>
        <button
          type="button"
          className={`tab-btn ${activeTab === 'register' ? 'active' : ''}`}
          onClick={() => setActiveTab('register')}
        >
          ➕ Register New Property
        </button>
        <button
          type="button"
          className={`tab-btn ${activeTab === 'inquiries' ? 'active' : ''}`}
          onClick={() => setActiveTab('inquiries')}
        >
          📬 Inquiries Received ({inquiries.length})
        </button>
      </div>

      {/* TAB 1: MY PROPERTIES */}
      {activeTab === 'my-properties' && (
        <section className="dashboard-content">
          {loading ? (
            <div className="loading-state">
              <div className="spinner"></div>
              <p>Loading your properties…</p>
            </div>
          ) : properties.length === 0 ? (
            <div className="empty-state-card">
              <div className="empty-icon">🏢</div>
              <h3>No Properties Registered Yet</h3>
              <p>You haven't listed any rental properties. Register your first listing to start receiving tenant inquiries.</p>
              <button onClick={() => setActiveTab('register')} className="primary-button">
                + Register First Property
              </button>
            </div>
          ) : (
            <div className="owner-table-wrapper">
              <table className="owner-properties-table">
                <thead>
                  <tr>
                    <th>Property</th>
                    <th>Type & BHK</th>
                    <th>Monthly Rent</th>
                    <th>Additional Charges</th>
                    <th>Status</th>
                    <th>Water & Occupancy</th>
                    <th>Actions</th>
                  </tr>
                </thead>
                <tbody>
                  {properties.map((p) => {
                    const isAvailable = p.status === 'AVAILABLE';
                    return (
                      <tr key={p.id}>
                        <td>
                          <div className="prop-table-cell">
                            <strong>{p.title}</strong>
                            <span className="prop-cell-location">📍 {p.city}, {p.state}</span>
                            <span className="prop-cell-photos">📸 {p.images?.length || 0} photos</span>
                          </div>
                        </td>
                        <td>
                          <span className="bhk-pill">{p.bhkType}</span>
                          <span className="table-type">
                            {p.propertyType === 'GATED_COMMUNITY' ? 'Gated Comm.' : 'Indiv. House'}
                          </span>
                        </td>
                        <td>
                          <strong>{formatINR(p.monthlyRent)}</strong>
                          <span className="sub-unit">/mo</span>
                        </td>
                        <td>
                          <div className="charges-summary">
                            <span>⚡ Elec: {formatINR(p.electricityCharges)}</span>
                            <span>💧 Water: {formatINR(p.waterCharges)}</span>
                          </div>
                        </td>
                        <td>
                          <div className="status-cell-wrapper">
                            <span className={`listing-state-tag ${isAvailable ? 'tag-online' : 'tag-offline'}`}>
                              {isAvailable ? '🟢 Online (In Listings)' : '🔴 Offline (Hidden)'}
                            </span>
                            <button
                              type="button"
                              onClick={() => handleToggleStatus(p.id, p.status)}
                              className={`status-action-pill ${isAvailable ? 'btn-make-offline' : 'btn-make-online'}`}
                              title={isAvailable ? "Click to take property offline (remove from public listings)" : "Click to make property online (publish to public listings)"}
                            >
                              {isAvailable ? '📴 Make Offline' : '🌐 Make Online'}
                            </button>
                          </div>
                        </td>
                        <td>
                          <div className="rules-summary">
                            <span>💧 Water: {p.waterAvailability ? 'Yes' : 'No'}</span>
                            <span>👥 {p.familyAllowed ? 'Family' : ''}{p.familyAllowed && p.singleAllowed ? ' & ' : ''}{p.singleAllowed ? 'Single' : ''}</span>
                          </div>
                        </td>
                        <td>
                          <div className="action-buttons-group">
                            <Link to={`/properties/${p.id}`} className="view-link-btn" title="View Public Listing">
                              👁️
                            </Link>
                            <button
                              type="button"
                              onClick={() => handleOpenEdit(p)}
                              className="edit-prop-btn"
                              title="Edit Property Info & Rent"
                            >
                              ✏️
                            </button>
                            <button
                              type="button"
                              onClick={() => handleOpenPhotoManager(p)}
                              className="upload-photo-btn"
                              title="Manage Photographs"
                            >
                              📷
                            </button>
                            <button
                              type="button"
                              onClick={() => handleDeleteProperty(p.id)}
                              className="delete-prop-btn"
                              title="Delete Listing"
                            >
                              🗑️
                            </button>
                          </div>
                        </td>
                      </tr>
                    );
                  })}
                </tbody>
              </table>
            </div>
          )}

          {/* Photo Manager Drawer */}
          {managingPhotosProperty && (
            <div className="photo-manager-drawer">
              <div className="photo-manager-header">
                <div>
                  <h4>📸 Photo Manager: {managingPhotosProperty.title}</h4>
                  <p className="subtext">View, remove, or upload high-resolution photographs for this rental listing.</p>
                </div>
                <button
                  type="button"
                  className="secondary-button close-drawer-btn"
                  onClick={() => setManagingPhotosProperty(null)}
                >
                  ✕ Close
                </button>
              </div>

              {/* Existing Photos Grid */}
              <div className="photos-grid-preview">
                {(!managingPhotosProperty.images || managingPhotosProperty.images.length === 0) ? (
                  <div className="no-photos-alert">
                    <p>No photos uploaded yet for this property. Add photos below to attract more prospective tenants.</p>
                  </div>
                ) : (
                  managingPhotosProperty.images.map((img) => {
                    const fullImgUrl = resolveImageUrl(img.imageUrl) || FALLBACK_PROPERTY_IMAGE;
                    return (
                      <div key={img.id} className="photo-manager-card">
                        <img
                          src={fullImgUrl}
                          alt="Property"
                          className="photo-manager-thumb"
                          onError={(e) => {
                            e.target.src = FALLBACK_PROPERTY_IMAGE;
                          }}
                        />
                        <div className="photo-manager-card-overlay">
                          {img.isPrimary && <span className="photo-badge-primary">★ Primary</span>}
                          <button
                            type="button"
                            onClick={() => handleDeletePhoto(img.id)}
                            className="photo-delete-btn"
                            title="Delete photograph"
                          >
                            🗑️ Delete
                          </button>
                        </div>
                      </div>
                    );
                  })
                )}
              </div>

              {/* Upload New Photo Form */}
              <form onSubmit={handleUploadPhoto} className="upload-photo-bar">
                <div className="upload-photo-bar-title">
                  <h5>Add New Photograph</h5>
                  <button
                    type="button"
                    onClick={async () => {
                      try {
                        const refreshed = await propertyService.getPropertyById(managingPhotosProperty.id);
                        setManagingPhotosProperty(refreshed);
                        loadMyProperties();
                        setMsg('Photo gallery refreshed.');
                      } catch (e) {}
                    }}
                    className="refresh-photos-btn"
                    title="Refresh photo gallery from database"
                  >
                    🔄 Refresh Gallery
                  </button>
                </div>
                <div className="upload-inputs-row">
                  <input
                    ref={fileInputRef}
                    type="file"
                    accept="image/jpeg,image/png,image/webp"
                    onChange={(e) => setSelectedFile(e.target.files && e.target.files[0] ? e.target.files[0] : null)}
                    required
                  />
                  {selectedFile && (
                    <span className="selected-file-pill">
                      📎 {selectedFile.name} ({(selectedFile.size / 1024).toFixed(0)} KB)
                    </span>
                  )}
                  <label className="checkbox-label">
                    <input
                      type="checkbox"
                      checked={isPrimaryUpload}
                      onChange={(e) => setIsPrimaryUpload(e.target.checked)}
                    />
                    <span>Set as primary photo</span>
                  </label>
                  <button type="submit" className="primary-button" disabled={uploadingPhoto || !selectedFile}>
                    {uploadingPhoto ? 'Uploading…' : '⬆ Upload Photo'}
                  </button>
                </div>
              </form>
            </div>
          )}
        </section>
      )}

      {/* EDIT PROPERTY MODAL */}
      {editingProperty && editFormData && (
        <div className="modal-overlay" onClick={() => setEditingProperty(null)}>
          <div className="modal-content edit-property-modal" onClick={(e) => e.stopPropagation()}>
            <div className="modal-header">
              <h2>✏️ Edit Property: {editingProperty.title}</h2>
              <button
                type="button"
                className="modal-close-btn"
                onClick={() => setEditingProperty(null)}
              >
                ✕
              </button>
            </div>

            <form onSubmit={handleEditSubmit} className="edit-property-form">
              <div className="form-section-card">
                <h3>1. Basic Information</h3>
                <div className="form-grid-2">
                  <div className="form-group full-span">
                    <label>Property Title *</label>
                    <input
                      type="text"
                      required
                      value={editFormData.title}
                      onChange={(e) => setEditFormData({ ...editFormData, title: e.target.value })}
                    />
                  </div>
                  <div className="form-group full-span">
                    <label>Description</label>
                    <textarea
                      rows={3}
                      value={editFormData.description}
                      onChange={(e) => setEditFormData({ ...editFormData, description: e.target.value })}
                    />
                  </div>
                  <div className="form-group">
                    <label>Property Type *</label>
                    <select
                      value={editFormData.propertyType}
                      onChange={(e) => setEditFormData({ ...editFormData, propertyType: e.target.value })}
                    >
                      <option value="GATED_COMMUNITY">🛡️ Gated Community</option>
                      <option value="INDIVIDUAL_HOUSE">🏡 Individual House</option>
                    </select>
                  </div>
                  <div className="form-group">
                    <label>BHK Configuration *</label>
                    <select
                      value={editFormData.bhkType}
                      onChange={(e) => setEditFormData({ ...editFormData, bhkType: e.target.value })}
                    >
                      <option value="1 RK">1 RK</option>
                      <option value="1 BHK">1 BHK</option>
                      <option value="2 BHK">2 BHK</option>
                      <option value="3 BHK">3 BHK</option>
                      <option value="4 BHK">4 BHK</option>
                      <option value="4+ BHK">4+ BHK</option>
                    </select>
                  </div>
                  <div className="form-group">
                    <label>Bedrooms</label>
                    <input
                      type="number"
                      min="1"
                      value={editFormData.bedrooms}
                      onChange={(e) => setEditFormData({ ...editFormData, bedrooms: e.target.value })}
                    />
                  </div>
                  <div className="form-group">
                    <label>Bathrooms</label>
                    <input
                      type="number"
                      min="1"
                      value={editFormData.bathrooms}
                      onChange={(e) => setEditFormData({ ...editFormData, bathrooms: e.target.value })}
                    />
                  </div>
                  <div className="form-group">
                    <label>Built-up Area (sqft)</label>
                    <input
                      type="number"
                      value={editFormData.areaSqft}
                      onChange={(e) => setEditFormData({ ...editFormData, areaSqft: e.target.value })}
                    />
                  </div>
                  <div className="form-group">
                    <label>Availability Status</label>
                    <select
                      value={editFormData.status}
                      onChange={(e) => setEditFormData({ ...editFormData, status: e.target.value })}
                    >
                      <option value="AVAILABLE">Available for Rent</option>
                      <option value="RENTED">Already Rented</option>
                    </select>
                  </div>
                </div>
              </div>

              {/* Financial Details */}
              <div className="form-section-card">
                <h3>2. Rent & Additional Charges</h3>
                <div className="form-grid-3">
                  <div className="form-group">
                    <label>Base Monthly Rent (INR ₹) *</label>
                    <input
                      type="number"
                      required
                      value={editFormData.monthlyRent}
                      onChange={(e) => setEditFormData({ ...editFormData, monthlyRent: e.target.value })}
                    />
                  </div>
                  <div className="form-group">
                    <label>Electricity Charges (INR ₹)</label>
                    <input
                      type="number"
                      value={editFormData.electricityCharges}
                      onChange={(e) => setEditFormData({ ...editFormData, electricityCharges: e.target.value })}
                    />
                  </div>
                  <div className="form-group">
                    <label>Water Charges (INR ₹)</label>
                    <input
                      type="number"
                      value={editFormData.waterCharges}
                      onChange={(e) => setEditFormData({ ...editFormData, waterCharges: e.target.value })}
                    />
                  </div>
                </div>
              </div>

              {/* Occupancy & Water */}
              <div className="form-section-card">
                <h3>3. Water Availability & Occupancy Rules</h3>
                <div className="form-grid-3">
                  <label className="checkbox-toggle-card">
                    <input
                      type="checkbox"
                      checked={editFormData.waterAvailability}
                      onChange={(e) => setEditFormData({ ...editFormData, waterAvailability: e.target.checked })}
                    />
                    <span>💧 Water Availability</span>
                  </label>
                  <label className="checkbox-toggle-card">
                    <input
                      type="checkbox"
                      checked={editFormData.familyAllowed}
                      onChange={(e) => setEditFormData({ ...editFormData, familyAllowed: e.target.checked })}
                    />
                    <span>👨‍👩‍👧 Family Allowed</span>
                  </label>
                  <label className="checkbox-toggle-card">
                    <input
                      type="checkbox"
                      checked={editFormData.singleAllowed}
                      onChange={(e) => setEditFormData({ ...editFormData, singleAllowed: e.target.checked })}
                    />
                    <span>👤 Single Allowed</span>
                  </label>
                </div>
              </div>

              {/* Nearby Facilities */}
              <div className="form-section-card">
                <h3>4. Nearby Facility Distances (km)</h3>
                <div className="form-grid-3">
                  <div className="form-group">
                    <label>🏥 Hospital (km)</label>
                    <input
                      type="number"
                      step="0.1"
                      value={editFormData.distanceHospitalKm}
                      onChange={(e) => setEditFormData({ ...editFormData, distanceHospitalKm: e.target.value })}
                    />
                  </div>
                  <div className="form-group">
                    <label>🚇 Metro (km)</label>
                    <input
                      type="number"
                      step="0.1"
                      value={editFormData.distanceMetroKm}
                      onChange={(e) => setEditFormData({ ...editFormData, distanceMetroKm: e.target.value })}
                    />
                  </div>
                  <div className="form-group">
                    <label>🚌 Bus Stop (km)</label>
                    <input
                      type="number"
                      step="0.1"
                      value={editFormData.distanceBusStopKm}
                      onChange={(e) => setEditFormData({ ...editFormData, distanceBusStopKm: e.target.value })}
                    />
                  </div>
                  <div className="form-group">
                    <label>🏫 School (km)</label>
                    <input
                      type="number"
                      step="0.1"
                      value={editFormData.distanceSchoolKm}
                      onChange={(e) => setEditFormData({ ...editFormData, distanceSchoolKm: e.target.value })}
                    />
                  </div>
                  <div className="form-group">
                    <label>🎓 College (km)</label>
                    <input
                      type="number"
                      step="0.1"
                      value={editFormData.distanceCollegeKm}
                      onChange={(e) => setEditFormData({ ...editFormData, distanceCollegeKm: e.target.value })}
                    />
                  </div>
                </div>
              </div>

              {/* Address */}
              <div className="form-section-card">
                <h3>5. Address</h3>
                <div className="form-grid-2">
                  <div className="form-group full-span">
                    <label>Address *</label>
                    <input
                      type="text"
                      required
                      value={editFormData.address}
                      onChange={(e) => setEditFormData({ ...editFormData, address: e.target.value })}
                    />
                  </div>
                  <div className="form-group">
                    <label>City *</label>
                    <input
                      type="text"
                      required
                      value={editFormData.city}
                      onChange={(e) => setEditFormData({ ...editFormData, city: e.target.value })}
                    />
                  </div>
                  <div className="form-group">
                    <label>State *</label>
                    <input
                      type="text"
                      required
                      value={editFormData.state}
                      onChange={(e) => setEditFormData({ ...editFormData, state: e.target.value })}
                    />
                  </div>
                  <div className="form-group">
                    <label>Postal Code</label>
                    <input
                      type="text"
                      value={editFormData.postalCode}
                      onChange={(e) => setEditFormData({ ...editFormData, postalCode: e.target.value })}
                    />
                  </div>
                </div>
              </div>

              <div className="form-actions-row">
                <button type="submit" className="primary-button" disabled={updatingProperty}>
                  {updatingProperty ? 'Saving Changes…' : '✓ Save Property Changes'}
                </button>
                <button
                  type="button"
                  className="secondary-button"
                  onClick={() => setEditingProperty(null)}
                >
                  Cancel
                </button>
              </div>
            </form>
          </div>
        </div>
      )}

      {/* TAB 2: REGISTER NEW PROPERTY */}
      {activeTab === 'register' && (
        <section className="dashboard-content">
          <form onSubmit={handleCreateSubmit} className="register-property-form">
            <h2>Register Rental Property Listing</h2>
            <p className="form-subhead">Enter complete property details, charges, and nearby facility distances.</p>

            {/* Basic Info */}
            <div className="form-section-card">
              <h3>1. Basic Information</h3>
              <div className="form-grid-2">
                <div className="form-group full-span">
                  <label>Property Title *</label>
                  <input
                    type="text"
                    required
                    placeholder="e.g. Skyline Residency 3 BHK Gated Flat"
                    value={formData.title}
                    onChange={(e) => setFormData({ ...formData, title: e.target.value })}
                  />
                </div>

                <div className="form-group full-span">
                  <label>Description</label>
                  <textarea
                    rows={3}
                    placeholder="Highlight features: ventilation, modular kitchen, balcony view, security..."
                    value={formData.description}
                    onChange={(e) => setFormData({ ...formData, description: e.target.value })}
                  />
                </div>

                <div className="form-group">
                  <label>Property Type *</label>
                  <select
                    value={formData.propertyType}
                    onChange={(e) => setFormData({ ...formData, propertyType: e.target.value })}
                  >
                    <option value="GATED_COMMUNITY">🛡️ Gated Community</option>
                    <option value="INDIVIDUAL_HOUSE">🏡 Individual House</option>
                  </select>
                </div>

                <div className="form-group">
                  <label>BHK Configuration *</label>
                  <select
                    value={formData.bhkType}
                    onChange={(e) => setFormData({ ...formData, bhkType: e.target.value })}
                  >
                    <option value="1 RK">1 RK</option>
                    <option value="1 BHK">1 BHK</option>
                    <option value="2 BHK">2 BHK</option>
                    <option value="3 BHK">3 BHK</option>
                    <option value="4 BHK">4 BHK</option>
                    <option value="4+ BHK">4+ BHK</option>
                  </select>
                </div>

                <div className="form-group">
                  <label>Bedrooms Count</label>
                  <input
                    type="number"
                    min="1"
                    value={formData.bedrooms}
                    onChange={(e) => setFormData({ ...formData, bedrooms: e.target.value })}
                  />
                </div>

                <div className="form-group">
                  <label>Bathrooms Count</label>
                  <input
                    type="number"
                    min="1"
                    value={formData.bathrooms}
                    onChange={(e) => setFormData({ ...formData, bathrooms: e.target.value })}
                  />
                </div>

                <div className="form-group">
                  <label>Built-up Area (sqft)</label>
                  <input
                    type="number"
                    placeholder="e.g. 1500"
                    value={formData.areaSqft}
                    onChange={(e) => setFormData({ ...formData, areaSqft: e.target.value })}
                  />
                </div>

                <div className="form-group">
                  <label>Initial Status</label>
                  <select
                    value={formData.status}
                    onChange={(e) => setFormData({ ...formData, status: e.target.value })}
                  >
                    <option value="AVAILABLE">Available for Rent</option>
                    <option value="RENTED">Already Rented</option>
                  </select>
                </div>
              </div>
            </div>

            {/* Financial Details */}
            <div className="form-section-card">
              <h3>2. Monthly Rent & Additional Charges</h3>
              <div className="form-grid-3">
                <div className="form-group">
                  <label>Base Monthly Rent (INR ₹) *</label>
                  <input
                    type="number"
                    required
                    placeholder="e.g. 45000"
                    value={formData.monthlyRent}
                    onChange={(e) => setFormData({ ...formData, monthlyRent: e.target.value })}
                  />
                </div>

                <div className="form-group">
                  <label>Monthly Electricity Charges (INR ₹)</label>
                  <input
                    type="number"
                    placeholder="0 if as per meter"
                    value={formData.electricityCharges}
                    onChange={(e) => setFormData({ ...formData, electricityCharges: e.target.value })}
                  />
                </div>

                <div className="form-group">
                  <label>Monthly Water Charges (INR ₹)</label>
                  <input
                    type="number"
                    placeholder="0 if included"
                    value={formData.waterCharges}
                    onChange={(e) => setFormData({ ...formData, waterCharges: e.target.value })}
                  />
                </div>
              </div>
            </div>

            {/* Occupancy & Water */}
            <div className="form-section-card">
              <h3>3. Water Availability & Occupancy Rules</h3>
              <div className="form-grid-3">
                <div className="checkbox-toggle-card">
                  <label>
                    <input
                      type="checkbox"
                      checked={formData.waterAvailability}
                      onChange={(e) => setFormData({ ...formData, waterAvailability: e.target.checked })}
                    />
                    <span>💧 Water Availability (Yes / No)</span>
                  </label>
                  <small>Guaranteed continuous water supply</small>
                </div>

                <div className="checkbox-toggle-card">
                  <label>
                    <input
                      type="checkbox"
                      checked={formData.familyAllowed}
                      onChange={(e) => setFormData({ ...formData, familyAllowed: e.target.checked })}
                    />
                    <span>👨‍👩‍👧 Family Allowed</span>
                  </label>
                  <small>Families permitted</small>
                </div>

                <div className="checkbox-toggle-card">
                  <label>
                    <input
                      type="checkbox"
                      checked={formData.singleAllowed}
                      onChange={(e) => setFormData({ ...formData, singleAllowed: e.target.checked })}
                    />
                    <span>👤 Single Occupancy Allowed</span>
                  </label>
                  <small>Bachelors / singles permitted</small>
                </div>
              </div>
            </div>

            {/* Nearby Facilities */}
            <div className="form-section-card">
              <h3>4. Nearby Facilities & Approximate Distances (in km)</h3>
              <div className="form-grid-3">
                <div className="form-group">
                  <label>🏥 Distance to Hospital (km)</label>
                  <input
                    type="number"
                    step="0.1"
                    placeholder="e.g. 1.5"
                    value={formData.distanceHospitalKm}
                    onChange={(e) => setFormData({ ...formData, distanceHospitalKm: e.target.value })}
                  />
                </div>

                <div className="form-group">
                  <label>🚇 Distance to Metro Station (km)</label>
                  <input
                    type="number"
                    step="0.1"
                    placeholder="e.g. 0.8"
                    value={formData.distanceMetroKm}
                    onChange={(e) => setFormData({ ...formData, distanceMetroKm: e.target.value })}
                  />
                </div>

                <div className="form-group">
                  <label>🚌 Distance to Bus Stop (km)</label>
                  <input
                    type="number"
                    step="0.1"
                    placeholder="e.g. 0.3"
                    value={formData.distanceBusStopKm}
                    onChange={(e) => setFormData({ ...formData, distanceBusStopKm: e.target.value })}
                  />
                </div>

                <div className="form-group">
                  <label>🏫 Distance to School (km)</label>
                  <input
                    type="number"
                    step="0.1"
                    placeholder="e.g. 1.2"
                    value={formData.distanceSchoolKm}
                    onChange={(e) => setFormData({ ...formData, distanceSchoolKm: e.target.value })}
                  />
                </div>

                <div className="form-group">
                  <label>🎓 Distance to College (km)</label>
                  <input
                    type="number"
                    step="0.1"
                    placeholder="e.g. 3.0"
                    value={formData.distanceCollegeKm}
                    onChange={(e) => setFormData({ ...formData, distanceCollegeKm: e.target.value })}
                  />
                </div>
              </div>
            </div>

            {/* Location & Address */}
            <div className="form-section-card">
              <h3>5. Location & Complete Address</h3>
              <div className="form-grid-2">
                <div className="form-group full-span">
                  <label>Complete Street Address *</label>
                  <input
                    type="text"
                    required
                    placeholder="House/Flat No., Building Name, Street, Landmark"
                    value={formData.address}
                    onChange={(e) => setFormData({ ...formData, address: e.target.value })}
                  />
                </div>

                <div className="form-group">
                  <label>City *</label>
                  <input
                    type="text"
                    required
                    placeholder="e.g. Bengaluru"
                    value={formData.city}
                    onChange={(e) => setFormData({ ...formData, city: e.target.value })}
                  />
                </div>

                <div className="form-group">
                  <label>State *</label>
                  <input
                    type="text"
                    required
                    placeholder="e.g. Karnataka"
                    value={formData.state}
                    onChange={(e) => setFormData({ ...formData, state: e.target.value })}
                  />
                </div>

                <div className="form-group">
                  <label>PIN / Postal Code</label>
                  <input
                    type="text"
                    placeholder="e.g. 560001"
                    value={formData.postalCode}
                    onChange={(e) => setFormData({ ...formData, postalCode: e.target.value })}
                  />
                </div>
              </div>
            </div>

            <div className="form-actions-row">
              <button type="submit" className="primary-button submit-listing-btn" disabled={loading}>
                {loading ? 'Submitting Property…' : '✓ Register Property'}
              </button>
            </div>
          </form>
        </section>
      )}

      {/* TAB 3: INQUIRIES RECEIVED */}
      {activeTab === 'inquiries' && (
        <section className="dashboard-content">
          {loading ? (
            <div className="loading-state">
              <div className="spinner"></div>
              <p>Loading inquiries…</p>
            </div>
          ) : inquiries.length === 0 ? (
            <div className="empty-state-card">
              <div className="empty-icon">📬</div>
              <h3>No Inquiries Received Yet</h3>
              <p>When prospective tenants submit tour requests on your listings, they will appear here with lead details.</p>
            </div>
          ) : (
            <div className="inquiries-list">
              {inquiries.map((inq) => {
                const statusClass = `status-${inq.status?.toLowerCase() || 'new'}`;
                return (
                  <div key={inq.id} className="inquiry-item-card">
                    <div className="inquiry-item-header">
                      <div>
                        <h4>{inq.name}</h4>
                        <div className="inquiry-property-title">
                          Property: <strong>{inq.propertyTitle || `#${inq.propertyId}`}</strong>
                        </div>
                      </div>
                      <div className="inquiry-header-actions">
                        <span className={`inquiry-status-badge ${statusClass}`}>
                          {inq.status === 'ACCEPTED'
                            ? '✅ Accepted / Rented'
                            : inq.status === 'DENIED'
                            ? '❌ Denied'
                            : inq.status === 'CONTACTED'
                            ? '📞 Contacted'
                            : inq.status === 'CLOSED'
                            ? '🔒 Closed'
                            : '⏳ Pending Review'}
                        </span>
                        <span className="inquiry-date">
                          {new Date(inq.createdAt).toLocaleDateString('en-IN', {
                            day: 'numeric',
                            month: 'short',
                            year: 'numeric',
                          })}
                        </span>
                      </div>
                    </div>

                    <p className="inquiry-message">"{inq.message}"</p>

                    <div className="inquiry-footer-row">
                      <div className="inquiry-contact-chips">
                        <a href={`mailto:${inq.email}`} className="contact-chip">✉️ {inq.email}</a>
                        {inq.phone && <a href={`tel:${inq.phone}`} className="contact-chip">📞 {inq.phone}</a>}
                      </div>

                      <div className="inquiry-status-controls">
                        <label>Owner Decision / Status:</label>
                        <select
                          value={inq.status || 'NEW'}
                          onChange={(e) => handleUpdateInquiryStatus(inq.id, e.target.value)}
                          className={`inquiry-status-select select-${inq.status?.toLowerCase() || 'new'}`}
                        >
                          <option value="NEW">⏳ Pending Review</option>
                          <option value="ACCEPTED">✅ Accept Request (Approve Seeker & Delist House)</option>
                          <option value="DENIED">❌ Deny Request (Decline Seeker)</option>
                          <option value="CONTACTED">📞 In Contact / Follow Up</option>
                          <option value="CLOSED">🔒 Closed</option>
                        </select>
                        <button
                          type="button"
                          onClick={() => handleDeleteInquiry(inq.id)}
                          className="inquiry-delete-btn"
                          title="Dismiss inquiry"
                        >
                          🗑️
                        </button>
                      </div>
                    </div>
                  </div>
                );
              })}
            </div>
          )}
        </section>
      )}
    </main>
  );
}
