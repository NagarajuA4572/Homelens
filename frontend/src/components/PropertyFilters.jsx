export default function PropertyFilters({ filters, onFilterChange, onReset }) {
  const bhkOptions = ['All', '1 RK', '1 BHK', '2 BHK', '3 BHK', '4 BHK', '4+ BHK'];

  const handleBhkClick = (bhk) => {
    onFilterChange({ bhkType: bhk === 'All' ? '' : bhk, page: 0 });
  };

  const handlePropertyTypeChange = (val) => {
    onFilterChange({ propertyType: val === 'ALL' ? '' : val, page: 0 });
  };

  const handleOccupancyChange = (type, checked) => {
    onFilterChange({ [type]: checked ? true : undefined, page: 0 });
  };

  const handleWaterAvailabilityChange = (checked) => {
    onFilterChange({ waterAvailability: checked ? true : undefined, page: 0 });
  };

  const handleStatusChange = (availableOnly) => {
    onFilterChange({ status: availableOnly ? 'AVAILABLE' : '', page: 0 });
  };

  return (
    <aside className="filters-sidebar">
      <div className="filters-header">
        <h3>Filter Properties</h3>
        <button type="button" onClick={onReset} className="reset-filters-btn">
          Reset All
        </button>
      </div>

      {/* 1. BHK Configuration */}
      <div className="filter-group">
        <label className="filter-label">BHK Configuration</label>
        <div className="bhk-pill-group">
          {bhkOptions.map((bhk) => {
            const isSelected = (!filters.bhkType && bhk === 'All') || filters.bhkType === bhk;
            return (
              <button
                key={bhk}
                type="button"
                className={`bhk-filter-btn ${isSelected ? 'selected' : ''}`}
                onClick={() => handleBhkClick(bhk)}
              >
                {bhk}
              </button>
            );
          })}
        </div>
      </div>

      {/* 2. Monthly Rent Range (in INR) */}
      <div className="filter-group">
        <label className="filter-label">Monthly Rent (₹)</label>
        <div className="price-inputs-row">
          <input
            type="number"
            placeholder="Min ₹"
            value={filters.minRent || ''}
            onChange={(e) => onFilterChange({ minRent: e.target.value, page: 0 })}
            className="price-input"
          />
          <span className="price-dash">—</span>
          <input
            type="number"
            placeholder="Max ₹"
            value={filters.maxRent || ''}
            onChange={(e) => onFilterChange({ maxRent: e.target.value, page: 0 })}
            className="price-input"
          />
        </div>
        <div className="rent-presets">
          <button
            type="button"
            className="rent-preset-btn"
            onClick={() => onFilterChange({ minRent: '', maxRent: '25000', page: 0 })}
          >
            &lt; ₹25k
          </button>
          <button
            type="button"
            className="rent-preset-btn"
            onClick={() => onFilterChange({ minRent: '25000', maxRent: '50000', page: 0 })}
          >
            ₹25k - ₹50k
          </button>
          <button
            type="button"
            className="rent-preset-btn"
            onClick={() => onFilterChange({ minRent: '50000', maxRent: '100000', page: 0 })}
          >
            ₹50k - ₹1L
          </button>
          <button
            type="button"
            className="rent-preset-btn"
            onClick={() => onFilterChange({ minRent: '100000', maxRent: '', page: 0 })}
          >
            &gt; ₹1L
          </button>
        </div>
      </div>

      {/* 3. Property Type */}
      <div className="filter-group">
        <label className="filter-label">Property Type</label>
        <div className="radio-options-column">
          <label className="radio-item">
            <input
              type="radio"
              name="propertyType"
              value="ALL"
              checked={!filters.propertyType}
              onChange={() => handlePropertyTypeChange('ALL')}
            />
            <span>All Types</span>
          </label>
          <label className="radio-item">
            <input
              type="radio"
              name="propertyType"
              value="GATED_COMMUNITY"
              checked={filters.propertyType === 'GATED_COMMUNITY'}
              onChange={() => handlePropertyTypeChange('GATED_COMMUNITY')}
            />
            <span>🛡️ Gated Community</span>
          </label>
          <label className="radio-item">
            <input
              type="radio"
              name="propertyType"
              value="INDIVIDUAL_HOUSE"
              checked={filters.propertyType === 'INDIVIDUAL_HOUSE'}
              onChange={() => handlePropertyTypeChange('INDIVIDUAL_HOUSE')}
            />
            <span>🏡 Individual House</span>
          </label>
        </div>
      </div>

      {/* 4. Occupancy Policy */}
      <div className="filter-group">
        <label className="filter-label">Occupancy Policy</label>
        <div className="checkbox-options-column">
          <label className="checkbox-item">
            <input
              type="checkbox"
              checked={!!filters.familyAllowed}
              onChange={(e) => handleOccupancyChange('familyAllowed', e.target.checked)}
            />
            <span>👨‍👩‍👧 Family Allowed</span>
          </label>
          <label className="checkbox-item">
            <input
              type="checkbox"
              checked={!!filters.singleAllowed}
              onChange={(e) => handleOccupancyChange('singleAllowed', e.target.checked)}
            />
            <span>👤 Single / Bachelor Allowed</span>
          </label>
        </div>
      </div>

      {/* 5. Water Availability */}
      <div className="filter-group">
        <label className="filter-label">Water Availability</label>
        <label className="checkbox-item highlighted">
          <input
            type="checkbox"
            checked={!!filters.waterAvailability}
            onChange={(e) => handleWaterAvailabilityChange(e.target.checked)}
          />
          <span>💧 Guaranteed Water Supply</span>
        </label>
      </div>

      {/* 6. Availability Status */}
      <div className="filter-group">
        <label className="filter-label">Availability</label>
        <label className="checkbox-item">
          <input
            type="checkbox"
            checked={filters.status === 'AVAILABLE' || !filters.status}
            onChange={(e) => handleStatusChange(e.target.checked)}
          />
          <span>Show Available Only</span>
        </label>
      </div>
    </aside>
  );
}
