-- V3__enhance_property_requirements.sql
-- HomeLens Rental Requirements Enhancement Migration

-- 1. Add BHK configuration and monthly rent (in INR)
ALTER TABLE properties ADD COLUMN IF NOT EXISTS bhk_type VARCHAR(50) DEFAULT '2 BHK';
ALTER TABLE properties ADD COLUMN IF NOT EXISTS monthly_rent NUMERIC(15, 2) DEFAULT 0.00;

-- Backfill monthly_rent from existing price
UPDATE properties SET monthly_rent = price WHERE monthly_rent = 0.00 OR monthly_rent IS NULL;

-- 2. Add Occupancy requirements (Family / Single-person occupancy)
ALTER TABLE properties ADD COLUMN IF NOT EXISTS family_allowed BOOLEAN DEFAULT TRUE;
ALTER TABLE properties ADD COLUMN IF NOT EXISTS single_allowed BOOLEAN DEFAULT TRUE;

-- 3. Add Additional Monthly Charges (electricity and water, distinguished from base monthly rent)
ALTER TABLE properties ADD COLUMN IF NOT EXISTS electricity_charges NUMERIC(10, 2) DEFAULT 0.00;
ALTER TABLE properties ADD COLUMN IF NOT EXISTS water_charges NUMERIC(10, 2) DEFAULT 0.00;

-- 4. Add Water Availability flag (Yes / No)
ALTER TABLE properties ADD COLUMN IF NOT EXISTS water_availability BOOLEAN DEFAULT TRUE;

-- 5. Add Nearby Facilities approximate distances (in kilometres)
ALTER TABLE properties ADD COLUMN IF NOT EXISTS distance_hospital_km NUMERIC(5, 2);
ALTER TABLE properties ADD COLUMN IF NOT EXISTS distance_college_km NUMERIC(5, 2);
ALTER TABLE properties ADD COLUMN IF NOT EXISTS distance_school_km NUMERIC(5, 2);
ALTER TABLE properties ADD COLUMN IF NOT EXISTS distance_metro_km NUMERIC(5, 2);
ALTER TABLE properties ADD COLUMN IF NOT EXISTS distance_bus_stop_km NUMERIC(5, 2);

-- 6. Update seed data with realistic rental values, property types, BHK, and facility distances
UPDATE properties SET
    property_type = 'GATED_COMMUNITY',
    bhk_type = '3 BHK',
    monthly_rent = 65000.00,
    family_allowed = TRUE,
    single_allowed = TRUE,
    electricity_charges = 3000.00,
    water_charges = 1000.00,
    water_availability = TRUE,
    distance_hospital_km = 1.80,
    distance_college_km = 3.50,
    distance_school_km = 1.20,
    distance_metro_km = 0.80,
    distance_bus_stop_km = 0.30
WHERE id = 1;

UPDATE properties SET
    property_type = 'INDIVIDUAL_HOUSE',
    bhk_type = '4 BHK',
    monthly_rent = 45000.00,
    family_allowed = TRUE,
    single_allowed = FALSE,
    electricity_charges = 2500.00,
    water_charges = 800.00,
    water_availability = TRUE,
    distance_hospital_km = 2.50,
    distance_college_km = 4.00,
    distance_school_km = 0.90,
    distance_metro_km = 2.10,
    distance_bus_stop_km = 0.50
WHERE id = 2;

UPDATE properties SET
    property_type = 'GATED_COMMUNITY',
    bhk_type = '3 BHK',
    monthly_rent = 85000.00,
    family_allowed = TRUE,
    single_allowed = FALSE,
    electricity_charges = 4500.00,
    water_charges = 1500.00,
    water_availability = TRUE,
    distance_hospital_km = 3.00,
    distance_college_km = 5.20,
    distance_school_km = 2.00,
    distance_metro_km = 1.50,
    distance_bus_stop_km = 0.40
WHERE id = 3;

UPDATE properties SET
    property_type = 'GATED_COMMUNITY',
    bhk_type = '2 BHK',
    monthly_rent = 35000.00,
    family_allowed = TRUE,
    single_allowed = TRUE,
    electricity_charges = 2000.00,
    water_charges = 600.00,
    water_availability = TRUE,
    distance_hospital_km = 1.20,
    distance_college_km = 2.00,
    distance_school_km = 1.50,
    distance_metro_km = 0.40,
    distance_bus_stop_km = 0.20
WHERE id = 4;

UPDATE properties SET
    property_type = 'GATED_COMMUNITY',
    bhk_type = '3 BHK',
    monthly_rent = 42000.00,
    family_allowed = TRUE,
    single_allowed = TRUE,
    electricity_charges = 2200.00,
    water_charges = 700.00,
    water_availability = TRUE,
    distance_hospital_km = 2.00,
    distance_college_km = 3.10,
    distance_school_km = 0.80,
    distance_metro_km = 1.00,
    distance_bus_stop_km = 0.30
WHERE id = 5;

UPDATE properties SET
    property_type = 'INDIVIDUAL_HOUSE',
    bhk_type = '1 BHK',
    monthly_rent = 18000.00,
    family_allowed = FALSE,
    single_allowed = TRUE,
    electricity_charges = 1200.00,
    water_charges = 400.00,
    water_availability = TRUE,
    distance_hospital_km = 1.00,
    distance_college_km = 1.50,
    distance_school_km = 1.10,
    distance_metro_km = 0.60,
    distance_bus_stop_km = 0.20
WHERE id = 6;

-- 7. Add filter and search indexes
CREATE INDEX IF NOT EXISTS idx_properties_bhk_type ON properties(bhk_type);
CREATE INDEX IF NOT EXISTS idx_properties_monthly_rent ON properties(monthly_rent);
CREATE INDEX IF NOT EXISTS idx_properties_water_avail ON properties(water_availability);
CREATE INDEX IF NOT EXISTS idx_properties_family_allowed ON properties(family_allowed);
CREATE INDEX IF NOT EXISTS idx_properties_single_allowed ON properties(single_allowed);
