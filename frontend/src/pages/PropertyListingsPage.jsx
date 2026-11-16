import { useState, useEffect } from 'react';
import { useSearchParams } from 'react-router-dom';
import { propertyService } from '../services/propertyService';
import PropertyCard from '../components/PropertyCard';
import PropertyFilters from '../components/PropertyFilters';

export default function PropertyListingsPage() {
  const [searchParams, setSearchParams] = useSearchParams();

  // Initialize filters from URL query parameters
  const [filters, setFilters] = useState({
    query: searchParams.get('query') || '',
    city: searchParams.get('city') || '',
    bhkType: searchParams.get('bhkType') || '',
    propertyType: searchParams.get('propertyType') || '',
    minRent: searchParams.get('minRent') || '',
    maxRent: searchParams.get('maxRent') || '',
    familyAllowed: searchParams.get('familyAllowed') === 'true' ? true : undefined,
    singleAllowed: searchParams.get('singleAllowed') === 'true' ? true : undefined,
    waterAvailability: searchParams.get('waterAvailability') === 'true' ? true : undefined,
    status: searchParams.get('status') || 'AVAILABLE',
    page: parseInt(searchParams.get('page') || '0', 10),
    size: 9,
    sortBy: searchParams.get('sortBy') || 'createdAt',
    sortDir: searchParams.get('sortDir') || 'desc',
  });

  const [properties, setProperties] = useState([]);
  const [pageData, setPageData] = useState({
    totalElements: 0,
    totalPages: 0,
    pageNumber: 0,
    isLast: true,
  });
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');
  const [searchInput, setSearchInput] = useState(filters.city || filters.query || '');

  useEffect(() => {
    const fetchProperties = async () => {
      setLoading(true);
      setError('');
      try {
        const data = await propertyService.getProperties(filters);
        setProperties(data.content || []);
        setPageData({
          totalElements: data.totalElements,
          totalPages: data.totalPages,
          pageNumber: data.pageNumber,
          isLast: data.isLast,
        });
      } catch (err) {
        setError('Failed to load rental properties. Make sure the backend server is running.');
      } finally {
        setLoading(false);
      }
    };

    fetchProperties();

    // Sync non-empty filters with URL params
    const params = {};
    Object.keys(filters).forEach((key) => {
      if (filters[key] !== '' && filters[key] !== undefined && filters[key] !== false) {
        params[key] = filters[key];
      }
    });
    setSearchParams(params, { replace: true });
  }, [filters]);

  const handleFilterChange = (newFilters) => {
    setFilters((prev) => ({
      ...prev,
      ...newFilters,
    }));
  };

  const handleSearchSubmit = (e) => {
    e.preventDefault();
    setFilters((prev) => ({
      ...prev,
      query: searchInput.trim(),
      city: '',
      page: 0,
    }));
  };

  const handleResetFilters = () => {
    setSearchInput('');
    setFilters({
      query: '',
      city: '',
      bhkType: '',
      propertyType: '',
      minRent: '',
      maxRent: '',
      familyAllowed: undefined,
      singleAllowed: undefined,
      waterAvailability: undefined,
      status: 'AVAILABLE',
      page: 0,
      size: 9,
      sortBy: 'createdAt',
      sortDir: 'desc',
    });
  };

  return (
    <main className="container listings-container">
      {/* Top Search & Filter Bar */}
      <section className="search-header-section">
        <div className="search-header-content">
          <h1>Find Rental Homes in India</h1>
          <p className="search-subtitle">
            Explore 1, 2, 3+ BHK flats in Gated Communities and Individual Houses with transparent charges.
          </p>

          <form onSubmit={handleSearchSubmit} className="search-bar-form">
            <input
              type="text"
              placeholder="Search by city (e.g. Bengaluru, Hyderabad, Mumbai) or location..."
              value={searchInput}
              onChange={(e) => setSearchInput(e.target.value)}
              className="main-search-input"
            />
            <button type="submit" className="primary-button search-submit-btn">
              🔍 Search
            </button>
          </form>
        </div>
      </section>

      {/* Main Content: Filters Sidebar + Results Grid */}
      <div className="listings-layout">
        <PropertyFilters
          filters={filters}
          onFilterChange={handleFilterChange}
          onReset={handleResetFilters}
        />

        <section className="listings-results-pane">
          <div className="results-toolbar">
            <div className="results-count">
              {loading ? (
                'Loading properties…'
              ) : (
                <span>
                  Showing <strong>{properties.length}</strong> of{' '}
                  <strong>{pageData.totalElements}</strong> rental properties
                </span>
              )}
            </div>

            <div className="sort-controls">
              <label htmlFor="sort-select">Sort By:</label>
              <select
                id="sort-select"
                value={`${filters.sortBy}-${filters.sortDir}`}
                onChange={(e) => {
                  const [sortBy, sortDir] = e.target.value.split('-');
                  handleFilterChange({ sortBy, sortDir, page: 0 });
                }}
                className="sort-dropdown"
              >
                <option value="createdAt-desc">Newest First</option>
                <option value="monthlyRent-asc">Rent: Low to High</option>
                <option value="monthlyRent-desc">Rent: High to Low</option>
              </select>
            </div>
          </div>

          {error && <div className="error-banner">{error}</div>}

          {loading ? (
            <div className="loading-state">
              <div className="spinner"></div>
              <p>Fetching properties from HomeLens…</p>
            </div>
          ) : properties.length === 0 ? (
            <div className="empty-state-card">
              <div className="empty-icon">🏠</div>
              <h3>No properties found matching your criteria</h3>
              <p>Try widening your rent range, clearing BHK filters, or searching another city.</p>
              <button onClick={handleResetFilters} className="primary-button">
                Clear Filters
              </button>
            </div>
          ) : (
            <div className="properties-grid">
              {properties.map((property) => (
                <PropertyCard key={property.id} property={property} />
              ))}
            </div>
          )}

          {/* Pagination */}
          {pageData.totalPages > 1 && (
            <nav className="pagination-bar" aria-label="Properties pagination">
              <button
                type="button"
                className="pagination-btn"
                disabled={filters.page === 0}
                onClick={() => handleFilterChange({ page: filters.page - 1 })}
              >
                ← Previous
              </button>
              <span className="pagination-info">
                Page {filters.page + 1} of {pageData.totalPages}
              </span>
              <button
                type="button"
                className="pagination-btn"
                disabled={pageData.isLast || filters.page >= pageData.totalPages - 1}
                onClick={() => handleFilterChange({ page: filters.page + 1 })}
              >
                Next →
              </button>
            </nav>
          )}
        </section>
      </div>
    </main>
  );
}
