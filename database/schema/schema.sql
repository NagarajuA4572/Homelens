-- HomeLens Database Schema Reference
-- Includes V1 Baseline + V3 Rental Enhancements

CREATE TABLE IF NOT EXISTS users (
    id BIGSERIAL PRIMARY KEY,
    email VARCHAR(255) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    full_name VARCHAR(255) NOT NULL,
    phone VARCHAR(50),
    role VARCHAR(50) NOT NULL DEFAULT 'ROLE_BUYER', -- ROLE_BUYER, ROLE_OWNER, ROLE_AGENT, ROLE_ADMIN
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS properties (
    id BIGSERIAL PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    description TEXT,
    price NUMERIC(15, 2) NOT NULL,
    monthly_rent NUMERIC(15, 2) DEFAULT 0.00,
    bhk_type VARCHAR(50) DEFAULT '2 BHK', -- 1 RK, 1 BHK, 2 BHK, 3 BHK, 4 BHK, etc.
    property_type VARCHAR(50) NOT NULL,   -- GATED_COMMUNITY, INDIVIDUAL_HOUSE, APARTMENT, etc.
    listing_type VARCHAR(50) NOT NULL DEFAULT 'FOR_RENT', -- FOR_RENT, FOR_SALE
    status VARCHAR(50) NOT NULL DEFAULT 'AVAILABLE',      -- AVAILABLE, RENTED, UNDER_CONTRACT, SOLD
    bedrooms INT DEFAULT 0,
    bathrooms INT DEFAULT 0,
    area_sqft NUMERIC(10, 2) DEFAULT 0,
    family_allowed BOOLEAN DEFAULT TRUE,
    single_allowed BOOLEAN DEFAULT TRUE,
    electricity_charges NUMERIC(10, 2) DEFAULT 0.00,
    water_charges NUMERIC(10, 2) DEFAULT 0.00,
    water_availability BOOLEAN DEFAULT TRUE,
    distance_hospital_km NUMERIC(5, 2),
    distance_college_km NUMERIC(5, 2),
    distance_school_km NUMERIC(5, 2),
    distance_metro_km NUMERIC(5, 2),
    distance_bus_stop_km NUMERIC(5, 2),
    address VARCHAR(255) NOT NULL,
    city VARCHAR(100) NOT NULL,
    state VARCHAR(100) NOT NULL,
    postal_code VARCHAR(20),
    latitude DOUBLE PRECISION,
    longitude DOUBLE PRECISION,
    agent_id BIGINT REFERENCES users(id) ON DELETE SET NULL, -- References property owner / agent
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS property_images (
    id BIGSERIAL PRIMARY KEY,
    property_id BIGINT NOT NULL REFERENCES properties(id) ON DELETE CASCADE,
    image_url VARCHAR(500) NOT NULL,
    is_primary BOOLEAN DEFAULT FALSE,
    display_order INT DEFAULT 0,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS inquiries (
    id BIGSERIAL PRIMARY KEY,
    property_id BIGINT NOT NULL REFERENCES properties(id) ON DELETE CASCADE,
    user_id BIGINT REFERENCES users(id) ON DELETE SET NULL,
    name VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL,
    phone VARCHAR(50),
    message TEXT NOT NULL,
    status VARCHAR(50) DEFAULT 'NEW',
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

-- Search and filter indexes
CREATE INDEX IF NOT EXISTS idx_properties_city ON properties(city);
CREATE INDEX IF NOT EXISTS idx_properties_price ON properties(price);
CREATE INDEX IF NOT EXISTS idx_properties_monthly_rent ON properties(monthly_rent);
CREATE INDEX IF NOT EXISTS idx_properties_bhk_type ON properties(bhk_type);
CREATE INDEX IF NOT EXISTS idx_properties_type ON properties(property_type);
CREATE INDEX IF NOT EXISTS idx_properties_listing_type ON properties(listing_type);
CREATE INDEX IF NOT EXISTS idx_properties_status ON properties(status);
CREATE INDEX IF NOT EXISTS idx_properties_water_avail ON properties(water_availability);
CREATE INDEX IF NOT EXISTS idx_properties_family_allowed ON properties(family_allowed);
CREATE INDEX IF NOT EXISTS idx_properties_single_allowed ON properties(single_allowed);
CREATE INDEX IF NOT EXISTS idx_properties_agent ON properties(agent_id);
CREATE INDEX IF NOT EXISTS idx_property_images_property ON property_images(property_id);
CREATE INDEX IF NOT EXISTS idx_inquiries_property ON inquiries(property_id);
